package wd40.lubricant.api.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Registers {@link CreativeModeTab}s under one mod's namespace. The {@code
 * configure} consumer receives a fresh {@link CreativeModeTab.Builder} - set
 * title, icon, and display items there.
 *
 * <p>Plain data - see {@link ItemRegistry} for the lifecycle. Loader entry
 * points read {@link #ALL} and commit to the creative-mode-tab registry.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/CreativeTabs">CreativeTabs wiki</a></p>
 */
public final class CreativeTabRegistry {

    public static final List<CreativeTabRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry> entries = new ArrayList<>();

    public record Entry(String path, Consumer<CreativeModeTab.Builder> configure, AtomicReference<CreativeModeTab> ref) {}

    public static CreativeTabRegistry create(String modId) {
        CreativeTabRegistry registry = new CreativeTabRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private CreativeTabRegistry(String modId) {
        this.modId = modId;
    }

    public Supplier<CreativeModeTab> register(String path, Consumer<CreativeModeTab.Builder> configure) {
        Entry entry = new Entry(path, configure, new AtomicReference<>());
        entries.add(entry);
        return () -> {
            CreativeModeTab tab = entry.ref.get();
            if (tab == null) {
                throw new IllegalStateException(
                        "CreativeModeTab " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return tab;
        };
    }

    public String modId() {
        return modId;
    }

    public List<Entry> entries() {
        return Collections.unmodifiableList(entries);
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            out.add(ResourceLocation.fromNamespaceAndPath(modId, entry.path));
        }
        return out;
    }
}

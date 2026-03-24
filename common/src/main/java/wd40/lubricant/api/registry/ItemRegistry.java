package wd40.lubricant.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers {@link Item}s under one mod's namespace. Plain data - no SPI lookup
 * during {@code create} or {@code register}. The loader entry point reads
 * {@link #ALL} and commits each {@link Entry} into the loader's registry pipeline.
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Items">Items wiki</a></p>
 */
public final class ItemRegistry {

    /** Every registry instance ever created in this JVM, in creation order. */
    public static final List<ItemRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry> entries = new ArrayList<>();

    /** One queued registration. The loader sets {@code ref} once the Item exists. */
    public record Entry(String path, Function<Item.Properties, Item> factory, AtomicReference<Item> ref) {}

    public static ItemRegistry create(String modId) {
        ItemRegistry registry = new ItemRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private ItemRegistry(String modId) {
        this.modId = modId;
    }

    /** Calling {@code .get()} on the returned supplier before the loader binds throws. */
    public Supplier<Item> register(String path, Function<Item.Properties, Item> factory) {
        Entry entry = new Entry(path, factory, new AtomicReference<>());
        entries.add(entry);
        return () -> {
            Item item = entry.ref.get();
            if (item == null) {
                throw new IllegalStateException(
                        "Item " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return item;
        };
    }

    public String modId() {
        return modId;
    }

    /** Read-only view of the queued registrations. Loader iterates and writes through {@code Entry.ref()}. */
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

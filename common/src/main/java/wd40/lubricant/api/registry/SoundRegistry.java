package wd40.lubricant.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Registers {@link SoundEvent}s under one mod's namespace. Each sound becomes a
 * variable-range event ({@link SoundEvent#createVariableRangeEvent}), matching
 * the vanilla default.
 *
 * <p>Plain data - see {@link ItemRegistry} for the lifecycle. Loader entry
 * points read {@link #ALL} and commit to the sound-event registry.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Sounds">Sounds wiki</a></p>
 */
public final class SoundRegistry {

    public static final List<SoundRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry> entries = new ArrayList<>();

    public record Entry(String path, AtomicReference<SoundEvent> ref) {}

    public static SoundRegistry create(String modId) {
        SoundRegistry registry = new SoundRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private SoundRegistry(String modId) {
        this.modId = modId;
    }

    public Supplier<SoundEvent> register(String path) {
        Entry entry = new Entry(path, new AtomicReference<>());
        entries.add(entry);
        return () -> {
            SoundEvent sound = entry.ref.get();
            if (sound == null) {
                throw new IllegalStateException(
                        "SoundEvent " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return sound;
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

package wd40.lubricant.api.particle;
import wd40.lubricant.api.registry.RegistrySupplier;

import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Registers {@link SimpleParticleType}s under one mod's namespace. The {@code
 * overrideLimiter} flag matches the vanilla constructor argument: {@code true}
 * lets the particle ignore the client-side particle render distance limit
 * (useful for status effects that must always be visible).
 *
 * <p>Plain data - see {@link ItemRegistry} for the lifecycle. Loader entry
 * points read {@link #ALL} and commit to the particle-type registry.</p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/Particles">Particles wiki</a></p>
 */
public final class ParticleRegistry {

    public static final List<ParticleRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry> entries = new ArrayList<>();

    public record Entry(String path, boolean overrideLimiter, AtomicReference<SimpleParticleType> ref) {}

    public static ParticleRegistry create(String modId) {
        ParticleRegistry registry = new ParticleRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private ParticleRegistry(String modId) {
        this.modId = modId;
    }

    public Supplier<SimpleParticleType> register(String path, boolean overrideLimiter) {
        Entry entry = new Entry(path, overrideLimiter, new AtomicReference<>());
        entries.add(entry);
        return () -> {
            SimpleParticleType type = entry.ref.get();
            if (type == null) {
                throw new IllegalStateException(
                        "ParticleType " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return type;
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

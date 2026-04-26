package wd40.lubricant.api.entity;
import wd40.lubricant.api.registry.RegistrySupplier;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

/**
 * Registers {@link EntityType}s under one mod's namespace. Plain data - see
 * {@link ItemRegistry} for the lifecycle. Loader entry points read
 * {@link #ALL} and commit to vanilla / NeoForge registries.
 *
 * <p>The {@code builder} is the standard {@link EntityType.Builder} pre-configured
 * with category, size, factory, and any other fluent options. Lubricant calls
 * {@code .build(id)} at bind time using the modId + path.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Entities">Entities wiki</a></p>
 */
public final class EntityRegistry {

    public static final List<EntityRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry<? extends Entity>> entries = new ArrayList<>();

    /** One queued registration. Loader sets {@code ref} once the type exists. */
    public record Entry<T extends Entity>(
            String path,
            EntityType.Builder<T> builder,
            AtomicReference<EntityType<T>> ref) {}

    public static EntityRegistry create(String modId) {
        EntityRegistry registry = new EntityRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private EntityRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Queue an entity-type registration.
     *
     * @param path     path under the mod's namespace, e.g. {@code "spinning_cog"}
     * @param builder  pre-configured {@link EntityType.Builder}
     */
    public <T extends Entity> Supplier<EntityType<T>> register(
            String path,
            EntityType.Builder<T> builder) {
        Entry<T> entry = new Entry<>(path, builder, new AtomicReference<>());
        entries.add(entry);
        return () -> {
            EntityType<T> type = entry.ref.get();
            if (type == null) {
                throw new IllegalStateException(
                        "EntityType " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return type;
        };
    }

    public String modId() {
        return modId;
    }

    public List<Entry<? extends Entity>> entries() {
        return Collections.unmodifiableList(entries);
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry<?> entry : entries) {
            out.add(ResourceLocation.fromNamespaceAndPath(modId, entry.path()));
        }
        return out;
    }
}

package wd40.lubricant.api.common.registry;

import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

/**
 * Handle returned by {@link ItemRegistry#register}, {@link BlockRegistry#register},
 * and the other content registries. Wraps the eventual registered value plus
 * the registration {@link #id}.
 *
 * <p>The value is constructed lazily during the loader's binding phase
 * (Fabric: tail of {@code Entry#onInitialize}; NeoForge: inside the
 * {@code RegisterEvent} for the target registry). Calling {@link #get} before
 * binding completes returns {@code null}.</p>
 *
 * <p>{@link #id} is available immediately - useful for referencing the
 * registration in JSON ids, datapack files, recipe builders, or vanilla APIs
 * that take a {@link ResourceLocation} (e.g. {@code FlowerPotBlock.addPlant})
 * without forcing the value to resolve.</p>
 *
 * <p>Extends {@link Supplier} so any consumer that just wants the value
 * accepts a {@code RegistrySupplier} polymorphically.</p>
 */
public interface RegistrySupplier<T> extends Supplier<T> {

    /** Resource location under the owning mod's namespace. Always non-null and ready immediately. */
    ResourceLocation id();
}

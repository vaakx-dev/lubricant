package wd40.lubricant.internal;

import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.ItemRegistry;

/**
 * Loader-specific factory for {@link ItemRegistry} and {@link BlockRegistry}
 * instances. One implementation per loader, discovered via JDK
 * {@link java.util.ServiceLoader}.
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.FabricRegistryHelper}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.NeoForgeRegistryHelper}</li>
 * </ul>
 *
 * <p>Each implementation tracks the registries it creates in static lists, so
 * the loader entry points can iterate them at the right lifecycle moment to
 * commit the queued {@code register} calls to the underlying loader API.</p>
 */
public interface RegistryHelper {

    /** Creates a per-mod {@link ItemRegistry}. The impl tracks it for batch-bind. */
    ItemRegistry createItemRegistry(String modId);

    /** Creates a per-mod {@link BlockRegistry}. The impl tracks it for batch-bind. */
    BlockRegistry createBlockRegistry(String modId);
}

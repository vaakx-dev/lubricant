package wd40.lubricant.internal;

import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.ItemRegistry;

// Service interface implemented once per loader. Each loader's META-INF/services file
// points at its own implementation:
//   fabric:   wd40.lubricant.fabric.FabricRegistryHelper
//   neoforge: wd40.lubricant.neoforge.NeoForgeRegistryHelper
//
public interface RegistryHelper {

    ItemRegistry createItemRegistry(String modId);

    BlockRegistry createBlockRegistry(String modId);
}

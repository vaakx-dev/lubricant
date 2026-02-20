package wd40.lubricant.internal;

import wd40.lubricant.api.BlockRegistry;
import wd40.lubricant.api.ItemRegistry;

// Service interface implemented once per loader. Each loader's META-INF/services file
// points at its own implementation:
//   fabric:   wd40.lubricant.fabric.FabricRegistryHelper
//   neoforge: wd40.lubricant.neoforge.NeoForgeRegistryHelper
//
public interface RegistryHelper {

    ItemRegistry createItemRegistry(String modId);

    BlockRegistry createBlockRegistry(String modId);
}

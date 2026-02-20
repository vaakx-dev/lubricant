package wd40.lubricant.fabric;

import wd40.lubricant.api.BlockRegistry;
import wd40.lubricant.api.ItemRegistry;
import wd40.lubricant.internal.RegistryHelper;

import java.util.ArrayList;
import java.util.List;

// Fabric loader's RegistryHelper. Tracks every registry it creates so LubricantFabric
// can call bind() on each at the right moment.
public final class FabricRegistryHelper implements RegistryHelper {

    static final List<FabricItemRegistry>  ALL_ITEMS  = new ArrayList<>();
    static final List<FabricBlockRegistry> ALL_BLOCKS = new ArrayList<>();

    @Override
    public ItemRegistry createItemRegistry(String modId) {
        FabricItemRegistry r = new FabricItemRegistry(modId);
        ALL_ITEMS.add(r);
        return r;
    }

    @Override
    public BlockRegistry createBlockRegistry(String modId) {
        FabricBlockRegistry r = new FabricBlockRegistry(modId);
        ALL_BLOCKS.add(r);
        return r;
    }
}

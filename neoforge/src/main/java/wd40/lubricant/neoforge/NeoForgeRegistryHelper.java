package wd40.lubricant.neoforge;

import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.lubricant.internal.RegistryHelper;

import java.util.ArrayList;
import java.util.List;

public final class NeoForgeRegistryHelper implements RegistryHelper {

    static final List<NeoForgeItemRegistry>  ALL_ITEMS  = new ArrayList<>();
    static final List<NeoForgeBlockRegistry> ALL_BLOCKS = new ArrayList<>();

    @Override
    public ItemRegistry createItemRegistry(String modId) {
        NeoForgeItemRegistry r = new NeoForgeItemRegistry(modId);
        ALL_ITEMS.add(r);
        return r;
    }

    @Override
    public BlockRegistry createBlockRegistry(String modId) {
        NeoForgeBlockRegistry r = new NeoForgeBlockRegistry(modId);
        ALL_BLOCKS.add(r);
        return r;
    }
}

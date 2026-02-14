package wd40.lubricant.neoforge;

import wd40.lubricant.api.ItemRegistry;
import wd40.lubricant.internal.RegistryHelper;

import java.util.ArrayList;
import java.util.List;

public final class NeoForgeRegistryHelper implements RegistryHelper {

    static final List<NeoForgeItemRegistry> ALL_ITEMS = new ArrayList<>();

    @Override
    public ItemRegistry createItemRegistry(String modId) {
        NeoForgeItemRegistry r = new NeoForgeItemRegistry(modId);
        ALL_ITEMS.add(r);
        return r;
    }
}

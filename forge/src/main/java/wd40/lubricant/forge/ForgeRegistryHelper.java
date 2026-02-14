package wd40.lubricant.forge;

import wd40.lubricant.api.ItemRegistry;
import wd40.lubricant.internal.RegistryHelper;

import java.util.ArrayList;
import java.util.List;

public final class ForgeRegistryHelper implements RegistryHelper {

    static final List<ForgeItemRegistry> ALL_ITEMS = new ArrayList<>();

    @Override
    public ItemRegistry createItemRegistry(String modId) {
        ForgeItemRegistry r = new ForgeItemRegistry(modId);
        ALL_ITEMS.add(r);
        return r;
    }
}

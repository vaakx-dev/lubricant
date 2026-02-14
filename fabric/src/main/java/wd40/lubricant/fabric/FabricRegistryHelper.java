package wd40.lubricant.fabric;

import wd40.lubricant.api.ItemRegistry;
import wd40.lubricant.internal.RegistryHelper;

import java.util.ArrayList;
import java.util.List;

// Fabric loader's RegistryHelper. Tracks every FabricItemRegistry it creates so
// LubricantFabric can call bind() on each at the right moment.
public final class FabricRegistryHelper implements RegistryHelper {

    static final List<FabricItemRegistry> ALL_ITEMS = new ArrayList<>();

    @Override
    public ItemRegistry createItemRegistry(String modId) {
        FabricItemRegistry r = new FabricItemRegistry(modId);
        ALL_ITEMS.add(r);
        return r;
    }
}

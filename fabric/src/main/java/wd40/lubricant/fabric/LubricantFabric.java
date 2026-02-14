package wd40.lubricant.fabric;

import net.fabricmc.api.ModInitializer;
import wd40.lubricant.internal.Bootstrap;

public final class LubricantFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // Force-load every consuming mod's Init class. Their static init queues
        // ItemRegistry.create() + register() calls into FabricRegistryHelper.ALL_ITEMS.
        Bootstrap.loadAllInit();

        // Now bind every queued registry - registers the items to vanilla's registries.
        for (FabricItemRegistry r : FabricRegistryHelper.ALL_ITEMS) {
            r.bind();
        }
    }
}

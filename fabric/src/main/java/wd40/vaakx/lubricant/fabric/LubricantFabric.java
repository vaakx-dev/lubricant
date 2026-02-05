package wd40.vaakx.lubricant.fabric;

import net.fabricmc.api.ModInitializer;
import wd40.vaakx.lubricant.Lubricant;

public final class LubricantFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // Phase 6 will register Fabric-specific Service implementations here.
        System.out.println("[" + Lubricant.MOD_ID + "] loaded on Fabric");
    }
}

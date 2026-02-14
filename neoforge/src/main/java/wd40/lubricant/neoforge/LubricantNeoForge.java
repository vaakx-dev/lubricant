package wd40.lubricant.neoforge;

import net.neoforged.fml.common.Mod;
import wd40.lubricant.Lubricant;

@Mod(Lubricant.MOD_ID)
public final class LubricantNeoForge {

    public LubricantNeoForge() {
        // Phase 6 will register NeoForge-specific Service implementations here.
        System.out.println("[" + Lubricant.MOD_ID + "] loaded on NeoForge");
    }
}

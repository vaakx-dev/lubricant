package wd40.lubricant.forge;

import net.minecraftforge.fml.common.Mod;
import wd40.lubricant.Lubricant;

@Mod(Lubricant.MOD_ID)
public final class LubricantForge {

    public LubricantForge() {
        // Phase 6 will register Forge-specific Service implementations here.
        System.out.println("[" + Lubricant.MOD_ID + "] loaded on Forge");
    }
}

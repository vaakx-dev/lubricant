package wd40.lubricant.forge;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import wd40.lubricant.internal.Bootstrap;

@Mod("lubricant")
public final class LubricantForge {

    public LubricantForge() {
        // Force-load every mod's Init class - fills ForgeRegistryHelper.ALL_ITEMS.
        Bootstrap.loadAllInit();

        // For each queued ForgeItemRegistry, find its mod's event bus and attach.
        // Forge's ModContainer is abstract; the concrete FMLModContainer exposes getEventBus().
        for (ForgeItemRegistry r : ForgeRegistryHelper.ALL_ITEMS) {
            ModContainer container = ModList.get().getModContainerById(r.modId)
                    .orElseThrow(() -> new IllegalStateException(
                            "lubricant: no Forge mod container for modId=" + r.modId));
            if (!(container instanceof FMLModContainer fml)) {
                throw new IllegalStateException(
                        "lubricant: " + r.modId + " is not a Java mod (FMLModContainer expected)");
            }
            r.attach(fml.getEventBus());
        }
    }
}

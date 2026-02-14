package wd40.lubricant.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import wd40.lubricant.internal.Bootstrap;

@Mod("lubricant")
public final class LubricantNeoForge {

    public LubricantNeoForge(IEventBus lubricantBus) {
        // Force-load every mod's Init class - fills NeoForgeRegistryHelper.ALL_ITEMS.
        Bootstrap.loadAllInit();

        // For each queued NeoForgeItemRegistry, find that mod's event bus and attach.
        // NeoForge's ModContainer.getEventBus() is public, so no reflection needed.
        for (NeoForgeItemRegistry r : NeoForgeRegistryHelper.ALL_ITEMS) {
            IEventBus bus;
            if ("lubricant".equals(r.modId)) {
                bus = lubricantBus;
            } else {
                ModContainer container = ModList.get().getModContainerById(r.modId)
                        .orElseThrow(() -> new IllegalStateException(
                                "lubricant: no NeoForge mod container for modId=" + r.modId));
                bus = container.getEventBus();
                if (bus == null) {
                    throw new IllegalStateException(
                            "lubricant: mod " + r.modId + " has no event bus yet");
                }
            }
            r.attach(bus);
        }
    }
}

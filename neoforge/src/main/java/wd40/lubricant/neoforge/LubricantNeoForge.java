package wd40.lubricant.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import wd40.lubricant.internal.Bootstrap;

@Mod("lubricant")
public final class LubricantNeoForge {

    public LubricantNeoForge(IEventBus lubricantBus) {
        // Force-load every mod's Init class - fills the ALL_X lists in NeoForgeRegistryHelper.
        Bootstrap.loadAllInit();

        // For each queued registry, find that mod's event bus and attach the
        // underlying DeferredRegister to it. NeoForge's ModContainer.getEventBus() is
        // public, so no reflection needed.
        for (NeoForgeBlockRegistry r : NeoForgeRegistryHelper.ALL_BLOCKS) {
            r.attach(busFor(r.modId, lubricantBus));
        }
        for (NeoForgeItemRegistry r : NeoForgeRegistryHelper.ALL_ITEMS) {
            r.attach(busFor(r.modId, lubricantBus));
        }
    }

    private static IEventBus busFor(String modId, IEventBus lubricantBus) {
        if ("lubricant".equals(modId)) return lubricantBus;
        ModContainer container = ModList.get().getModContainerById(modId)
                .orElseThrow(() -> new IllegalStateException(
                        "lubricant: no NeoForge mod container for modId=" + modId));
        IEventBus bus = container.getEventBus();
        if (bus == null) {
            throw new IllegalStateException(
                    "lubricant: mod " + modId + " has no event bus yet");
        }
        return bus;
    }
}

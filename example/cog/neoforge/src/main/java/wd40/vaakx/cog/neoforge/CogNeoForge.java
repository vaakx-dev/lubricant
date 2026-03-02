package wd40.vaakx.cog.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import wd40.vaakx.cog.CogMod;

/**
 * Cog's NeoForge entry point.
 *
 * NeoForge discovers this class via {@code @Mod(CogMod.ID)} and instantiates
 * it during mod construction. The {@code IEventBus} parameter is cog's own
 * mod-event bus, used for registry events, capabilities, etc.
 *
 * <h3>Why this is empty</h3>
 *
 * Lubricant looks up cog's mod-event bus via {@code ModList.get().getModContainerById}
 * during its OWN @Mod constructor (which runs before cog's, because cog
 * declares lubricant as a dependency in neoforge.mods.toml). It attaches its
 * own DeferredRegisters to cog's bus at that point, so by the time cog's
 * constructor fires, the registry events are already wired.
 *
 * The {@code modBus} parameter is unused in this minimal example. Real mods
 * would subscribe their own listeners (gui events, capabilities, networking
 * payload registration) here.
 */
@Mod(CogMod.ID)
public final class CogNeoForge {

    public CogNeoForge(IEventBus modBus) {
        // (intentionally empty - lubricant did the work)
    }
}

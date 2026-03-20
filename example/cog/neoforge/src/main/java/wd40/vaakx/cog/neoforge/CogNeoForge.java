package wd40.vaakx.cog.neoforge;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import wd40.vaakx.cog.Cog;

/**
 * Cog's NeoForge entry point. NeoForge discovers this class via
 * {@code @Mod(Cog.ID)} and instantiates it during mod construction. The
 * {@code IEventBus} parameter is cog's own mod-event bus.
 *
 * <h3>Why this is empty</h3>
 *
 * <p>Lubricant looks up cog's mod-event bus via
 * {@code ModList.get().getModContainerById("cog").getEventBus()} during its
 * own {@code @Mod} constructor (which runs first, because cog declares
 * lubricant as a dependency in {@code neoforge.mods.toml}). It attaches its
 * DeferredRegisters to that bus then, so by the time cog's constructor fires,
 * registry events are already wired.</p>
 *
 * <p>{@code modBus} is unused in this minimal example. Real mods can subscribe
 * NeoForge-specific listeners (capabilities, custom payloads if not using
 * lubricant's {@code Net}, GUI events) here.</p>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/How-It-Works">How
 * It Works</a> for the full pipeline.</p>
 */
@Mod(Cog.ID)
public final class CogNeoForge {

    public CogNeoForge(IEventBus modBus) {
        // (intentionally empty - lubricant did the work)
    }
}

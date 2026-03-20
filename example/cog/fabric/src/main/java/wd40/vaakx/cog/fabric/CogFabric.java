package wd40.vaakx.cog.fabric;

import net.fabricmc.api.ModInitializer;

/**
 * Cog's Fabric entry point. Referenced from {@code fabric.mod.json}'s
 * {@code entrypoints.main} array.
 *
 * <h3>Why this is empty</h3>
 *
 * <p>Lubricant handles cog's registration on cog's behalf. Lubricant's own
 * {@code ModInitializer} runs first (cog declares lubricant as a dependency),
 * and it:</p>
 * <ol>
 *   <li>ServiceLoader-finds every class implementing {@code wd40.lubricant.api.Init}
 *       (cog's {@code Items}, {@code Blocks}, {@code Listeners}, {@code Channel}).</li>
 *   <li>Triggers each class's static initializers, which call into lubricant's
 *       per-mod registries.</li>
 *   <li>Commits the queued registrations to vanilla / Fabric API at the right
 *       lifecycle moment.</li>
 * </ol>
 *
 * <p>By the time cog's own {@code onInitialize} fires, everything cog wanted
 * registered is registered. There's nothing for this method to do unless you
 * add Fabric-specific code (key bindings, custom renderers, etc).</p>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/How-It-Works">How
 * It Works</a> for the full pipeline.</p>
 */
public final class CogFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // (intentionally empty - lubricant did the work)
    }
}

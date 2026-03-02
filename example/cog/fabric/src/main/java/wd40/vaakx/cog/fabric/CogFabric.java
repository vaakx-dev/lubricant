package wd40.vaakx.cog.fabric;

import net.fabricmc.api.ModInitializer;

/**
 * Cog's Fabric entry point.
 *
 * Referenced from {@code fabric.mod.json}'s {@code entrypoints.main} array.
 * Fabric Loader instantiates this class and calls {@link #onInitialize()}
 * during MC client/server bootstrap.
 *
 * <h3>Why this is empty</h3>
 *
 * Lubricant handles all the registry registration on cog's behalf. When
 * lubricant's own ModInitializer runs (it's a separate mod, loaded before cog
 * because cog declares lubricant as a dependency), it:
 *   1. ServiceLoader-finds every class implementing {@code Init} (cog's Items
 *      and Blocks among them)
 *   2. Touches each one, triggering its static fields to run
 *   3. Iterates the queued registrations and commits them
 *
 * So by the time cog's own onInitialize fires, all of cog's items and blocks
 * are already registered. There's nothing for cog to do here unless you have
 * Fabric-specific code (event listeners, key bindings, client-only renderers,
 * etc.) - in which case put it here.
 */
public final class CogFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        // (intentionally empty - lubricant did the work)
    }
}

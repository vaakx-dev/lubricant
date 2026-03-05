package wd40.vaakx.cog.events;

import wd40.lubricant.api.Init;
import wd40.lubricant.api.events.Events;
import wd40.vaakx.cog.Cog;

/**
 * All of cog's event subscriptions. One holder class per behavior folder, named for
 * its role - this file <em>holds listeners</em>, hence {@code Listeners}.
 *
 * <h3>How lubricant picks this up</h3>
 *
 * The {@code implements Init} marker plus an entry in
 * {@code META-INF/services/wd40.lubricant.api.Init} tells lubricant to force-load
 * this class during its own startup. ServiceLoader instantiating the class triggers
 * the JVM to run its {@code <clinit>} - i.e. the static initializer block below.
 *
 * <h3>Why a static initializer instead of an init() method</h3>
 *
 * The {@link Init} marker has no methods - lubricant just needs the class to load.
 * Putting subscriptions in {@code static { ... }} means they happen once, exactly
 * when lubricant says "now". No bind/unbind dance, no lifecycle method to forget.
 *
 * <h3>The Events accessors</h3>
 *
 * {@link Events} exposes one static method per supported event (serverTick,
 * serverStart, playerJoin, etc.). Each returns an
 * {@link wd40.lubricant.api.events.Event Event<L>} - call {@code .subscribe(listener)}
 * with a lambda. Listener types reuse vanilla MC types directly, so signatures
 * match the documentation you already know ({@link net.minecraft.server.MinecraftServer},
 * {@link net.minecraft.server.level.ServerPlayer}, etc.).
 *
 * <h3>Threading</h3>
 *
 * Listeners run on the main game thread on both Fabric and NeoForge - lubricant
 * does the {@code enqueueWork} hop on NeoForge internally. Safe to mutate world
 * state, send packets, etc. without further synchronization.
 */
public final class Listeners implements Init {

    static {
        // Server has finished loading worlds and is about to accept connections.
        Events.serverStart().subscribe(server ->
                Cog.LOG.info("server starting: {}", server.getServerVersion()));

        // A player just finished login (entity already in the world).
        Events.playerJoin().subscribe(player ->
                Cog.LOG.info("player joined: {}", player.getScoreboardName()));

        // A player disconnected (any reason - quit, kick, timeout).
        Events.playerLeave().subscribe(player ->
                Cog.LOG.info("player left: {}", player.getScoreboardName()));
    }

    /** Public no-arg constructor required by ServiceLoader. Body intentionally empty. */
    public Listeners() {}
}

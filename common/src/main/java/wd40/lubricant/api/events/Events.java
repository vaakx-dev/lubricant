package wd40.lubricant.api.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.internal.Services;

import java.util.function.Consumer;

/**
 * Subscribe to game lifecycle events. Each accessor returns an {@link Event}
 * you call {@code .subscribe(listener)} on. Listeners run on the main game
 * thread on both Fabric and NeoForge.
 *
 * <h3>Usage</h3>
 *
 * <pre>{@code
 * public final class Listeners implements Init {
 *     static {
 *         Events.serverStart().subscribe(server -> ...);
 *         Events.playerJoin().subscribe(player -> ...);
 *         Events.serverTick().subscribe(server -> {
 *             if (server.getTickCount() % 20 == 0) {
 *                 // once per second
 *             }
 *         });
 *     }
 *     public Listeners() {}
 * }
 * }</pre>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/Events">the Events
 * wiki page</a> for full reference and threading details.</p>
 */
public final class Events {

    private Events() {}

    /** End of every server tick (20Hz). Listener: {@code Consumer<MinecraftServer>}. */
    public static Event<Consumer<MinecraftServer>> serverTick() {
        return Services.events().serverTick();
    }

    /** Server has loaded its worlds and is about to accept connections. */
    public static Event<Consumer<MinecraftServer>> serverStart() {
        return Services.events().serverStart();
    }

    /** Server is shutting down. Last chance to flush state. */
    public static Event<Consumer<MinecraftServer>> serverStop() {
        return Services.events().serverStop();
    }

    /** A player finished login. The {@link ServerPlayer} entity is already in the world. */
    public static Event<Consumer<ServerPlayer>> playerJoin() {
        return Services.events().playerJoin();
    }

    /** A player disconnected (any reason - quit, kick, timeout). */
    public static Event<Consumer<ServerPlayer>> playerLeave() {
        return Services.events().playerLeave();
    }

    /**
     * The command system is registering commands. Fires once per server lifetime
     * AND on {@code /reload} - keep registration logic idempotent.
     */
    public static Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands() {
        return Services.events().commands();
    }

    /**
     * A player right-clicks with an item. Listener returns an
     * {@link net.minecraft.world.InteractionResult} - {@code PASS} lets vanilla
     * continue, anything else short-circuits.
     */
    public static Event<ItemUseListener> itemUse() {
        return Services.events().itemUse();
    }
}

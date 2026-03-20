package wd40.lubricant.api.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.internal.Services;

import java.util.function.Consumer;

/**
 * Subscribe to game lifecycle events. Listeners run on the main game thread on
 * both loaders.
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Events">Events wiki</a></p>
 */
public final class Events {

    private Events() {}

    public static Event<Consumer<MinecraftServer>> serverTick() {
        return Services.events().serverTick();
    }

    public static Event<Consumer<MinecraftServer>> serverStart() {
        return Services.events().serverStart();
    }

    public static Event<Consumer<MinecraftServer>> serverStop() {
        return Services.events().serverStop();
    }

    public static Event<Consumer<ServerPlayer>> playerJoin() {
        return Services.events().playerJoin();
    }

    public static Event<Consumer<ServerPlayer>> playerLeave() {
        return Services.events().playerLeave();
    }

    /** Fires on every {@code /reload} - keep registration logic idempotent. */
    public static Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands() {
        return Services.events().commands();
    }

    public static Event<ItemUseListener> itemUse() {
        return Services.events().itemUse();
    }
}

package wd40.lubricant.api;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.internal.Services;

import java.util.function.Consumer;

// Public entry point for subscribing to game events. Each accessor returns an Event
// you call .subscribe(listener) on. Listener types reuse vanilla MC types directly -
// no wrapping - so signatures match the documentation you already know.
//
// Usage:
//   Events.serverStart().subscribe(server -> ...);
//   Events.itemUse().subscribe((player, level, hand) -> InteractionResult.PASS);
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

    public static Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands() {
        return Services.events().commands();
    }

    public static Event<ItemUseListener> itemUse() {
        return Services.events().itemUse();
    }
}

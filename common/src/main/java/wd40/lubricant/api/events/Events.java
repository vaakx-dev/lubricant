package wd40.lubricant.api.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

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
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverTick();
    }

    public static Event<Consumer<MinecraftServer>> serverStart() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverStart();
    }

    public static Event<Consumer<MinecraftServer>> serverStop() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverStop();
    }

    public static Event<Consumer<ServerPlayer>> playerJoin() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().playerJoin();
    }

    public static Event<Consumer<ServerPlayer>> playerLeave() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().playerLeave();
    }

    /** Fires on every {@code /reload} - keep registration logic idempotent. */
    public static Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().commands();
    }

    public static Event<ItemUseListener> itemUse() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().itemUse();
    }

    public static Event<EntityInteractListener> entityInteract() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().entityInteract();
    }

    /** One-shot post-registry hook. See {@link wd40.lubricant.core.events.EventHelper#setup()}. */
    public static Event<Runnable> setup() {
        return Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().setup();
    }
}

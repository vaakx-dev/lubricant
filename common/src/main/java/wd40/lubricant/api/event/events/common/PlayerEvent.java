package wd40.lubricant.api.event.events.common;

import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.api.event.Event;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;

import java.util.function.Consumer;

/**
 * Player connection events. Listeners receive a {@link ServerPlayer} on the
 * main server thread.
 */
public final class PlayerEvent {

    private PlayerEvent() {}

    public static final Event<Consumer<ServerPlayer>> JOIN =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().playerJoin();

    public static final Event<Consumer<ServerPlayer>> LEAVE =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().playerLeave();
}

package wd40.lubricant.api.server.events;

import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.api.common.events.Event;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

import java.util.function.Consumer;

/**
 * Player connection events. Listeners receive a {@link ServerPlayer} on the
 * main server thread.
 */
public final class PlayerEvents {

    private PlayerEvents() {}

    public static final Event<Consumer<ServerPlayer>> JOIN =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().playerJoin();

    public static final Event<Consumer<ServerPlayer>> LEAVE =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().playerLeave();
}

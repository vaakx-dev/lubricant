package wd40.lubricant.api.events;

import net.minecraft.server.MinecraftServer;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

import java.util.function.Consumer;

/**
 * Server-lifecycle events. Listeners run on the main server thread.
 *
 * <pre>{@code
 * ServerEvents.START.register(server -> LOG.info("starting"));
 * ServerEvents.TICK.register(server -> { ... });
 * }</pre>
 */
public final class ServerEvents {

    private ServerEvents() {}

    public static final Event<Consumer<MinecraftServer>> START =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverStart();

    public static final Event<Consumer<MinecraftServer>> STOP =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverStop();

    public static final Event<Consumer<MinecraftServer>> TICK =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverTick();
}

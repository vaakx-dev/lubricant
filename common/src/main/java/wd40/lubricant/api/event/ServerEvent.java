package wd40.lubricant.api.event;

import net.minecraft.server.MinecraftServer;
import wd40.lubricant.api.event.Event;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;

import java.util.function.Consumer;

/**
 * Server-lifecycle events. Listeners run on the main server thread.
 *
 * <pre>{@code
 * ServerEvent.START.register(server -> LOG.info("starting"));
 * ServerEvent.TICK.register(server -> { ... });
 * ServerEvent.SETUP.register(() -> FlowerPotBlock.addPlant(...));
 * }</pre>
 *
 * <p>{@link #SETUP} fires once after lubricant binds every registry on the
 * server side. Use for one-time wiring that depends on registered objects
 * existing.</p>
 */
public final class ServerEvent {

    private ServerEvent() {}

    public static final Event<Consumer<MinecraftServer>> START =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverStart();

    public static final Event<Consumer<MinecraftServer>> STOP =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverStop();

    public static final Event<Consumer<MinecraftServer>> TICK =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverTick();

    public static final Event<Runnable> SETUP =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().serverSetup();
}

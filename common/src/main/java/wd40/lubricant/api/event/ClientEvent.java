package wd40.lubricant.api.event;

import net.minecraft.client.Minecraft;
import wd40.lubricant.api.event.Event;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;
import wd40.lubricant.internal.rendering.RendererHelper;

import java.util.function.Consumer;

/**
 * Client-lifecycle events. Listeners run on the main client thread.
 *
 * <p>{@link #SETUP} fires once after lubricant binds every registry on the
 * client side. Listeners registered from a dedicated-server JVM never fire
 * (the client setup phase doesn't run there). Use for one-time client-side
 * wiring that depends on registered objects existing.</p>
 *
 * <p>{@link #TICK} fires every client tick (~20Hz). Same short-circuit
 * contract as the renderer facades - no-ops on dedicated server / datagen.</p>
 */
public final class ClientEvent {

    private ClientEvent() {}

    public static final Event<Runnable> SETUP =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().clientSetup();

    public static final Event<Consumer<Minecraft>> TICK = clientTick();

    private static Event<Consumer<Minecraft>> clientTick() {
        if (Datagen.IS_DATAGEN) return Datagen.noOpEvent();
        RendererHelper helper = Services.renderers();
        if (helper == null) return Datagen.noOpEvent();
        return helper.clientTick();
    }
}

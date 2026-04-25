package wd40.lubricant.api.client.events;

import wd40.lubricant.api.common.events.Event;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

/**
 * Client-lifecycle events. Listeners run on the main client thread.
 *
 * <p>{@link #SETUP} fires once after lubricant binds every registry on the
 * client side. Listeners registered from a dedicated-server JVM never fire
 * (the client setup phase doesn't run there). Use for one-time client-side
 * wiring that depends on registered objects existing.</p>
 */
public final class ClientEvents {

    private ClientEvents() {}

    public static final Event<Runnable> SETUP =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().clientSetup();
}

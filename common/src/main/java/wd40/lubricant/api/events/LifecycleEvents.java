package wd40.lubricant.api.events;

import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

/**
 * Mod-lifecycle events. {@link #SETUP} fires once after lubricant's loader
 * entry binds every registry. Use for one-time wiring that depends on
 * registered objects existing - e.g. calling
 * {@code FlowerPotBlock.addPlant(...)} after both blocks are real.
 * Listeners run on the main thread.
 */
public final class LifecycleEvents {

    private LifecycleEvents() {}

    public static final Event<Runnable> SETUP =
            Datagen.IS_DATAGEN ? Datagen.noOpEvent() : Services.events().setup();
}

package wd40.lubricant.api.event;

import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;
import wd40.lubricant.internal.rendering.RendererHelper;

/**
 * Client-side {@link net.minecraft.client.gui.screens.Screen} lifecycle events.
 * All listeners run on the main client thread.
 *
 * <ul>
 *   <li>{@link #OPEN} fires after a screen's {@code init()} completes - i.e.
 *       once per screen instance, after widgets are placed.</li>
 *   <li>{@link #CLOSE} fires when a screen is being removed (player closed it
 *       or vanilla replaced it).</li>
 *   <li>{@link #RENDER} fires every frame after the screen has drawn its
 *       widgets. Heavy - filter by screen type before doing real work.</li>
 * </ul>
 *
 * <p>All three no-op on dedicated server (no client renderer impl) and during
 * datagen.</p>
 */
public final class ScreenEvent {

    private ScreenEvent() {}

    public static final Event<ScreenLifecycleListener> OPEN = open();
    public static final Event<ScreenLifecycleListener> CLOSE = close();
    public static final Event<ScreenRenderListener> RENDER = render();

    private static Event<ScreenLifecycleListener> open() {
        if (Datagen.IS_DATAGEN) return Datagen.noOpEvent();
        RendererHelper helper = Services.renderers();
        return helper == null ? Datagen.noOpEvent() : helper.screenOpen();
    }

    private static Event<ScreenLifecycleListener> close() {
        if (Datagen.IS_DATAGEN) return Datagen.noOpEvent();
        RendererHelper helper = Services.renderers();
        return helper == null ? Datagen.noOpEvent() : helper.screenClose();
    }

    private static Event<ScreenRenderListener> render() {
        if (Datagen.IS_DATAGEN) return Datagen.noOpEvent();
        RendererHelper helper = Services.renderers();
        return helper == null ? Datagen.noOpEvent() : helper.screenRender();
    }
}

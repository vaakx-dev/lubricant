package wd40.lubricant.api.event.events.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;

/** Listener for {@link ScreenEvent#OPEN} / {@link ScreenEvent#CLOSE}. */
@FunctionalInterface
public interface ScreenLifecycleListener {
    void on(Minecraft client, Screen screen);
}

package wd40.lubricant.api.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;

/** Listener for {@link ScreenEvent#RENDER}. Fires every frame the screen is visible. */
@FunctionalInterface
public interface ScreenRenderListener {
    void render(Minecraft client, Screen screen, GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}

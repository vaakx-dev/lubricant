package wd40.lubricant.api.client.gui.render;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphics;

/**
 * A piece of HUD drawn each frame, between (or outside) vanilla's HUD pieces.
 * Modder code uses {@link GuiGraphics} for drawing and {@link DeltaTracker} for
 * partial-tick interpolation.
 *
 * <p>Registered via {@link HudRenderers}. The same layer instance fires every
 * frame the HUD is visible (i.e. not when an inventory is open if the anchor
 * implies that).</p>
 */
@FunctionalInterface
public interface HudLayer {
    void render(GuiGraphics graphics, DeltaTracker tracker);
}

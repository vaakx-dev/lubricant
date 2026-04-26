package wd40.lubricant.api.client.gui.render;

import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;
import wd40.lubricant.internal.rendering.RendererHelper;

/**
 * Client-side HUD layer registration. Same short-circuit contract as
 * {@link EntityRenderers} - safe to call on dedicated server / datagen JVM.
 *
 * <p>A layer is identified by a {@link ResourceLocation} unique to the modder
 * (e.g. {@code mymod:locator_bar}). Anchors are picked from {@link Anchor}.
 * On NeoForge each anchor maps to the matching {@code VanillaGuiLayers} entry.
 * On Fabric (1.21.1, fabric-api 0.115) anchored placement isn't available -
 * the layer is drawn after all vanilla HUD via {@code HudRenderCallback}
 * regardless of anchor or above/below choice. Plan accordingly: the bar shows
 * up on top of every vanilla element; do your own bounds checks if you need
 * to avoid overlap with chat/boss-bar.</p>
 *
 * <pre>{@code
 * HudRenderers.registerAbove(
 *     ResourceLocation.fromNamespaceAndPath(MyMod.ID, "locator_bar"),
 *     (graphics, delta) -> { ... },
 *     HudRenderers.Anchor.AIR_LEVEL);
 * }</pre>
 */
public final class HudRenderers {

    private HudRenderers() {}

    /** Append a layer above all vanilla HUD layers. */
    public static void registerTop(ResourceLocation name, HudLayer layer) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.hudLayerTop(name, layer);
    }

    /** Insert a layer immediately above {@code anchor}. */
    public static void registerAbove(ResourceLocation name, HudLayer layer, Anchor anchor) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.hudLayerAbove(name, layer, anchor);
    }

    /** Insert a layer immediately below {@code anchor}. */
    public static void registerBelow(ResourceLocation name, HudLayer layer, Anchor anchor) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.hudLayerBelow(name, layer, anchor);
    }

    /**
     * Vanilla HUD layer anchors for relative positioning. NeoForge has direct
     * matches for each value; Fabric's API exposes coarser groups, so several
     * granular anchors collapse to {@code HOTBAR_AND_BARS} on Fabric. The
     * relative ordering within that group is loader-defined, not modder-controlled.
     */
    public enum Anchor {
        /** Crosshair in screen center. */
        CROSSHAIR,
        /** Hotbar at the bottom. Fabric: HOTBAR_AND_BARS. */
        HOTBAR,
        /** Player health bar. Fabric: HOTBAR_AND_BARS. */
        HEALTH,
        /** Armor bar. Fabric: HOTBAR_AND_BARS. */
        ARMOR,
        /** Hunger / saturation bar. Fabric: HOTBAR_AND_BARS. */
        FOOD,
        /** Air bubbles bar. Fabric: HOTBAR_AND_BARS. */
        AIR_LEVEL,
        /** Experience bar. Fabric: HOTBAR_AND_BARS. */
        EXPERIENCE,
        /** Boss health overlay. */
        BOSS_BAR,
        /** Status effect icons. */
        EFFECTS,
        /** Chat history. */
        CHAT,
        /** Title / subtitle text. Fabric: TITLE_AND_SUBTITLE. */
        TITLE,
        /** F3 debug overlay. */
        DEBUG
    }
}

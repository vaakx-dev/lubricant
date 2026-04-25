package wd40.lubricant.api.client;

import net.minecraft.world.level.block.Block;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;
import wd40.lubricant.core.client.RendererHelper;

import java.util.function.Supplier;

/**
 * Client-side block render-type assignment. Same short-circuit contract as
 * {@link EntityRenderers} - safe to call on dedicated server / datagen JVM.
 *
 * <p>Vanilla blocks default to the solid render type. Call one of these methods
 * for blocks with transparency (cutout for hard-edge holes like glass panes,
 * cutoutMipped for foliage, translucent for stained glass).</p>
 *
 * <pre>{@code
 * BlockRenderers.cutout(Blocks.IRON_BARS);
 * BlockRenderers.cutoutMipped(Blocks.OAK_LEAVES);
 * BlockRenderers.translucent(Blocks.STAINED_GLASS);
 * }</pre>
 */
public final class BlockRenderers {

    private BlockRenderers() {}

    public static void cutout(Supplier<? extends Block> block) {
        apply(block, Layer.CUTOUT);
    }

    public static void cutoutMipped(Supplier<? extends Block> block) {
        apply(block, Layer.CUTOUT_MIPPED);
    }

    public static void translucent(Supplier<? extends Block> block) {
        apply(block, Layer.TRANSLUCENT);
    }

    private static void apply(Supplier<? extends Block> block, Layer layer) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.blockRenderType(block, layer);
    }

    /** Render layer choices exposed by {@link BlockRenderers}. The SPI maps these to the loader's actual {@code RenderType}. */
    public enum Layer {
        CUTOUT,
        CUTOUT_MIPPED,
        TRANSLUCENT
    }
}

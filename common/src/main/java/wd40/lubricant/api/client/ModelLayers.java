package wd40.lubricant.api.client;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;
import wd40.lubricant.core.client.RendererHelper;

import java.util.function.Supplier;

/**
 * Client-side entity model layer registration. Same short-circuit contract as
 * {@link EntityRenderers} - safe to call on dedicated server / datagen JVM.
 *
 * <p>Use this when your renderer needs a custom model. Define the
 * {@link LayerDefinition} in a static helper, register it here under a
 * {@link ModelLayerLocation}, then look it up in your renderer's constructor:
 * {@code context.bakeLayer(MY_LAYER)}.</p>
 *
 * <pre>{@code
 * public static final ModelLayerLocation MY_LAYER =
 *     ModelLayers.of(MyMod.ID, "my_entity", "main");
 * ModelLayers.register(MY_LAYER, MyEntityModel::createBodyLayer);
 * }</pre>
 */
public final class ModelLayers {

    private ModelLayers() {}

    /** Build a {@link ModelLayerLocation} under your modid. The {@code layer} part defaults to {@code "main"} for most renderers. */
    public static ModelLayerLocation of(String modId, String path, String layer) {
        return new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(modId, path), layer);
    }

    public static void register(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.modelLayer(location, definition);
    }
}

package wd40.lubricant.api.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;
import wd40.lubricant.core.client.RendererHelper;

import java.util.function.Supplier;

/**
 * Client-side renderer registration. All methods are safe to call from any
 * side - the facade short-circuits when no client renderer service is
 * available (dedicated server, datagen JVM). Modder code can call these from
 * regular {@link wd40.lubricant.api.Init} static blocks without guarding by
 * side; the loader handles the plumbing.
 */
public final class Renderers {

    private Renderers() {}

    /**
     * Register a built-in invisible renderer for {@code type}. The entity
     * spawns and ticks normally but draws nothing. Use as a placeholder while
     * developing or for purely-logical entities.
     */
    public static <T extends Entity> void entityInvisible(Supplier<? extends EntityType<? extends T>> type) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;  // dedicated server or no impl on classpath
        helper.entityInvisible(type);
    }

    /**
     * Register your own renderer for {@code type}. Pass any
     * {@link EntityRendererProvider} - typically a constructor reference like
     * {@code MyEntityRenderer::new}. The renderer class is yours; lubricant
     * just registers it at the right phase.
     */
    public static <T extends Entity> void entity(
            Supplier<? extends EntityType<? extends T>> type,
            EntityRendererProvider<T> provider) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.entity(type, provider);
    }
}

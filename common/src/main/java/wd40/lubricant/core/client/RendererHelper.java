package wd40.lubricant.core.client;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Supplier;

/**
 * Loader-specific source of client-side renderer registration backing the
 * public {@link wd40.lubricant.api.client.Renderers} facade. One implementation
 * per loader, discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.client.Renderers}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.client.Renderers}</li>
 * </ul>
 *
 * <p>References client-only vanilla types ({@link EntityRendererProvider})
 * in its method signatures. On a dedicated server the loader-side service
 * provider is absent (or its class is stripped) so
 * {@link wd40.lubricant.core.Services#renderers()} returns {@code null} and
 * the public facade no-ops. In a fabric production dedicated server, fabric
 * strips client classes wholesale; this interface itself may fail to load,
 * which is why {@code Services.renderers()} catches Throwable.</p>
 */
public interface RendererHelper {

    /**
     * Register a built-in invisible renderer for {@code type}. Lubricant
     * supplies the renderer; modder code never references any client-only
     * type. Cheapest possible registration.
     */
    <T extends Entity> void entityInvisible(Supplier<? extends EntityType<? extends T>> type);

    /**
     * Register the modder's own renderer for {@code type}. Full control:
     * custom textures, models, animations - anything {@link EntityRendererProvider}
     * lets you build. The modder writes the renderer; lubricant just plumbs it
     * into the loader's registration phase.
     */
    <T extends Entity> void entity(
            Supplier<? extends EntityType<? extends T>> type,
            EntityRendererProvider<T> provider);
}

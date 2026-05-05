package wd40.lubricant.internal.rendering;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import wd40.lubricant.api.client.renderer.block.BlockRenderers;
import wd40.lubricant.api.client.gui.render.HudLayer;
import wd40.lubricant.api.client.gui.render.HudRenderers;
import wd40.lubricant.api.event.Event;
import wd40.lubricant.api.event.ScreenLifecycleListener;
import wd40.lubricant.api.event.ScreenRenderListener;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Loader-specific source of client-side renderer registration backing the
 * public {@link wd40.lubricant.api.client.renderer.entity.EntityRenderers} and
 * {@link wd40.lubricant.api.client.renderer.particle.ParticleRenderers} facades. One
 * implementation per loader, discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.client.Renderers}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.client.Renderers}</li>
 * </ul>
 *
 * <p>References client-only vanilla types ({@link EntityRendererProvider})
 * in its method signatures. On a dedicated server the loader-side service
 * provider is absent (or its class is stripped) so
 * {@link wd40.lubricant.internal.Services#renderers()} returns {@code null} and
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

    /**
     * Register a sprite-based {@link ParticleProvider} for {@code type}. The
     * factory receives the loader's {@link SpriteSet} for the particle (loaded
     * from {@code assets/<modid>/particles/<path>.json}) and returns the actual
     * provider. Mirrors NeoForge's {@code event.registerSpriteSet} and Fabric's
     * {@code ParticleFactoryRegistry.register}.
     */
    <T extends ParticleOptions> void particle(
            Supplier<? extends ParticleType<T>> type,
            Function<SpriteSet, ParticleProvider<T>> factory);

    /**
     * Assign a non-default render type to a block. Fabric:
     * {@code BlockRenderLayerMap.INSTANCE.putBlock}. NeoForge:
     * {@code ItemBlockRenderTypes.setRenderLayer} via the client init phase.
     */
    void blockRenderType(Supplier<? extends Block> block, BlockRenderers.Layer layer);

    /**
     * Register an entity model layer definition under {@code location}. Fabric:
     * {@code EntityModelLayerRegistry.registerModelLayer}. NeoForge:
     * {@code EntityRenderersEvent.RegisterLayerDefinitions.registerLayerDefinition}.
     */
    void modelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition);

    /**
     * Client-side per-tick event. Fires after each client tick, on the main
     * client thread. Backed by Fabric's {@code ClientTickEvents.END_CLIENT_TICK}
     * and NeoForge's {@code ClientTickEvent.Post}.
     */
    Event<Consumer<Minecraft>> clientTick();

    /** Append a HUD layer above all vanilla HUD layers. */
    void hudLayerTop(ResourceLocation name, HudLayer layer);

    /** Insert a HUD layer immediately above {@code anchor}. */
    void hudLayerAbove(ResourceLocation name, HudLayer layer, HudRenderers.Anchor anchor);

    /** Insert a HUD layer immediately below {@code anchor}. */
    void hudLayerBelow(ResourceLocation name, HudLayer layer, HudRenderers.Anchor anchor);

    /** Register a screen for a menu type. Wraps vanilla {@code MenuScreens.register}. */
    <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void menuScreen(
            Supplier<? extends MenuType<? extends M>> type,
            MenuScreens.ScreenConstructor<M, S> screen);

    /** Fires after a screen's {@code init()} completes. */
    Event<ScreenLifecycleListener> screenOpen();

    /** Fires when a screen is being removed (close, replaced, etc.). */
    Event<ScreenLifecycleListener> screenClose();

    /** Fires every frame after a screen finishes drawing. */
    Event<ScreenRenderListener> screenRender();

    /** Register a per-item tint handler. */
    void itemColor(Supplier<? extends Item> item, ItemColor handler);
}

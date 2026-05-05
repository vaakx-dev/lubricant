package wd40.lubricant.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.ScreenEvent;
import net.neoforged.neoforge.common.NeoForge;
import wd40.lubricant.api.client.renderer.block.BlockRenderers;
import wd40.lubricant.api.client.gui.render.HudLayer;
import wd40.lubricant.api.client.gui.render.HudRenderers;
import wd40.lubricant.api.event.Event;
import wd40.lubricant.api.event.ScreenLifecycleListener;
import wd40.lubricant.api.event.ScreenRenderListener;
import wd40.lubricant.internal.rendering.RendererHelper;
import wd40.lubricant.internal.event.BridgedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * NeoForge impl of RendererHelper.
 *
 * <p>This class itself loads on both sides because the SPI provider file
 * names it. ServiceLoader instantiates it during {@code Services.renderers()}
 * lookup. Modder code calls {@link #entityInvisible} / {@link #entity} which
 * queue a {@link Pending} record. The record references {@code EntityRendererProvider}
 * - a client-only type - so server-side class load may fail in production
 * fabric-style stripping environments. {@code Services.renderers()} catches
 * Throwable to keep the server alive in that case.</p>
 *
 * <p>The mod-bus listener attachment + drain references {@link EntityRenderersEvent}
 * (client-only). Those references live in {@link ClientWiring}, which is
 * called only on {@link Dist#CLIENT}.</p>
 */
public final class Renderers implements RendererHelper {

    public static volatile Renderers INSTANCE;

    final List<Pending<?>> pending = new ArrayList<>();
    final List<PendingParticle<?>> pendingParticles = new ArrayList<>();
    final List<PendingBlockLayer> pendingBlockLayers = new ArrayList<>();
    final List<PendingModelLayer> pendingModelLayers = new ArrayList<>();
    final List<PendingHud> pendingHuds = new ArrayList<>();
    final List<PendingMenuScreen<?, ?>> pendingMenuScreens = new ArrayList<>();
    final List<PendingItemColor> pendingItemColors = new ArrayList<>();

    private final Event<Consumer<Minecraft>> clientTick = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener(
                    (ClientTickEvent.Post e) -> l.accept(Minecraft.getInstance())));

    private final Event<ScreenLifecycleListener> screenOpen = new BridgedEvent<>(
            listener -> NeoForge.EVENT_BUS.addListener(
                    (ScreenEvent.Init.Post event) -> listener.on(Minecraft.getInstance(), event.getScreen())));

    private final Event<ScreenLifecycleListener> screenClose = new BridgedEvent<>(
            listener -> NeoForge.EVENT_BUS.addListener(
                    (ScreenEvent.Closing event) -> listener.on(Minecraft.getInstance(), event.getScreen())));

    private final Event<ScreenRenderListener> screenRender = new BridgedEvent<>(
            listener -> NeoForge.EVENT_BUS.addListener(
                    (ScreenEvent.Render.Post event) -> listener.render(
                            Minecraft.getInstance(), event.getScreen(),
                            event.getGuiGraphics(), event.getMouseX(), event.getMouseY(),
                            event.getPartialTick())));

    public Renderers() {
        INSTANCE = this;
    }

    @Override
    public <T extends Entity> void entityInvisible(Supplier<? extends EntityType<? extends T>> type) {
        pending.add(new Pending<>(type, InvisibleRenderer::new));
    }

    @Override
    public <T extends Entity> void entity(
            Supplier<? extends EntityType<? extends T>> type,
            EntityRendererProvider<T> provider) {
        pending.add(new Pending<>(type, provider));
    }

    @Override
    public <T extends ParticleOptions> void particle(
            Supplier<? extends ParticleType<T>> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {
        pendingParticles.add(new PendingParticle<>(type, factory));
    }

    @Override
    public void blockRenderType(Supplier<? extends Block> block, BlockRenderers.Layer layer) {
        pendingBlockLayers.add(new PendingBlockLayer(block, layer));
    }

    @Override
    public void modelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        pendingModelLayers.add(new PendingModelLayer(location, definition));
    }

    @Override
    public Event<Consumer<Minecraft>> clientTick() {
        return clientTick;
    }

    @Override
    public Event<ScreenLifecycleListener> screenOpen() { return screenOpen; }

    @Override
    public Event<ScreenLifecycleListener> screenClose() { return screenClose; }

    @Override
    public Event<ScreenRenderListener> screenRender() { return screenRender; }

    @Override
    public void itemColor(Supplier<? extends Item> item, ItemColor handler) {
        pendingItemColors.add(new PendingItemColor(item, handler));
    }

    @Override
    public void hudLayerTop(ResourceLocation name, HudLayer layer) {
        pendingHuds.add(new PendingHud(name, layer, null, false));
    }

    @Override
    public void hudLayerAbove(ResourceLocation name, HudLayer layer, HudRenderers.Anchor anchor) {
        pendingHuds.add(new PendingHud(name, layer, anchor, true));
    }

    @Override
    public void hudLayerBelow(ResourceLocation name, HudLayer layer, HudRenderers.Anchor anchor) {
        pendingHuds.add(new PendingHud(name, layer, anchor, false));
    }

    @Override
    public <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void menuScreen(
            Supplier<? extends MenuType<? extends M>> type,
            MenuScreens.ScreenConstructor<M, S> screen) {
        pendingMenuScreens.add(new PendingMenuScreen<>(type, screen));
    }

    /** Called from {@code neoforge.Entry} - delegates to client-only wiring iff on client. */
    public static void attachListenerIfClient(IEventBus modBus) {
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientWiring.attach(modBus);
        }
    }

    record Pending<T extends Entity>(
            Supplier<? extends EntityType<? extends T>> type,
            EntityRendererProvider<T> provider) {}

    record PendingParticle<T extends ParticleOptions>(
            Supplier<? extends ParticleType<T>> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {}

    record PendingBlockLayer(Supplier<? extends Block> block, BlockRenderers.Layer layer) {}

    record PendingModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {}

    record PendingHud(ResourceLocation name, HudLayer layer, HudRenderers.Anchor anchor, boolean above) {}

    record PendingMenuScreen<M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>>(
            Supplier<? extends MenuType<? extends M>> type,
            MenuScreens.ScreenConstructor<M, S> screen) {}

    record PendingItemColor(Supplier<? extends Item> item, ItemColor handler) {}

    /**
     * Client-only wiring. References {@link EntityRenderersEvent} (stripped
     * on dedicated servers). Loaded only when {@link #attachListenerIfClient}
     * routes to it under a {@code Dist.CLIENT} guard.
     */
    @OnlyIn(Dist.CLIENT)
    private static final class ClientWiring {
        static void attach(IEventBus modBus) {
            modBus.addListener(ClientWiring::onRegister);
            modBus.addListener(ClientWiring::onRegisterParticles);
            modBus.addListener(ClientWiring::onRegisterLayers);
            modBus.addListener(ClientWiring::onRegisterGuiLayers);
            modBus.addListener(ClientWiring::onRegisterItemColors);
            modBus.addListener(ClientWiring::onClientSetup);
            // Block render type is a mutable static (ItemBlockRenderTypes); apply
            // synchronously here. All registry binding is done by this point.
            for (PendingBlockLayer p : INSTANCE.pendingBlockLayers) {
                ItemBlockRenderTypes.setRenderLayer(p.block().get(), toRenderType(p.layer()));
            }
            INSTANCE.pendingBlockLayers.clear();
        }

        // MenuScreens.register must run after MenuTypes are registered. Drain in
        // FMLClientSetupEvent (fired after all RegisterEvent waves complete).
        static void onClientSetup(net.neoforged.fml.event.lifecycle.FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                for (PendingMenuScreen<?, ?> p : INSTANCE.pendingMenuScreens) registerOneMenuScreen(p);
                INSTANCE.pendingMenuScreens.clear();
            });
        }

        @SuppressWarnings("unchecked")
        static <M extends AbstractContainerMenu, S extends Screen & MenuAccess<M>> void registerOneMenuScreen(
                PendingMenuScreen<?, ?> raw) {
            PendingMenuScreen<M, S> p = (PendingMenuScreen<M, S>) raw;
            MenuType<M> bound = (MenuType<M>) p.type().get();
            MenuScreens.register(bound, p.screen());
        }

        static void onRegister(EntityRenderersEvent.RegisterRenderers event) {
            for (Pending<?> p : INSTANCE.pending) registerOne(event, p);
            INSTANCE.pending.clear();
        }

        @SuppressWarnings("unchecked")
        static <T extends Entity> void registerOne(EntityRenderersEvent.RegisterRenderers event, Pending<T> p) {
            EntityType<T> bound = (EntityType<T>) p.type().get();
            event.registerEntityRenderer(bound, p.provider());
        }

        static void onRegisterParticles(RegisterParticleProvidersEvent event) {
            for (PendingParticle<?> p : INSTANCE.pendingParticles) registerOneParticle(event, p);
            INSTANCE.pendingParticles.clear();
        }

        static <T extends ParticleOptions> void registerOneParticle(
                RegisterParticleProvidersEvent event, PendingParticle<T> p) {
            event.registerSpriteSet(p.type().get(), p.factory()::apply);
        }

        static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
            for (PendingModelLayer p : INSTANCE.pendingModelLayers) {
                event.registerLayerDefinition(p.location(), p.definition()::get);
            }
            INSTANCE.pendingModelLayers.clear();
        }

        static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
            for (PendingItemColor p : INSTANCE.pendingItemColors) {
                event.register(p.handler(), p.item().get());
            }
            INSTANCE.pendingItemColors.clear();
        }

        static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
            for (PendingHud p : INSTANCE.pendingHuds) {
                LayeredDraw.Layer adapted = (graphics, tracker) -> p.layer().render(graphics, tracker);
                if (p.anchor() == null) {
                    event.registerAboveAll(p.name(), adapted);
                } else if (p.above()) {
                    event.registerAbove(toAnchor(p.anchor()), p.name(), adapted);
                } else {
                    event.registerBelow(toAnchor(p.anchor()), p.name(), adapted);
                }
            }
            INSTANCE.pendingHuds.clear();
        }

        static ResourceLocation toAnchor(HudRenderers.Anchor anchor) {
            return switch (anchor) {
                case CROSSHAIR -> VanillaGuiLayers.CROSSHAIR;
                case HOTBAR -> VanillaGuiLayers.HOTBAR;
                case HEALTH -> VanillaGuiLayers.PLAYER_HEALTH;
                case ARMOR -> VanillaGuiLayers.ARMOR_LEVEL;
                case FOOD -> VanillaGuiLayers.FOOD_LEVEL;
                case AIR_LEVEL -> VanillaGuiLayers.AIR_LEVEL;
                case EXPERIENCE -> VanillaGuiLayers.EXPERIENCE_BAR;
                case BOSS_BAR -> VanillaGuiLayers.BOSS_OVERLAY;
                case EFFECTS -> VanillaGuiLayers.EFFECTS;
                case CHAT -> VanillaGuiLayers.CHAT;
                case TITLE -> VanillaGuiLayers.TITLE;
                case DEBUG -> VanillaGuiLayers.DEBUG_OVERLAY;
            };
        }

        static RenderType toRenderType(BlockRenderers.Layer layer) {
            return switch (layer) {
                case CUTOUT -> RenderType.cutout();
                case CUTOUT_MIPPED -> RenderType.cutoutMipped();
                case TRANSLUCENT -> RenderType.translucent();
            };
        }
    }

    /** Tiny no-draw renderer used by entityInvisible. */
    @OnlyIn(Dist.CLIENT)
    static final class InvisibleRenderer<T extends Entity> extends EntityRenderer<T> {
        private static final ResourceLocation NONE = ResourceLocation.withDefaultNamespace("textures/misc/white.png");

        InvisibleRenderer(EntityRendererProvider.Context context) {
            super(context);
        }

        @Override
        public ResourceLocation getTextureLocation(T entity) {
            return NONE;
        }

        @Override
        public void render(T entity, float entityYaw, float partialTicks, PoseStack poseStack,
                           MultiBufferSource buffer, int packedLight) {
            // intentionally empty
        }
    }
}

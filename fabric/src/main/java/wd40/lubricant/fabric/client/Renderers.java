package wd40.lubricant.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import wd40.lubricant.api.client.BlockRenderers;
import wd40.lubricant.core.Bootstrap;
import wd40.lubricant.core.Services;
import wd40.lubricant.core.client.RendererHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

// Fabric impl of RendererHelper. Wears two hats:
//
// 1. JDK ServiceLoader instantiates one copy via META-INF/services/wd40.lubricant.core.client.RendererHelper.
//    Public API calls (entityInvisible / entity) go to that instance, which queues into PENDING.
//
// 2. fabric.mod.json's "client" entrypoint instantiates a second copy via fabric-loader.
//    onInitializeClient runs at the right phase, drains PENDING, calls EntityRendererRegistry.register.
//
// Both instances share the static PENDING list. Different instance refs, same state.
@Environment(EnvType.CLIENT)
public final class Renderers implements RendererHelper, ClientModInitializer {

    private static final List<Pending<?>> PENDING = new ArrayList<>();
    private static final List<PendingParticle<?>> PENDING_PARTICLES = new ArrayList<>();
    private static final List<PendingBlockLayer> PENDING_BLOCK_LAYERS = new ArrayList<>();
    private static final List<PendingModelLayer> PENDING_MODEL_LAYERS = new ArrayList<>();

    public Renderers() {}

    @Override
    public <T extends Entity> void entityInvisible(Supplier<? extends EntityType<? extends T>> type) {
        PENDING.add(new Pending<>(type, InvisibleRenderer::new));
    }

    @Override
    public <T extends Entity> void entity(
            Supplier<? extends EntityType<? extends T>> type,
            EntityRendererProvider<T> provider) {
        PENDING.add(new Pending<>(type, provider));
    }

    @Override
    public <T extends ParticleOptions> void particle(
            Supplier<? extends ParticleType<T>> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {
        PENDING_PARTICLES.add(new PendingParticle<>(type, factory));
    }

    @Override
    public void blockRenderType(Supplier<? extends Block> block, BlockRenderers.Layer layer) {
        PENDING_BLOCK_LAYERS.add(new PendingBlockLayer(block, layer));
    }

    @Override
    public void modelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        PENDING_MODEL_LAYERS.add(new PendingModelLayer(location, definition));
    }

    @Override
    public void onInitializeClient() {
        // Pulls double duty: this class IS the SPI impl for renderers, AND fabric's
        // single client entrypoint. Sequence: load ClientInit classes (their static
        // blocks queue renderers/particles into PENDING), drain the queues into
        // fabric registries, then fire ClientEvents.SETUP for any one-time wiring.
        Bootstrap.loadClient();
        for (Pending<?> p : PENDING) p.register();
        PENDING.clear();
        for (PendingParticle<?> p : PENDING_PARTICLES) p.register();
        PENDING_PARTICLES.clear();
        for (PendingBlockLayer p : PENDING_BLOCK_LAYERS) p.register();
        PENDING_BLOCK_LAYERS.clear();
        for (PendingModelLayer p : PENDING_MODEL_LAYERS) p.register();
        PENDING_MODEL_LAYERS.clear();
        Services.events().fireClientSetup();
    }

    private record Pending<T extends Entity>(
            Supplier<? extends EntityType<? extends T>> type,
            EntityRendererProvider<T> provider) {
        @SuppressWarnings("unchecked")
        void register() {
            EntityType<T> bound = (EntityType<T>) type.get();
            EntityRendererRegistry.register(bound, provider);
        }
    }

    private record PendingParticle<T extends ParticleOptions>(
            Supplier<? extends ParticleType<T>> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {
        void register() {
            // Fabric's PendingParticleFactory yields a FabricSpriteProvider, which IS-A SpriteSet.
            ParticleFactoryRegistry.getInstance().register(type.get(), factory::apply);
        }
    }

    private record PendingBlockLayer(Supplier<? extends Block> block, BlockRenderers.Layer layer) {
        void register() {
            BlockRenderLayerMap.INSTANCE.putBlock(block.get(), toRenderType(layer));
        }
    }

    private record PendingModelLayer(ModelLayerLocation location, Supplier<LayerDefinition> definition) {
        void register() {
            EntityModelLayerRegistry.registerModelLayer(location, definition::get);
        }
    }

    private static RenderType toRenderType(BlockRenderers.Layer layer) {
        return switch (layer) {
            case CUTOUT -> RenderType.cutout();
            case CUTOUT_MIPPED -> RenderType.cutoutMipped();
            case TRANSLUCENT -> RenderType.translucent();
        };
    }

    /** Tiny no-draw renderer used by entityInvisible. Modder-supplied renderers don't go through this. */
    private static final class InvisibleRenderer<T extends Entity> extends EntityRenderer<T> {
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

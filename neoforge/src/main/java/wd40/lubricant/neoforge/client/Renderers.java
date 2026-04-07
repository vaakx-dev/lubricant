package wd40.lubricant.neoforge.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import wd40.lubricant.core.client.RendererHelper;

import java.util.ArrayList;
import java.util.List;
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

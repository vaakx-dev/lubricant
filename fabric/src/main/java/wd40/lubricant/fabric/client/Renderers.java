package wd40.lubricant.fabric.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import wd40.lubricant.core.client.RendererHelper;

import java.util.ArrayList;
import java.util.List;
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
    public void onInitializeClient() {
        for (Pending<?> p : PENDING) p.register();
        PENDING.clear();
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

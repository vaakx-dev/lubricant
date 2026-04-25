package wd40.lubricant.api.client;

import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;
import wd40.lubricant.core.client.RendererHelper;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Client-side particle rendering registration. Same short-circuit contract as
 * {@link EntityRenderers} - safe to call on dedicated server / datagen JVM.
 *
 * <p>Pair with {@link wd40.lubricant.api.common.registry.ParticleRegistry} for the
 * data side: register the {@link ParticleType} once on both sides via
 * {@code ParticleRegistry}, then on the client side register a renderer
 * (vanilla calls it {@link ParticleProvider}) for it here. The
 * {@link SpriteSet} is loaded from the {@code .json} file at
 * {@code assets/<modid>/particles/<path>.json}.</p>
 */
public final class ParticleRenderers {

    private ParticleRenderers() {}

    public static <T extends ParticleOptions> void register(
            Supplier<? extends ParticleType<T>> type,
            Function<SpriteSet, ParticleProvider<T>> factory) {
        if (Datagen.IS_DATAGEN) return;
        RendererHelper helper = Services.renderers();
        if (helper == null) return;
        helper.particle(type, factory);
    }
}

package wd40.vaakx.cog.common.particles;

import net.minecraft.core.particles.SimpleParticleType;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.registry.ParticleRegistry;
import wd40.vaakx.cog.Cog;

import java.util.function.Supplier;

/**
 * Smoke test for {@link ParticleRegistry}. Registers one particle type the
 * {@link wd40.vaakx.cog.server.Server} layer spawns on greased_cog right-click.
 *
 * <p>The {@code particles/gear_spark.json} asset references a vanilla texture
 * so we avoid shipping a particle sprite just for the smoke test.</p>
 */
public final class Particles implements Init {

    public static final ParticleRegistry PARTICLES = ParticleRegistry.create(Cog.ID);

    /** Spawned when right-clicking a greased_cog. Resource id: {@code cog:gear_spark}. */
    public static final Supplier<SimpleParticleType> GEAR_SPARK = PARTICLES.register("gear_spark", false);

    public Particles() {}
}

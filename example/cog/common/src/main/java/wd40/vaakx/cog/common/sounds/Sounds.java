package wd40.vaakx.cog.common.sounds;

import net.minecraft.sounds.SoundEvent;
import wd40.lubricant.api.init.CommonInit;
import wd40.lubricant.api.sounds.SoundRegistry;
import wd40.vaakx.cog.Cog;

import java.util.function.Supplier;

/**
 * Smoke test for {@link SoundRegistry}. Registers one sound the
 * {@link wd40.vaakx.cog.server.Server} layer plays on greased_cog right-click.
 *
 * <p>The {@code sounds.json} entry under {@code assets/cog/} maps this id to a
 * vanilla .ogg, so we avoid shipping an audio asset just for the smoke test.</p>
 */
public final class Sounds implements CommonInit {

    public static final SoundRegistry SOUNDS = SoundRegistry.create(Cog.ID);

    /** Played when right-clicking a greased_cog. Resource id: {@code cog:gear_click}. */
    public static final Supplier<SoundEvent> GEAR_CLICK = SOUNDS.register("gear_click");

    public Sounds() {}
}

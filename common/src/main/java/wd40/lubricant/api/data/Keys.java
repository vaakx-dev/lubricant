package wd40.lubricant.api.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

/**
 * Per-mod factory for {@link Key} instances. Declare the modId once, mint as
 * many keys as the mod needs without repeating it.
 *
 * <pre>{@code
 * private static final Keys KEYS = Keys.create("mymod");
 *
 * public static final Key<Integer>  CHARGE = KEYS.of("charge", Codec.INT, 0);
 * public static final Key<BlockPos> LINKED = KEYS.of("linked", BlockPos.CODEC);
 * }</pre>
 *
 * <p>Mirrors the {@code ItemRegistry.create(modId)} pattern - the modId is the
 * one piece of identity information the consumer needs to declare per mod.</p>
 */
public final class Keys {

    private final String modId;

    private Keys(String modId) {
        this.modId = modId;
    }

    public static Keys create(String modId) {
        return new Keys(modId);
    }

    /** Mint a {@link Key} with a default value (returned by {@code get} when absent). */
    public <T> Key<T> of(String path, Codec<T> codec, T defaultValue) {
        return new Key<>(ResourceLocation.fromNamespaceAndPath(modId, path), codec, defaultValue);
    }

    /** Mint a {@link Key} with no default - {@code get} returns {@code null} when absent. */
    public <T> Key<T> of(String path, Codec<T> codec) {
        return new Key<>(ResourceLocation.fromNamespaceAndPath(modId, path), codec, null);
    }
}

package wd40.lubricant.api.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

/**
 * Per-mod factory for {@link DataKey} instances. Declare the modId once, mint as
 * many keys as the mod needs without repeating it.
 *
 * <pre>{@code
 * private static final DataKeys KEYS = DataKeys.create("mymod");
 *
 * public static final DataKey<Integer>  CHARGE = KEYS.of("charge", Codec.INT, 0);
 * public static final DataKey<BlockPos> LINKED = KEYS.of("linked", BlockPos.CODEC);
 * }</pre>
 *
 * <p>Mirrors the {@code ItemRegistry.create(modId)} pattern - the modId is the
 * one piece of identity information the consumer needs to declare per mod.</p>
 */
public final class DataKeys {

    private final String modId;

    private DataKeys(String modId) {
        this.modId = modId;
    }

    public static DataKeys create(String modId) {
        return new DataKeys(modId);
    }

    /** Mint a {@link DataKey} with a default value (returned by {@code get} when absent). */
    public <T> DataKey<T> of(String path, Codec<T> codec, T defaultValue) {
        return new DataKey<>(ResourceLocation.fromNamespaceAndPath(modId, path), codec, defaultValue);
    }

    /** Mint a {@link DataKey} with no default - {@code get} returns {@code null} when absent. */
    public <T> DataKey<T> of(String path, Codec<T> codec) {
        return new DataKey<>(ResourceLocation.fromNamespaceAndPath(modId, path), codec, null);
    }
}

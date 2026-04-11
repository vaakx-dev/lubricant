package wd40.lubricant.api.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

/**
 * Per-mod factory for {@link DataKey} instances. Declare the modId once, mint as
 * many keys as the mod needs without repeating it.
 *
 * <p>Use {@link #stack}, {@link #entity}, {@link #blockEntity} for the common
 * one-line case: each mints a key AND registers it on the matching facade. Use
 * {@link #of} for manual registration (e.g. shared key reused across facades is
 * not allowed - declare separate keys with different paths instead).</p>
 *
 * <pre>{@code
 * private static final DataKeys KEYS = DataKeys.create("mymod");
 *
 * public static final DataKey<Integer>  CHARGE = KEYS.stack("charge", Codec.INT, 0);
 * public static final DataKey<UUID>     OWNER  = KEYS.entity("owner", UUIDUtil.CODEC);
 * public static final DataKey<Integer>  CLICKS = KEYS.blockEntity("clicks", Codec.INT, 0);
 * }</pre>
 */
public final class DataKeys {

    private final String modId;

    private DataKeys(String modId) {
        this.modId = modId;
    }

    public static DataKeys create(String modId) {
        return new DataKeys(modId);
    }

    /** Mint and register an item-attachable key in one call. */
    public <T> DataKey<T> stack(String path, Codec<T> codec, T defaultValue) {
        DataKey<T> key = of(path, codec, defaultValue);
        StackData.register(key);
        return key;
    }

    /** Mint and register an item-attachable key in one call (no default value). */
    public <T> DataKey<T> stack(String path, Codec<T> codec) {
        DataKey<T> key = of(path, codec);
        StackData.register(key);
        return key;
    }

    /** Mint and register an entity-attachable key in one call. */
    public <T> DataKey<T> entity(String path, Codec<T> codec, T defaultValue) {
        DataKey<T> key = of(path, codec, defaultValue);
        EntityData.register(key);
        return key;
    }

    /** Mint and register an entity-attachable key in one call (no default value). */
    public <T> DataKey<T> entity(String path, Codec<T> codec) {
        DataKey<T> key = of(path, codec);
        EntityData.register(key);
        return key;
    }

    /** Mint and register a block-entity-attachable key in one call. */
    public <T> DataKey<T> blockEntity(String path, Codec<T> codec, T defaultValue) {
        DataKey<T> key = of(path, codec, defaultValue);
        BlockEntityData.register(key);
        return key;
    }

    /** Mint and register a block-entity-attachable key in one call (no default value). */
    public <T> DataKey<T> blockEntity(String path, Codec<T> codec) {
        DataKey<T> key = of(path, codec);
        BlockEntityData.register(key);
        return key;
    }

    /** Mint a {@link DataKey} without registering it. Use when registration happens elsewhere. */
    public <T> DataKey<T> of(String path, Codec<T> codec, T defaultValue) {
        return new DataKey<>(ResourceLocation.fromNamespaceAndPath(modId, path), codec, defaultValue);
    }

    /** Mint a {@link DataKey} without registering it (no default - {@code get} returns null when absent). */
    public <T> DataKey<T> of(String path, Codec<T> codec) {
        return new DataKey<>(ResourceLocation.fromNamespaceAndPath(modId, path), codec, null);
    }
}

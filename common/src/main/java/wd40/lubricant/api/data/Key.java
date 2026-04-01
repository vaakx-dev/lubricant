package wd40.lubricant.api.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

/**
 * A typed handle for a piece of data attached to an ItemStack, BlockEntity,
 * Entity, or Level. Created via {@link Keys#of}, registered via the matching
 * facade ({@link Stacks#register}, {@code BlockEntities.register}, etc.), then
 * used as the lookup key for get/set operations.
 *
 * <p>The {@link Codec} is the only serialization spec lubricant needs: on
 * 1.21 it drives DataComponentType persistence and network sync; on 1.20 it
 * drives a CompoundTag write under the key's namespaced id.</p>
 *
 * <p>The {@code defaultValue} is returned by {@code get} when the key has not
 * been set on the holder. Pass {@code null} to leave "absent" distinct from
 * "default" - callers must use {@code has} to disambiguate in that case.</p>
 *
 * @param <T> the value type
 */
public final class Key<T> {

    private final ResourceLocation id;
    private final Codec<T> codec;
    private final T defaultValue;

    Key(ResourceLocation id, Codec<T> codec, T defaultValue) {
        this.id = id;
        this.codec = codec;
        this.defaultValue = defaultValue;
    }

    public ResourceLocation id() { return id; }

    public Codec<T> codec() { return codec; }

    /** May be {@code null}. */
    public T defaultValue() { return defaultValue; }

    @Override
    public String toString() {
        return "Key[" + id + "]";
    }
}

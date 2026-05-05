package wd40.lubricant.api.saveddata;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import wd40.lubricant.internal.Datagen;

import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Typed handle for a piece of data persisted per {@link ServerLevel}, backed by
 * vanilla {@link SavedData}. The serialized JSON-of-NBT lives at
 * {@code <world>/<dim>/data/<modid>_<path>.dat}.
 *
 * <p>Register once at mod init (e.g. from a {@code static} block in a
 * {@link wd40.lubricant.api.init.CommonInit} implementation):</p>
 *
 * <pre>{@code
 * public static final WorldData<Integer> CLICKS =
 *     WorldData.register("mymod", "clicks", Codec.INT, () -> 0);
 * }</pre>
 *
 * <p>Then read/write against any {@link ServerLevel}:</p>
 *
 * <pre>{@code
 * int n = CLICKS.get(serverLevel);
 * CLICKS.set(serverLevel, n + 1);
 * // or:
 * CLICKS.update(serverLevel, current -> current + 1);
 * }</pre>
 *
 * <p>Scope is per-{@link ServerLevel} (i.e. per-dimension). Pass the overworld
 * for "per-world" semantics; pass a specific dimension for per-dimension data.
 * Mutations mark the holder dirty automatically - vanilla flushes to disk on
 * world save.</p>
 *
 * <p>The {@link Codec} runs against a {@link DynamicOps} built from the level's
 * {@link HolderLookup.Provider}, so registry-bearing types (e.g. {@code ItemStack})
 * round-trip correctly.</p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/WorldData">WorldData wiki</a></p>
 *
 * @param <T> the value type
 */
public final class WorldData<T> {

    private final ResourceLocation id;
    private final String storageKey;
    private final Codec<T> codec;
    private final Supplier<T> defaultValue;
    private final SavedData.Factory<Holder<T>> factory;

    private WorldData(ResourceLocation id, Codec<T> codec, Supplier<T> defaultValue) {
        this.id = id;
        this.storageKey = id.getNamespace() + "_" + id.getPath();
        this.codec = codec;
        this.defaultValue = defaultValue;
        this.factory = new SavedData.Factory<>(
                () -> new Holder<>(this, defaultValue.get()),
                (tag, provider) -> {
                    DynamicOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
                    Tag value = tag.get("value");
                    T parsed = value == null
                            ? defaultValue.get()
                            : codec.parse(ops, value).result().orElseGet(defaultValue);
                    return new Holder<>(this, parsed);
                },
                null);
    }

    /**
     * Create a {@link WorldData} handle. Calling this does not touch any
     * {@link ServerLevel} - storage is allocated lazily on first {@link #get}
     * or {@link #set} per level.
     */
    public static <T> WorldData<T> register(String modId, String path, Codec<T> codec, Supplier<T> defaultValue) {
        return new WorldData<>(ResourceLocation.fromNamespaceAndPath(modId, path), codec, defaultValue);
    }

    /** Read the current value on {@code level}, falling back to the default if absent. */
    public T get(ServerLevel level) {
        if (Datagen.IS_DATAGEN) return defaultValue.get();
        return level.getDataStorage().computeIfAbsent(factory, storageKey).value;
    }

    /** Replace the value on {@code level} and mark dirty. */
    public void set(ServerLevel level, T value) {
        if (Datagen.IS_DATAGEN) return;
        Holder<T> holder = level.getDataStorage().computeIfAbsent(factory, storageKey);
        holder.value = value;
        holder.setDirty();
    }

    /** Read-modify-write convenience. The supplied function must return non-null. */
    public void update(ServerLevel level, UnaryOperator<T> updater) {
        if (Datagen.IS_DATAGEN) return;
        Holder<T> holder = level.getDataStorage().computeIfAbsent(factory, storageKey);
        holder.value = updater.apply(holder.value);
        holder.setDirty();
    }

    public ResourceLocation id() { return id; }

    @Override
    public String toString() {
        return "WorldData[" + id + "]";
    }

    private static final class Holder<T> extends SavedData {
        private final WorldData<T> owner;
        T value;

        Holder(WorldData<T> owner, T value) {
            this.owner = owner;
            this.value = value;
        }

        @Override
        public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
            DynamicOps<Tag> ops = provider.createSerializationContext(NbtOps.INSTANCE);
            owner.codec.encodeStart(ops, value).result()
                    .ifPresent(encoded -> tag.put("value", encoded));
            return tag;
        }
    }
}

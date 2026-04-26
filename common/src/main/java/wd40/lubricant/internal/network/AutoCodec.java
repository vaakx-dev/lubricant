package wd40.lubricant.internal.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.RecordComponent;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Builds a {@link StreamCodec} from a record's components by mapping each
 * component type to a built-in stream codec. Backs the auto-detection in
 * {@code Payloads.toClient/toServer}.
 *
 * <p>Component-type lookup table covers primitives, common Mojang types, and
 * vanilla nbt-encoded types. Unsupported types throw at first registration with
 * a clear message - the modder hand-writes a CODEC field instead.</p>
 */
public final class AutoCodec {

    private static final Map<Class<?>, StreamCodec<?, ?>> COMPONENT = new HashMap<>();
    private static final Map<Class<?>, StreamCodec<?, ?>> CACHE = new ConcurrentHashMap<>();

    static {
        COMPONENT.put(int.class,              ByteBufCodecs.VAR_INT);
        COMPONENT.put(Integer.class,          ByteBufCodecs.VAR_INT);
        COMPONENT.put(long.class,             ByteBufCodecs.VAR_LONG);
        COMPONENT.put(Long.class,             ByteBufCodecs.VAR_LONG);
        COMPONENT.put(boolean.class,          ByteBufCodecs.BOOL);
        COMPONENT.put(Boolean.class,          ByteBufCodecs.BOOL);
        COMPONENT.put(float.class,            ByteBufCodecs.FLOAT);
        COMPONENT.put(Float.class,            ByteBufCodecs.FLOAT);
        COMPONENT.put(double.class,           ByteBufCodecs.DOUBLE);
        COMPONENT.put(Double.class,           ByteBufCodecs.DOUBLE);
        COMPONENT.put(byte.class,             ByteBufCodecs.BYTE);
        COMPONENT.put(Byte.class,             ByteBufCodecs.BYTE);
        COMPONENT.put(short.class,            ByteBufCodecs.SHORT);
        COMPONENT.put(Short.class,            ByteBufCodecs.SHORT);
        COMPONENT.put(String.class,           ByteBufCodecs.STRING_UTF8);
        COMPONENT.put(byte[].class,           ByteBufCodecs.BYTE_ARRAY);
        COMPONENT.put(UUID.class,             UUIDUtil.STREAM_CODEC);
        COMPONENT.put(BlockPos.class,         BlockPos.STREAM_CODEC);
        COMPONENT.put(GlobalPos.class,        GlobalPos.STREAM_CODEC);
        COMPONENT.put(ResourceLocation.class, ResourceLocation.STREAM_CODEC);
        COMPONENT.put(ItemStack.class,        ItemStack.STREAM_CODEC);
    }

    private AutoCodec() {}

    @SuppressWarnings("unchecked")
    public static <T extends CustomPacketPayload> StreamCodec<RegistryFriendlyByteBuf, T> forRecord(Class<T> cls) {
        StreamCodec<?, ?> cached = CACHE.get(cls);
        if (cached != null) return (StreamCodec<RegistryFriendlyByteBuf, T>) cached;

        if (!cls.isRecord()) {
            throw new IllegalStateException(cls.getName() +
                    " is not a record - lubricant can only autogen codecs for records. Add a CODEC field.");
        }

        RecordComponent[] components = cls.getRecordComponents();
        StreamCodec<RegistryFriendlyByteBuf, T> codec = build(cls, components);
        CACHE.put(cls, codec);
        return codec;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static <T extends CustomPacketPayload> StreamCodec<RegistryFriendlyByteBuf, T> build(
            Class<T> cls, RecordComponent[] components) {

        StreamCodec<RegistryFriendlyByteBuf, ?>[] codecs = new StreamCodec[components.length];
        Class<?>[] types = new Class<?>[components.length];
        for (int i = 0; i < components.length; i++) {
            Class<?> type = components[i].getType();
            StreamCodec<?, ?> componentCodec = COMPONENT.get(type);
            if (componentCodec == null) {
                throw new IllegalStateException(cls.getName() + "." + components[i].getName() +
                        ": type " + type.getName() + " has no built-in stream codec - " +
                        "declare an explicit CODEC field on the record.");
            }
            codecs[i] = (StreamCodec<RegistryFriendlyByteBuf, ?>) componentCodec;
            types[i] = type;
        }

        Constructor<T> ctor;
        try {
            ctor = cls.getDeclaredConstructor(types);
            ctor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("canonical constructor not found for " + cls.getName(), e);
        }

        return new GeneratedCodec<>(cls, codecs, ctor);
    }

    /**
     * Generic StreamCodec implementation. Encodes by reading record accessors
     * via reflection, decodes by invoking the canonical constructor with the
     * decoded values. One instance per payload class, cached.
     */
    private static final class GeneratedCodec<T extends CustomPacketPayload>
            implements StreamCodec<RegistryFriendlyByteBuf, T> {

        private final Class<T> cls;
        private final StreamCodec<RegistryFriendlyByteBuf, ?>[] componentCodecs;
        private final Constructor<T> ctor;
        private final java.lang.reflect.Method[] accessors;

        GeneratedCodec(Class<T> cls, StreamCodec<RegistryFriendlyByteBuf, ?>[] componentCodecs, Constructor<T> ctor) {
            this.cls = cls;
            this.componentCodecs = componentCodecs;
            this.ctor = ctor;
            RecordComponent[] components = cls.getRecordComponents();
            this.accessors = new java.lang.reflect.Method[components.length];
            for (int i = 0; i < components.length; i++) {
                accessors[i] = components[i].getAccessor();
                accessors[i].setAccessible(true);
            }
        }

        @Override
        @SuppressWarnings({"unchecked", "rawtypes"})
        public void encode(RegistryFriendlyByteBuf buf, T value) {
            try {
                for (int i = 0; i < componentCodecs.length; i++) {
                    Object component = accessors[i].invoke(value);
                    ((StreamCodec) componentCodecs[i]).encode(buf, component);
                }
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("encode failed for " + cls.getName(), e);
            }
        }

        @Override
        public T decode(RegistryFriendlyByteBuf buf) {
            try {
                Object[] values = new Object[componentCodecs.length];
                for (int i = 0; i < componentCodecs.length; i++) {
                    values[i] = componentCodecs[i].decode(buf);
                }
                return ctor.newInstance(values);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("decode failed for " + cls.getName(), e);
            }
        }
    }
}

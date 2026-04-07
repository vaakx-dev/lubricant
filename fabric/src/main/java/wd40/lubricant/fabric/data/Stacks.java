package wd40.lubricant.fabric.data;

import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import wd40.lubricant.api.data.DataKey;
import wd40.lubricant.core.data.Registered;
import wd40.lubricant.core.data.StackHelper;

import java.util.HashMap;
import java.util.Map;

// Fabric impl of StackHelper. Each registered DataKey spawns a DataComponentType
// in BuiltInRegistries.DATA_COMPONENT_TYPE. Vanilla handles persistence and
// network sync via the Codec + StreamCodec.
//
// Registration runs from Init impls' static blocks, triggered by
// Bootstrap.loadAllInit() inside Entry.onInitialize - which is the
// ModInitializer phase, before the data-component-type registry freezes.
public final class Stacks implements StackHelper {

    private final Map<ResourceLocation, DataComponentType<?>> byId = new HashMap<>();

    public Stacks() {}

    @Override
    public <T> void register(DataKey<T> key) {
        Registered.claim(key, Registered.Facade.STACKS);
        StreamCodec<RegistryFriendlyByteBuf, T> stream = ByteBufCodecs.fromCodecWithRegistries(key.codec());
        DataComponentType<T> type = DataComponentType.<T>builder()
                .persistent(key.codec())
                .networkSynchronized(stream)
                .build();
        Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, key.id(), type);
        byId.put(key.id(), type);
    }

    @Override
    public <T> T get(ItemStack stack, DataKey<T> key) {
        T value = stack.get(typeOf(key));
        return value != null ? value : key.defaultValue();
    }

    @Override
    public <T> void set(ItemStack stack, DataKey<T> key, T value) {
        stack.set(typeOf(key), value);
    }

    @Override
    public <T> boolean has(ItemStack stack, DataKey<T> key) {
        return stack.has(typeOf(key));
    }

    @Override
    public <T> void remove(ItemStack stack, DataKey<T> key) {
        stack.remove(typeOf(key));
    }

    @SuppressWarnings("unchecked")
    private <T> DataComponentType<T> typeOf(DataKey<T> key) {
        DataComponentType<?> raw = byId.get(key.id());
        if (raw == null) {
            throw new IllegalStateException(
                    "DataKey " + key.id() + " not registered as item-attachable - call StackData.register(...) during init");
        }
        return (DataComponentType<T>) raw;
    }
}

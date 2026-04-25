package wd40.lubricant.neoforge.data;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.RegisterEvent;
import wd40.lubricant.api.common.data.DataKey;
import wd40.lubricant.core.data.Registered;
import wd40.lubricant.core.data.StackHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// NeoForge impl of StackHelper. NeoForge funnels vanilla registry writes
// through RegisterEvent on the mod bus, so we queue DataKey registrations from
// Init static blocks and drain them inside a listener attached to lubricant's
// mod bus.
//
// Entry wires the listener: lubricantBus.addListener(INSTANCE::onRegister)
// after Bootstrap.loadAllInit() has filled the queue.
public final class Stacks implements StackHelper {

    public static volatile Stacks INSTANCE;

    private final List<DataKey<?>> pending = new ArrayList<>();
    private final Map<ResourceLocation, DataComponentType<?>> byId = new HashMap<>();

    public Stacks() {
        INSTANCE = this;
    }

    @Override
    public <T> void register(DataKey<T> key) {
        Registered.claim(key, Registered.Facade.STACKS);
        pending.add(key);
    }

    public void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.DATA_COMPONENT_TYPE)) return;
        for (DataKey<?> key : pending) registerOne(event, key);
        pending.clear();
    }

    private <T> void registerOne(RegisterEvent event, DataKey<T> key) {
        StreamCodec<RegistryFriendlyByteBuf, T> stream = ByteBufCodecs.fromCodecWithRegistries(key.codec());
        DataComponentType<T> type = DataComponentType.<T>builder()
                .persistent(key.codec())
                .networkSynchronized(stream)
                .build();
        event.register(Registries.DATA_COMPONENT_TYPE, key.id(), () -> type);
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

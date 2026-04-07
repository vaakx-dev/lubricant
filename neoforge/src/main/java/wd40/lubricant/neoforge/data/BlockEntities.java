package wd40.lubricant.neoforge.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import wd40.lubricant.api.data.DataKey;
import wd40.lubricant.core.data.BlockEntityHelper;
import wd40.lubricant.core.data.Registered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// NeoForge impl of BlockEntityHelper. Like the Stacks impl, queues DataKey
// registrations from Init static blocks and drains them in a RegisterEvent
// listener wired by Entry. AttachmentType<T> is registered into
// NeoForgeRegistries.ATTACHMENT_TYPES.
public final class BlockEntities implements BlockEntityHelper {

    public static volatile BlockEntities INSTANCE;

    private final List<DataKey<?>> pending = new ArrayList<>();
    private final Map<ResourceLocation, AttachmentType<?>> byId = new HashMap<>();

    public BlockEntities() {
        INSTANCE = this;
    }

    @Override
    public <T> void register(DataKey<T> key) {
        Registered.claim(key, Registered.Facade.BLOCK_ENTITIES);
        pending.add(key);
    }

    public void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(NeoForgeRegistries.Keys.ATTACHMENT_TYPES)) return;
        for (DataKey<?> key : pending) registerOne(event, key);
        pending.clear();
    }

    private <T> void registerOne(RegisterEvent event, DataKey<T> key) {
        AttachmentType<T> type = AttachmentType.<T>builder(key::defaultValue)
                .serialize(key.codec())
                .build();
        event.register(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, key.id(), () -> type);
        byId.put(key.id(), type);
    }

    @Override
    public <T> T get(BlockEntity holder, DataKey<T> key) {
        T value = holder.getData(typeOf(key));
        return value != null ? value : key.defaultValue();
    }

    @Override
    public <T> void set(BlockEntity holder, DataKey<T> key, T value) {
        holder.setData(typeOf(key), value);
    }

    @Override
    public <T> boolean has(BlockEntity holder, DataKey<T> key) {
        return holder.hasData(typeOf(key));
    }

    @Override
    public <T> void remove(BlockEntity holder, DataKey<T> key) {
        holder.removeData(typeOf(key));
    }

    @SuppressWarnings("unchecked")
    private <T> AttachmentType<T> typeOf(DataKey<T> key) {
        AttachmentType<?> raw = byId.get(key.id());
        if (raw == null) {
            throw new IllegalStateException(
                    "DataKey " + key.id() + " not registered as block-entity-attachable - call BlockEntityData.register(...) during init");
        }
        return (AttachmentType<T>) raw;
    }
}

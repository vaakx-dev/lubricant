package wd40.lubricant.neoforge.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.registries.RegisterEvent;
import wd40.lubricant.api.data.DataKey;
import wd40.lubricant.internal.data.EntityHelper;
import wd40.lubricant.internal.data.Registered;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// NeoForge impl of EntityHelper. Same shape as BlockEntities - vanilla
// NeoForge AttachmentType is holder-agnostic; the IAttachmentHolder interface
// is implemented by both BlockEntity and Entity.
public final class Entities implements EntityHelper {

    public static volatile Entities INSTANCE;

    private final List<DataKey<?>> pending = new ArrayList<>();
    private final Map<ResourceLocation, AttachmentType<?>> byId = new HashMap<>();

    public Entities() {
        INSTANCE = this;
    }

    @Override
    public <T> void register(DataKey<T> key) {
        Registered.claim(key, Registered.Facade.ENTITIES);
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
    public <T> T get(Entity holder, DataKey<T> key) {
        T value = holder.getData(typeOf(key));
        return value != null ? value : key.defaultValue();
    }

    @Override
    public <T> void set(Entity holder, DataKey<T> key, T value) {
        holder.setData(typeOf(key), value);
    }

    @Override
    public <T> boolean has(Entity holder, DataKey<T> key) {
        return holder.hasData(typeOf(key));
    }

    @Override
    public <T> void remove(Entity holder, DataKey<T> key) {
        holder.removeData(typeOf(key));
    }

    @SuppressWarnings("unchecked")
    private <T> AttachmentType<T> typeOf(DataKey<T> key) {
        AttachmentType<?> raw = byId.get(key.id());
        if (raw == null) {
            throw new IllegalStateException(
                    "DataKey " + key.id() + " not registered as entity-attachable - call EntityData.register(...) during init");
        }
        return (AttachmentType<T>) raw;
    }
}

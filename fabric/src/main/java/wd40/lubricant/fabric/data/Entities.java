package wd40.lubricant.fabric.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import wd40.lubricant.api.common.data.DataKey;
import wd40.lubricant.core.data.EntityHelper;
import wd40.lubricant.core.data.Registered;

import java.util.HashMap;
import java.util.Map;

// Fabric impl of EntityHelper. Same shape as BlockEntities - fabric-api's
// AttachmentType is holder-agnostic; the AttachmentTarget interface is
// implemented by both BlockEntity and Entity. Per-facade Key isolation is
// enforced by Registered.claim().
public final class Entities implements EntityHelper {

    private final Map<ResourceLocation, AttachmentType<?>> byId = new HashMap<>();

    public Entities() {}

    @Override
    public <T> void register(DataKey<T> key) {
        Registered.claim(key, Registered.Facade.ENTITIES);
        AttachmentRegistry.Builder<T> builder = AttachmentRegistry.<T>builder()
                .persistent(key.codec());
        if (key.defaultValue() != null) builder.initializer(key::defaultValue);
        AttachmentType<T> type = builder.buildAndRegister(key.id());
        byId.put(key.id(), type);
    }

    @Override
    public <T> T get(Entity holder, DataKey<T> key) {
        T value = holder.getAttached(typeOf(key));
        return value != null ? value : key.defaultValue();
    }

    @Override
    public <T> void set(Entity holder, DataKey<T> key, T value) {
        holder.setAttached(typeOf(key), value);
    }

    @Override
    public <T> boolean has(Entity holder, DataKey<T> key) {
        return holder.hasAttached(typeOf(key));
    }

    @Override
    public <T> void remove(Entity holder, DataKey<T> key) {
        holder.removeAttached(typeOf(key));
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

package wd40.lubricant.fabric.data;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import wd40.lubricant.api.data.Key;
import wd40.lubricant.core.data.BlockEntityHelper;
import wd40.lubricant.core.data.Registered;

import java.util.HashMap;
import java.util.Map;

// Fabric impl of BlockEntityHelper. Each registered Key spawns an
// AttachmentType via fabric-api's AttachmentRegistry. Persistence + default
// flow through the Codec/initializer on the builder.
public final class BlockEntities implements BlockEntityHelper {

    private final Map<ResourceLocation, AttachmentType<?>> byId = new HashMap<>();

    public BlockEntities() {}

    @Override
    public <T> void register(Key<T> key) {
        Registered.claim(key, Registered.Facade.BLOCK_ENTITIES);
        AttachmentRegistry.Builder<T> builder = AttachmentRegistry.<T>builder()
                .persistent(key.codec());
        if (key.defaultValue() != null) builder.initializer(key::defaultValue);
        AttachmentType<T> type = builder.buildAndRegister(key.id());
        byId.put(key.id(), type);
    }

    @Override
    public <T> T get(BlockEntity holder, Key<T> key) {
        T value = holder.getAttached(typeOf(key));
        return value != null ? value : key.defaultValue();
    }

    @Override
    public <T> void set(BlockEntity holder, Key<T> key, T value) {
        holder.setAttached(typeOf(key), value);
    }

    @Override
    public <T> boolean has(BlockEntity holder, Key<T> key) {
        return holder.hasAttached(typeOf(key));
    }

    @Override
    public <T> void remove(BlockEntity holder, Key<T> key) {
        holder.removeAttached(typeOf(key));
    }

    @SuppressWarnings("unchecked")
    private <T> AttachmentType<T> typeOf(Key<T> key) {
        AttachmentType<?> raw = byId.get(key.id());
        if (raw == null) {
            throw new IllegalStateException(
                    "Key " + key.id() + " not registered as block-entity-attachable - call BlockEntities.register(...) during init");
        }
        return (AttachmentType<T>) raw;
    }
}

package wd40.lubricant.api.entity;
import wd40.lubricant.api.data.DataKey;

import net.minecraft.world.entity.Entity;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;

/**
 * Typed data attached to an {@link Entity}. Backed by AttachmentType on
 * 1.21+ (fabric-api on Fabric, NeoForge attachments on NeoForge), by entity
 * NBT under the key's namespaced id on 1.20.
 *
 * <p>Register every {@link DataKey} during mod init - typically from a static
 * block in an {@link wd40.lubricant.api.init.CommonInit} implementation. The same
 * {@link DataKey} cannot be registered on more than one facade (Stacks /
 * BlockEntityData / EntityData); doing so throws to surface modder mistakes
 * early. Declare separate {@link DataKey}s with different paths if similar data
 * is needed on multiple holder kinds.</p>
 *
 * <pre>{@code
 * static { EntityData.register(MyKeys.OWNER); }
 *
 * UUID owner = EntityData.get(player, MyKeys.OWNER);
 * EntityData.set(player, MyKeys.OWNER, player.getUUID());
 * }</pre>
 *
 * <p>Vanilla persists entity attachments through {@code addAdditionalSaveData}
 * so values survive logout / chunk unload / server restart automatically.</p>
 */
public final class EntityData {

    private EntityData() {}

    /** Declare {@code key} as entity-attachable. Idempotent within a single mod boot. */
    public static <T> void register(DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.entities().register(key);
    }

    /** Read the current value, or {@link DataKey#defaultValue} if absent. */
    public static <T> T get(Entity holder, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return key.defaultValue();
        return Services.entities().get(holder, key);
    }

    /** Replace the value on {@code holder}. The loader marks the holder dirty. */
    public static <T> void set(Entity holder, DataKey<T> key, T value) {
        if (Datagen.IS_DATAGEN) return;
        Services.entities().set(holder, key, value);
    }

    /** True iff the key has been explicitly set on this holder. */
    public static <T> boolean has(Entity holder, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return false;
        return Services.entities().has(holder, key);
    }

    /** Clear the key from {@code holder}. Subsequent {@code get} returns the default. */
    public static <T> void remove(Entity holder, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.entities().remove(holder, key);
    }
}

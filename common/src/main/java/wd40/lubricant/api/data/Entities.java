package wd40.lubricant.api.data;

import net.minecraft.world.entity.Entity;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

/**
 * Typed data attached to an {@link Entity}. Backed by AttachmentType on
 * 1.21+ (fabric-api on Fabric, NeoForge attachments on NeoForge), by entity
 * NBT under the key's namespaced id on 1.20.
 *
 * <p>Register every {@link Key} during mod init - typically from a static
 * block in an {@link wd40.lubricant.api.Init} implementation. The same
 * {@link Key} cannot be registered on more than one facade (Stacks /
 * BlockEntities / Entities); doing so throws to surface modder mistakes
 * early. Declare separate {@link Key}s with different paths if similar data
 * is needed on multiple holder kinds.</p>
 *
 * <pre>{@code
 * static { Entities.register(MyKeys.OWNER); }
 *
 * UUID owner = Entities.get(player, MyKeys.OWNER);
 * Entities.set(player, MyKeys.OWNER, player.getUUID());
 * }</pre>
 *
 * <p>Vanilla persists entity attachments through {@code addAdditionalSaveData}
 * so values survive logout / chunk unload / server restart automatically.</p>
 */
public final class Entities {

    private Entities() {}

    /** Declare {@code key} as entity-attachable. Idempotent within a single mod boot. */
    public static <T> void register(Key<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.entities().register(key);
    }

    /** Read the current value, or {@link Key#defaultValue} if absent. */
    public static <T> T get(Entity holder, Key<T> key) {
        if (Datagen.IS_DATAGEN) return key.defaultValue();
        return Services.entities().get(holder, key);
    }

    /** Replace the value on {@code holder}. The loader marks the holder dirty. */
    public static <T> void set(Entity holder, Key<T> key, T value) {
        if (Datagen.IS_DATAGEN) return;
        Services.entities().set(holder, key, value);
    }

    /** True iff the key has been explicitly set on this holder. */
    public static <T> boolean has(Entity holder, Key<T> key) {
        if (Datagen.IS_DATAGEN) return false;
        return Services.entities().has(holder, key);
    }

    /** Clear the key from {@code holder}. Subsequent {@code get} returns the default. */
    public static <T> void remove(Entity holder, Key<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.entities().remove(holder, key);
    }
}

package wd40.lubricant.api.data;

import net.minecraft.world.level.block.entity.BlockEntity;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

/**
 * Typed data attached to a {@link BlockEntity}. Backed by AttachmentType on
 * 1.21+ (fabric-api on Fabric, NeoForge attachments on NeoForge), by NBT
 * fields on the BlockEntity on 1.20.
 *
 * <p>Register every {@link Key} during mod init - typically from a static
 * block in an {@link wd40.lubricant.api.Init} implementation. The same
 * {@link Key} cannot be registered on more than one facade (Stacks /
 * BlockEntities / Entities); doing so throws to surface modder mistakes
 * early. Declare separate {@link Key}s with different paths if similar data
 * is needed on multiple holder kinds.</p>
 *
 * <pre>{@code
 * static { BlockEntities.register(MyKeys.LINKED); }
 *
 * BlockPos linked = BlockEntities.get(blockEntity, MyKeys.LINKED);
 * BlockEntities.set(blockEntity, MyKeys.LINKED, newPos);
 * }</pre>
 *
 * <p>The loader marks the BlockEntity dirty automatically on {@code set} -
 * no manual {@code setChanged()} call is needed.</p>
 */
public final class BlockEntities {

    private BlockEntities() {}

    /** Declare {@code key} as block-entity-attachable. Idempotent within a single mod boot. */
    public static <T> void register(Key<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.blockEntities().register(key);
    }

    /** Read the current value, or {@link Key#defaultValue} if absent. */
    public static <T> T get(BlockEntity holder, Key<T> key) {
        if (Datagen.IS_DATAGEN) return key.defaultValue();
        return Services.blockEntities().get(holder, key);
    }

    /** Replace the value on {@code holder}. The loader marks the holder dirty. */
    public static <T> void set(BlockEntity holder, Key<T> key, T value) {
        if (Datagen.IS_DATAGEN) return;
        Services.blockEntities().set(holder, key, value);
    }

    /** True iff the key has been explicitly set on this holder. */
    public static <T> boolean has(BlockEntity holder, Key<T> key) {
        if (Datagen.IS_DATAGEN) return false;
        return Services.blockEntities().has(holder, key);
    }

    /** Clear the key from {@code holder}. Subsequent {@code get} returns the default. */
    public static <T> void remove(BlockEntity holder, Key<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.blockEntities().remove(holder, key);
    }
}

package wd40.lubricant.api.common.data;

import net.minecraft.world.level.block.entity.BlockEntity;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

/**
 * Typed data attached to a {@link BlockEntity}. Backed by AttachmentType on
 * 1.21+ (fabric-api on Fabric, NeoForge attachments on NeoForge), by NBT
 * fields on the BlockEntity on 1.20.
 *
 * <p>Register every {@link DataKey} during mod init - typically from a static
 * block in an {@link wd40.lubricant.api.common.CommonInit} implementation. The same
 * {@link DataKey} cannot be registered on more than one facade (Stacks /
 * BlockEntityData / Entities); doing so throws to surface modder mistakes
 * early. Declare separate {@link DataKey}s with different paths if similar data
 * is needed on multiple holder kinds.</p>
 *
 * <pre>{@code
 * static { BlockEntityData.register(MyKeys.LINKED); }
 *
 * BlockPos linked = BlockEntityData.get(blockEntity, MyKeys.LINKED);
 * BlockEntityData.set(blockEntity, MyKeys.LINKED, newPos);
 * }</pre>
 *
 * <p>The loader marks the BlockEntity dirty automatically on {@code set} -
 * no manual {@code setChanged()} call is needed.</p>
 */
public final class BlockEntityData {

    private BlockEntityData() {}

    /** Declare {@code key} as block-entity-attachable. Idempotent within a single mod boot. */
    public static <T> void register(DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.blockEntities().register(key);
    }

    /** Read the current value, or {@link DataKey#defaultValue} if absent. */
    public static <T> T get(BlockEntity holder, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return key.defaultValue();
        return Services.blockEntities().get(holder, key);
    }

    /** Replace the value on {@code holder}. The loader marks the holder dirty. */
    public static <T> void set(BlockEntity holder, DataKey<T> key, T value) {
        if (Datagen.IS_DATAGEN) return;
        Services.blockEntities().set(holder, key, value);
    }

    /** True iff the key has been explicitly set on this holder. */
    public static <T> boolean has(BlockEntity holder, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return false;
        return Services.blockEntities().has(holder, key);
    }

    /** Clear the key from {@code holder}. Subsequent {@code get} returns the default. */
    public static <T> void remove(BlockEntity holder, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.blockEntities().remove(holder, key);
    }
}

package wd40.lubricant.core.data;

import net.minecraft.world.level.block.entity.BlockEntity;
import wd40.lubricant.api.data.DataKey;

/**
 * Loader-specific source of BlockEntity data ops backing the public
 * {@link wd40.lubricant.api.data.BlockEntityData} facade. One implementation
 * per loader, discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.data.BlockEntities}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.data.BlockEntities}</li>
 * </ul>
 */
public interface BlockEntityHelper {

    <T> void register(DataKey<T> key);

    <T> T get(BlockEntity holder, DataKey<T> key);

    <T> void set(BlockEntity holder, DataKey<T> key, T value);

    <T> boolean has(BlockEntity holder, DataKey<T> key);

    <T> void remove(BlockEntity holder, DataKey<T> key);
}

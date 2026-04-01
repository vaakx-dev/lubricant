package wd40.lubricant.core.data;

import net.minecraft.world.level.block.entity.BlockEntity;
import wd40.lubricant.api.data.Key;

/**
 * Loader-specific source of BlockEntity data ops backing the public
 * {@link wd40.lubricant.api.data.BlockEntities} facade. One implementation
 * per loader, discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.data.BlockEntities}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.data.BlockEntities}</li>
 * </ul>
 */
public interface BlockEntityHelper {

    <T> void register(Key<T> key);

    <T> T get(BlockEntity holder, Key<T> key);

    <T> void set(BlockEntity holder, Key<T> key, T value);

    <T> boolean has(BlockEntity holder, Key<T> key);

    <T> void remove(BlockEntity holder, Key<T> key);
}

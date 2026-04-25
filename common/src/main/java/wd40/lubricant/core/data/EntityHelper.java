package wd40.lubricant.core.data;

import net.minecraft.world.entity.Entity;
import wd40.lubricant.api.common.data.DataKey;

/**
 * Loader-specific source of Entity data ops backing the public
 * {@link wd40.lubricant.api.common.data.EntityData} facade. One implementation
 * per loader, discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.data.Entities}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.data.Entities}</li>
 * </ul>
 */
public interface EntityHelper {

    <T> void register(DataKey<T> key);

    <T> T get(Entity holder, DataKey<T> key);

    <T> void set(Entity holder, DataKey<T> key, T value);

    <T> boolean has(Entity holder, DataKey<T> key);

    <T> void remove(Entity holder, DataKey<T> key);
}

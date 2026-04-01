package wd40.lubricant.core.data;

import net.minecraft.world.item.ItemStack;
import wd40.lubricant.api.data.Key;

/**
 * Loader-specific source of ItemStack data ops backing the public
 * {@link wd40.lubricant.api.data.Stacks} facade. One implementation per loader,
 * discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.data.Stacks}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.data.Stacks}</li>
 * </ul>
 *
 * <p>Each impl maps {@link Key} ids to the loader's native machinery -
 * DataComponentType on 1.21, NBT path on 1.20.</p>
 */
public interface StackHelper {

    <T> void register(Key<T> key);

    <T> T get(ItemStack stack, Key<T> key);

    <T> void set(ItemStack stack, Key<T> key, T value);

    <T> boolean has(ItemStack stack, Key<T> key);

    <T> void remove(ItemStack stack, Key<T> key);
}

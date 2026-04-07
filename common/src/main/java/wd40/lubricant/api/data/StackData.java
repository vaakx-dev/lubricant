package wd40.lubricant.api.data;

import net.minecraft.world.item.ItemStack;
import wd40.lubricant.core.Datagen;
import wd40.lubricant.core.Services;

/**
 * Typed data attached to an {@link ItemStack}. Backed by DataComponentType on
 * 1.21+, by NBT under the key's namespaced id on 1.20.
 *
 * <p>Register every {@link DataKey} during mod init - typically from a static
 * block in an {@link wd40.lubricant.api.Init} implementation. Reads and writes
 * after init are safe from any thread that already holds the stack reference.</p>
 *
 * <pre>{@code
 * static { StackData.register(MyKeys.CHARGE); }
 *
 * int charge = StackData.get(stack, MyKeys.CHARGE);
 * StackData.set(stack, MyKeys.CHARGE, charge + 1);
 * }</pre>
 */
public final class StackData {

    private StackData() {}

    /** Declare {@code key} as item-attachable. Idempotent within a single mod boot. */
    public static <T> void register(DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.stacks().register(key);
    }

    /** Read the current value, or {@link DataKey#defaultValue} if absent. */
    public static <T> T get(ItemStack stack, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return key.defaultValue();
        return Services.stacks().get(stack, key);
    }

    /** Replace the value on {@code stack}. */
    public static <T> void set(ItemStack stack, DataKey<T> key, T value) {
        if (Datagen.IS_DATAGEN) return;
        Services.stacks().set(stack, key, value);
    }

    /** True iff the key has been explicitly set on this stack (default-value-via-get returns true on miss). */
    public static <T> boolean has(ItemStack stack, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return false;
        return Services.stacks().has(stack, key);
    }

    /** Clear the key from {@code stack}. Subsequent {@code get} returns the default. */
    public static <T> void remove(ItemStack stack, DataKey<T> key) {
        if (Datagen.IS_DATAGEN) return;
        Services.stacks().remove(stack, key);
    }
}

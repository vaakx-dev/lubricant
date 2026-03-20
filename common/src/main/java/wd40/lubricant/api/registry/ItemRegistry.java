package wd40.lubricant.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import wd40.lubricant.internal.Services;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers {@link Item}s under one mod's namespace. Returned suppliers are
 * safe to declare as {@code static final} - they resolve to the actual
 * {@code Item} after lubricant has bound the registry.
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Items">Items wiki</a></p>
 */
public interface ItemRegistry {

    static ItemRegistry create(String modId) {
        return Services.registry().createItemRegistry(modId);
    }

    /** Calling {@code .get()} on the returned supplier before bind throws. */
    Supplier<Item> register(String path, Function<Item.Properties, Item> factory);

    /** Every id passed to {@link #register}, in registration order. Read at any phase. */
    List<ResourceLocation> ids();
}

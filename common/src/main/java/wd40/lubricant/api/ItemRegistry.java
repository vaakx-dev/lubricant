package wd40.lubricant.api;

import net.minecraft.world.item.Item;
import wd40.lubricant.internal.Services;

import java.util.function.Supplier;

// A handle for registering items under one mod's namespace.
//
// Get one with:
//   public static final ItemRegistry ITEMS = ItemRegistry.create(MyMod.ID);
//
// then for each item:
//   public static final Supplier<Item> WRENCH = ITEMS.register("wrench",
//           () -> new Item(new Item.Properties()));
//
// Lubricant calls bind() automatically at the right loader lifecycle moment, so
// mod authors don't touch it. After bind, WRENCH.get() returns the registered Item.
//
public interface ItemRegistry {

    static ItemRegistry create(String modId) {
        return Services.registry().createItemRegistry(modId);
    }

    Supplier<Item> register(String path, Supplier<Item> factory);
}

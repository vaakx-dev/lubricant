package wd40.lubricant.api.registry;

import net.minecraft.world.item.Item;
import wd40.lubricant.internal.Services;

import java.util.function.Function;
import java.util.function.Supplier;

// A handle for registering items under one mod's namespace.
//
// Get one with:
//   public static final ItemRegistry ITEMS = ItemRegistry.create(MyMod.ID);
//
// Then for each item:
//   public static final Supplier<Item> WRENCH = ITEMS.register("wrench",
//           props -> new Item(props.stacksTo(64)));
//
// The factory receives an Item.Properties pre-populated with the item's ResourceKey
// (required by MC 1.21+ for intrusive holder creation). Add your own settings via
// the builder methods, then construct your Item with that properties object.
//
// Lubricant calls bind() automatically at the right loader lifecycle moment - mod
// authors don't touch it. After bind, WRENCH.get() returns the registered Item.
public interface ItemRegistry {

    static ItemRegistry create(String modId) {
        return Services.registry().createItemRegistry(modId);
    }

    Supplier<Item> register(String path, Function<Item.Properties, Item> factory);
}

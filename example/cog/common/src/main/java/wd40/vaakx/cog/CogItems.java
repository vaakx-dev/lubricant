package wd40.vaakx.cog;

import net.minecraft.world.item.Item;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.ItemRegistry;

import java.util.function.Supplier;

public final class CogItems implements Init {
    public static final ItemRegistry ITEMS = ItemRegistry.create(CogMod.ID);

    public static final Supplier<Item> COG = ITEMS.register("cog",
            () -> new Item(new Item.Properties().stacksTo(64)));

    public static final Supplier<Item> GREASED_COG = ITEMS.register("greased_cog",
            () -> new Item(new Item.Properties().stacksTo(1).fireResistant()));

    public CogItems() {}
}

package wd40.vaakx.cog.items;

import net.minecraft.world.item.Item;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.ItemRegistry;
import wd40.vaakx.cog.CogMod;

import java.util.function.Supplier;

public final class Items implements Init {
    public static final ItemRegistry ITEMS = ItemRegistry.create(CogMod.ID);

    public static final Supplier<Item> COG = ITEMS.register("cog",
            props -> new Item(props.stacksTo(64)));

    public static final Supplier<Item> GREASED_COG = ITEMS.register("greased_cog",
            props -> new Item(props.stacksTo(1).fireResistant()));

    public Items() {}
}

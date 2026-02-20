package wd40.vaakx.cog.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.ItemRegistry;
import wd40.vaakx.cog.CogMod;
import wd40.vaakx.cog.blocks.Blocks;

import java.util.function.Supplier;

public final class Items implements Init {
    public static final ItemRegistry ITEMS = ItemRegistry.create(CogMod.ID);

    public static final Supplier<Item> COG = ITEMS.register("cog",
            props -> new Item(props.stacksTo(64)));

    public static final Supplier<Item> GREASED_COG = ITEMS.register("greased_cog",
            props -> new Item(props.stacksTo(1).fireResistant()));

    // BlockItem for the gear block - lets you place it from inventory.
    // Uses the same path as the block, so the registered ID is also "cog:gear".
    public static final Supplier<Item> GEAR = ITEMS.register("gear",
            props -> new BlockItem(Blocks.GEAR.get(), props));

    public Items() {}
}

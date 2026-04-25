package wd40.vaakx.cog.common.items;

import com.mojang.serialization.Codec;
import net.minecraft.world.item.Item;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.data.DataKey;
import wd40.lubricant.api.data.DataKeys;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.vaakx.cog.Cog;

/**
 * Cog's item registrations + the item-attached data {@link DataKey}s for them.
 * Behavior subscribers (e.g. itemUse handlers) live in
 * {@link wd40.vaakx.cog.server.Server} - this file declares; that file acts.
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/Items">Items wiki</a>.</p>
 */
public final class Items implements Init {

    public static final ItemRegistry ITEMS = ItemRegistry.create(Cog.ID);

    /** A regular item, max stack 64. Resource id: {@code cog:cog}. */
    public static final Item COG = ITEMS.register("cog",
            props -> new Item(props.stacksTo(64)));

    /** A "tool" style item: stacks to 1, doesn't burn in lava. Resource id: {@code cog:greased_cog}. */
    public static final Item GREASED_COG = ITEMS.register("greased_cog",
            props -> new Item(props.stacksTo(1).fireResistant()));

    private static final DataKeys KEYS = DataKeys.create(Cog.ID);

    /** Click-counter on a greased_cog ItemStack. Persists across logout/world reload. */
    public static final DataKey<Integer> CHARGE = KEYS.stack("charge", Codec.INT, 0);

    public Items() {}
}

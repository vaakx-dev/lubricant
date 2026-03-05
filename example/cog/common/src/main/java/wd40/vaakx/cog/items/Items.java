package wd40.vaakx.cog.items;

import net.minecraft.world.item.Item;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.vaakx.cog.Cog;

import java.util.function.Supplier;

/**
 * All of cog's plain items. Block-items live in {@link wd40.vaakx.cog.blocks.Blocks}
 * because they're tied to a block, not registered here.
 *
 * <h3>How lubricant picks this up</h3>
 *
 * The {@code implements Init} marker plus an entry in
 * {@code META-INF/services/wd40.lubricant.api.Init} tells lubricant to
 * force-load this class during its own startup. The static fields below run
 * at that moment, queueing each {@link ItemRegistry#register} call into
 * lubricant's per-mod registry. Lubricant then commits the queue to vanilla's
 * {@code BuiltInRegistries.ITEM} (Fabric) or attaches it to NeoForge's
 * registry event (NeoForge) at the loader-correct lifecycle moment.
 *
 * <h3>The factory pattern</h3>
 *
 * Each {@code register} takes:
 *   - a path string (becomes "{@code cog:<path>}")
 *   - a {@code Function<Item.Properties, Item>} that builds the Item
 *
 * Lubricant calls the factory once during bind, with a fresh {@code Item.Properties}.
 * Add settings via the builder methods on {@code props} - {@code stacksTo},
 * {@code fireResistant}, {@code food}, etc. - then construct your {@code Item}
 * (or subclass) and return it.
 *
 * <h3>The Supplier&lt;Item&gt; reference</h3>
 *
 * The static field is a {@code Supplier<Item>} so it's safe to declare at class
 * load time before lubricant has bound anything. Calling {@code COG.get()} after
 * bind returns the registered Item; calling it before bind throws.
 */
public final class Items implements Init {

    /** Per-mod registry handle. Multiple {@code ItemRegistry.create} calls are allowed. */
    public static final ItemRegistry ITEMS = ItemRegistry.create(Cog.ID);

    /** A regular item, max stack 64. Resource id: {@code cog:cog}. */
    public static final Supplier<Item> COG = ITEMS.register("cog",
            props -> new Item(props.stacksTo(64)));

    /** A "tool" style item: stacks to 1, doesn't burn in lava. Resource id: {@code cog:greased_cog}. */
    public static final Supplier<Item> GREASED_COG = ITEMS.register("greased_cog",
            props -> new Item(props.stacksTo(1).fireResistant()));

    /** Public no-arg constructor required by ServiceLoader. Body intentionally empty. */
    public Items() {}
}

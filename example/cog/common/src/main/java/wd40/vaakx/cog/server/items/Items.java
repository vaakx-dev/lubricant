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
 * <p>Two pieces wire this class to lubricant's startup:</p>
 * <ol>
 *   <li>{@code implements Init} marks it as something lubricant should force-load.</li>
 *   <li>{@code META-INF/services/wd40.lubricant.api.Init} (in this mod's resources)
 *       lists this class's FQN so JDK ServiceLoader can find it.</li>
 * </ol>
 *
 * <p>At lubricant boot, ServiceLoader instantiates this class, which runs its
 * {@code <clinit>} - the static field initializers below. Each
 * {@link ItemRegistry#register} call queues into lubricant's per-mod registry,
 * and lubricant commits the queue to vanilla's {@code BuiltInRegistries.ITEM}
 * (Fabric) or to NeoForge's DeferredRegister event at the right loader phase.</p>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/How-It-Works">How It
 * Works</a> for the end-to-end pipeline, or
 * <a href="https://github.com/vaakxxx/lubricant/wiki/Items">the Items wiki page</a>
 * for full reference.</p>
 *
 * <h3>The factory pattern</h3>
 *
 * <p>Each {@code register} takes a path string (becomes {@code cog:<path>}) and
 * a {@code Function<Item.Properties, Item>} that builds the Item. Lubricant calls
 * the factory once during bind, with a fresh {@code Item.Properties}. Add
 * settings via builder methods ({@code stacksTo}, {@code fireResistant},
 * {@code food}, etc.) and construct your {@code Item} (or a subclass).</p>
 *
 * <h3>The Supplier reference</h3>
 *
 * <p>The static field is a {@code Supplier<Item>} so it's safe to declare at
 * class-load time, before the actual Item exists. Calling {@code COG.get()}
 * after bind returns the registered Item; calling it before bind throws.</p>
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

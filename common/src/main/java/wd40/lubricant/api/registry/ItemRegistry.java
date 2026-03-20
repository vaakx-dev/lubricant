package wd40.lubricant.api.registry;

import net.minecraft.world.item.Item;
import wd40.lubricant.internal.Services;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers {@link Item}s under one mod's namespace. Returned {@link Supplier}
 * references are safe to declare as {@code static final} fields - they resolve
 * to the actual {@code Item} instance after lubricant has bound the registry.
 *
 * <h3>Usage</h3>
 *
 * <pre>{@code
 * public static final ItemRegistry ITEMS = ItemRegistry.create("mymod");
 *
 * public static final Supplier<Item> WRENCH = ITEMS.register("wrench",
 *         props -> new Item(props.stacksTo(64)));
 * }</pre>
 *
 * <p>The factory receives a fresh {@link Item.Properties} instance and returns
 * the constructed {@code Item}. Calling {@code WRENCH.get()} before lubricant
 * has bound the registry throws {@link IllegalStateException}.</p>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/Items">the Items
 * wiki page</a> for full reference and patterns.</p>
 *
 * @see BlockRegistry for blocks (with auto-generated BlockItems)
 */
public interface ItemRegistry {

    /** Creates a per-mod registration handle. Multiple {@code create} calls per modId are allowed but rarely useful. */
    static ItemRegistry create(String modId) {
        return Services.registry().createItemRegistry(modId);
    }

    /**
     * Registers an item at {@code <modId>:<path>}.
     *
     * @param path    the path component of the resource location (modId is set at {@link #create})
     * @param factory builds the {@code Item} from a fresh {@code Item.Properties}
     * @return a {@code Supplier} that resolves to the registered {@code Item} after bind
     */
    Supplier<Item> register(String path, Function<Item.Properties, Item> factory);
}

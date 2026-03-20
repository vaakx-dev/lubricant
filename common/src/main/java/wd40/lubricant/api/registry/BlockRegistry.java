package wd40.lubricant.api.registry;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wd40.lubricant.internal.Services;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers {@link Block}s under one mod's namespace. The standard {@link #register}
 * call also registers a matching {@code BlockItem} so the block shows up in
 * inventory and can be placed by hand. Use {@link #registerNoItem} for blocks
 * that should never appear in inventory (fluid placeholders, technical blocks, etc).
 *
 * <h3>Usage</h3>
 *
 * <pre>{@code
 * public static final BlockRegistry BLOCKS = BlockRegistry.create("mymod");
 *
 * public static final Supplier<Block> GEAR = BLOCKS.register("gear",
 *         props -> new Block(props.strength(2.0f)));
 *
 * public static final Supplier<Block> GEAR_HEAD = BLOCKS.registerNoItem("gear_head",
 *         props -> new Block(props.strength(50.0f)));
 * }</pre>
 *
 * <p>Lubricant binds blocks before items, so an {@code Item} factory in your
 * {@code Items.java} can reference {@code Blocks.GEAR.get()} to build a custom
 * BlockItem if you need one beyond the auto-generated default.</p>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/Blocks">the Blocks
 * wiki page</a> for full reference and patterns.</p>
 *
 * @see ItemRegistry for plain items
 */
public interface BlockRegistry {

    /** Creates a per-mod registration handle. */
    static BlockRegistry create(String modId) {
        return Services.registry().createBlockRegistry(modId);
    }

    /**
     * Registers a block AND a matching BlockItem with the same id.
     *
     * @param path    path component of the resource location
     * @param factory builds the {@code Block} from a fresh {@code BlockBehaviour.Properties}
     * @return a {@code Supplier} for the block; the auto-BlockItem isn't exposed here
     */
    Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory);

    /**
     * Registers a block with no BlockItem. The block exists in the world but
     * cannot be obtained in inventory (use for fluid blocks, fire/air-style blocks,
     * technical blocks players shouldn't hold).
     */
    Supplier<Block> registerNoItem(String path, Function<BlockBehaviour.Properties, Block> factory);
}

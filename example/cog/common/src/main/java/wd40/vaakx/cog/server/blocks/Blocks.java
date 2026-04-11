package wd40.vaakx.cog.server.blocks;

import net.minecraft.world.level.block.Block;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.vaakx.cog.Cog;

/**
 * All of cog's blocks (and their auto-generated BlockItems). Same lifecycle as
 * {@link wd40.vaakx.cog.server.items.Items} - see Items.java for the full explanation.
 *
 * <p>{@code register} adds a matching BlockItem; {@code registerNoItem} doesn't
 * (use for multi-block halves, technical blocks, anything you shouldn't hold).</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Blocks">Blocks wiki</a></p>
 */
public final class Blocks implements Init {

    public static final BlockRegistry BLOCKS = BlockRegistry.create(Cog.ID);

    public static final Block GEAR = BLOCKS.register("gear",
            props -> new Block(props.strength(2.0f).destroyTime(2.0f)));

    /** No BlockItem - {@code /give @s cog:gear_head} returns "Unknown item"; {@code /setblock} works. */
    public static final Block GEAR_HEAD = BLOCKS.registerNoItem("gear_head",
            props -> new Block(props.strength(50.0f).destroyTime(50.0f)));

    /** Right-clicking increments a counter stored on the BlockEntity via lubricant attached data. The Counter type is preserved by the generic register signature. */
    public static final Counter COUNTER = BLOCKS.register("counter",
            props -> new Counter(props.strength(1.0f).destroyTime(1.0f)));

    public Blocks() {}
}

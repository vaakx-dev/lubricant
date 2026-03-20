package wd40.vaakx.cog.blocks;

import net.minecraft.world.level.block.Block;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.vaakx.cog.Cog;

import java.util.function.Supplier;

/**
 * All of cog's blocks (and their auto-generated BlockItems). Same lifecycle as
 * {@link wd40.vaakx.cog.items.Items} - see Items.java for the full explanation.
 *
 * <p>{@code register} adds a matching BlockItem; {@code registerNoItem} doesn't
 * (use for multi-block halves, technical blocks, anything you shouldn't hold).</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Blocks">Blocks wiki</a></p>
 */
public final class Blocks implements Init {

    public static final BlockRegistry BLOCKS = BlockRegistry.create(Cog.ID);

    public static final Supplier<Block> GEAR = BLOCKS.register("gear",
            props -> new Block(props.strength(2.0f).destroyTime(2.0f)));

    /** No BlockItem - {@code /give @s cog:gear_head} returns "Unknown item"; {@code /setblock} works. */
    public static final Supplier<Block> GEAR_HEAD = BLOCKS.registerNoItem("gear_head",
            props -> new Block(props.strength(50.0f).destroyTime(50.0f)));

    public Blocks() {}
}

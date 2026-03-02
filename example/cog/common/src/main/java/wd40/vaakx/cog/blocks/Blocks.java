package wd40.vaakx.cog.blocks;

import net.minecraft.world.level.block.Block;
import wd40.lubricant.api.BlockRegistry;
import wd40.lubricant.api.Init;
import wd40.vaakx.cog.CogMod;

import java.util.function.Supplier;

public final class Blocks implements Init {
    public static final BlockRegistry BLOCKS = BlockRegistry.create(CogMod.ID);

    // Standard block: ships with a BlockItem at cog:gear.
    public static final Supplier<Block> GEAR = BLOCKS.register("gear",
            props -> new Block(props.strength(2.0f).destroyTime(2.0f)));

    // No-item block. Use registerNoItem when the block has no inventory entry of
    // its own, e.g.:
    //   - the second half of a multi-block (the top half of a tall plant or door,
    //     the head half of a bed). The "main" block has the BlockItem; secondary
    //     blocks are placed by the main block's place logic, not directly.
    //   - unobtainable blocks like vanilla's end_portal, nether_portal, piston_head.
    //     The block exists in worldgen / via /setblock, but never as an item.
    //
    // Here used as a demo only; in a real mod you'd pair this with placement logic.
    public static final Supplier<Block> GEAR_HEAD = BLOCKS.registerNoItem("gear_head",
            props -> new Block(props.strength(50.0f).destroyTime(50.0f)));

    public Blocks() {}
}

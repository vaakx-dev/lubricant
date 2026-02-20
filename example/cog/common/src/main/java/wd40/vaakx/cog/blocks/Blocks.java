package wd40.vaakx.cog.blocks;

import net.minecraft.world.level.block.Block;
import wd40.lubricant.api.BlockRegistry;
import wd40.lubricant.api.Init;
import wd40.vaakx.cog.CogMod;

import java.util.function.Supplier;

public final class Blocks implements Init {
    public static final BlockRegistry BLOCKS = BlockRegistry.create(CogMod.ID);

    public static final Supplier<Block> GEAR = BLOCKS.register("gear",
            props -> new Block(props.strength(2.0f).destroyTime(2.0f)));

    public Blocks() {}
}

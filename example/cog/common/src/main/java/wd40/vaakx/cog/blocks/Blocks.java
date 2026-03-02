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

    // No-item block: only placeable via /setblock cog:hidden_gear, never in inventory.
    public static final Supplier<Block> HIDDEN_GEAR = BLOCKS.registerNoItem("hidden_gear",
            props -> new Block(props.strength(50.0f).destroyTime(50.0f)));

    public Blocks() {}
}

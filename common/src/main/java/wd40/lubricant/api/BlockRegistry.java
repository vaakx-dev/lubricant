package wd40.lubricant.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wd40.lubricant.internal.Services;

import java.util.function.Function;
import java.util.function.Supplier;

// A handle for registering blocks under one mod's namespace.
//
// Get one with:
//   public static final BlockRegistry BLOCKS = BlockRegistry.create(MyMod.ID);
//
// Then for each block:
//   public static final Supplier<Block> GEAR = BLOCKS.register("gear",
//           props -> new Block(props.strength(2.0f).destroyTime(2.0f)));
//
// The factory receives a BlockBehaviour.Properties pre-populated with the block's
// ResourceKey (required by MC 1.21+).
//
// Lubricant calls bind automatically. After bind, GEAR.get() returns the registered Block.
public interface BlockRegistry {

    static BlockRegistry create(String modId) {
        return Services.registry().createBlockRegistry(modId);
    }

    Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory);
}

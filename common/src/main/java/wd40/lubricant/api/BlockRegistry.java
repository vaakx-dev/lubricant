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
// Standard register: also creates a BlockItem with the same id so the block
// shows up in inventories and can be placed by hand.
//
//   public static final Supplier<Block> GEAR = BLOCKS.register("gear",
//           props -> new Block(props.strength(2.0f).destroyTime(2.0f)));
//
// Both `cog:gear` (block) and `cog:gear` (BlockItem) get registered.
//
// Use registerNoItem for blocks that should never appear in inventory
// (e.g. fluid placeholder blocks, internal-only blocks, fire/air-style blocks).
//
// Lubricant calls bind automatically. After bind, GEAR.get() returns the registered Block.
public interface BlockRegistry {

    static BlockRegistry create(String modId) {
        return Services.registry().createBlockRegistry(modId);
    }

    /** Register a block AND a BlockItem with the same id. */
    Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory);

    /** Register a block without a corresponding BlockItem. */
    Supplier<Block> registerNoItem(String path, Function<BlockBehaviour.Properties, Block> factory);
}

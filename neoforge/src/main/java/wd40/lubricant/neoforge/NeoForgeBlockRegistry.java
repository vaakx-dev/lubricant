package wd40.lubricant.neoforge;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.registry.BlockRegistry;

import java.util.function.Function;
import java.util.function.Supplier;

// NeoForge implementation. Wraps DeferredRegister.Blocks for blocks and a separate
// DeferredRegister.Items for the BlockItems we create alongside.
final class NeoForgeBlockRegistry implements BlockRegistry {

    final String modId;
    final DeferredRegister.Blocks blocks;
    final DeferredRegister.Items blockItems;

    NeoForgeBlockRegistry(String modId) {
        this.modId = modId;
        this.blocks = DeferredRegister.createBlocks(modId);
        this.blockItems = DeferredRegister.createItems(modId);
    }

    @Override
    public Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory) {
        DeferredBlock<Block> block = blocks.registerBlock(path, factory);
        blockItems.registerSimpleBlockItem(block);
        return block;
    }

    @Override
    public Supplier<Block> registerNoItem(String path, Function<BlockBehaviour.Properties, Block> factory) {
        return blocks.registerBlock(path, factory);
    }

    void attach(IEventBus modBus) {
        blocks.register(modBus);
        blockItems.register(modBus);
    }
}

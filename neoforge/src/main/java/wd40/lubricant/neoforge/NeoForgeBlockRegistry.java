package wd40.lubricant.neoforge;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.BlockRegistry;

import java.util.function.Function;
import java.util.function.Supplier;

// NeoForge implementation. Wraps DeferredRegister.Blocks - registerBlock takes the
// factory directly, including ResourceKey injection.
final class NeoForgeBlockRegistry implements BlockRegistry {

    final String modId;
    final DeferredRegister.Blocks deferred;

    NeoForgeBlockRegistry(String modId) {
        this.modId = modId;
        this.deferred = DeferredRegister.createBlocks(modId);
    }

    @Override
    public Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory) {
        DeferredBlock<Block> ref = deferred.registerBlock(path, factory);
        return ref;
    }

    void attach(IEventBus modBus) {
        deferred.register(modBus);
    }
}

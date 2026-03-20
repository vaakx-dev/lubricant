package wd40.lubricant.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.registry.BlockRegistry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

// NeoForge implementation. Wraps DeferredRegister.Blocks for blocks and a separate
// DeferredRegister.Items for the BlockItems we create alongside.
final class NeoForgeBlockRegistry implements BlockRegistry {

    final String modId;
    final DeferredRegister.Blocks blocks;
    final DeferredRegister.Items blockItems;
    private final List<ResourceLocation> ids = new ArrayList<>();
    private final Set<ResourceLocation> noItem = new HashSet<>();

    NeoForgeBlockRegistry(String modId) {
        this.modId = modId;
        this.blocks = DeferredRegister.createBlocks(modId);
        this.blockItems = DeferredRegister.createItems(modId);
    }

    @Override
    public Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory) {
        ids.add(ResourceLocation.fromNamespaceAndPath(modId, path));
        DeferredBlock<Block> block = blocks.registerBlock(path, factory);
        blockItems.registerSimpleBlockItem(block);
        return block;
    }

    @Override
    public Supplier<Block> registerNoItem(String path, Function<BlockBehaviour.Properties, Block> factory) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
        ids.add(id);
        noItem.add(id);
        return blocks.registerBlock(path, factory);
    }

    @Override
    public List<ResourceLocation> ids() {
        return List.copyOf(ids);
    }

    @Override
    public Set<ResourceLocation> noItemIds() {
        return Set.copyOf(noItem);
    }

    void attach(IEventBus modBus) {
        blocks.register(modBus);
        blockItems.register(modBus);
    }
}

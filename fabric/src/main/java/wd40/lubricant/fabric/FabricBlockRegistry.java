package wd40.lubricant.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wd40.lubricant.api.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

// Fabric implementation. Each register() queues a block+item pair (or just a block
// for registerNoItem). Both are executed against the vanilla registries during bind().
final class FabricBlockRegistry implements BlockRegistry {

    private final String modId;
    private final List<Runnable> queued = new ArrayList<>();

    FabricBlockRegistry(String modId) {
        this.modId = modId;
    }

    @Override
    public Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory) {
        Supplier<Block> blockRef = registerBlock(path, factory);
        // Also queue a BlockItem with the same id.
        queued.add(() -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
            Item item = new BlockItem(blockRef.get(), new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, id, item);
        });
        return blockRef;
    }

    @Override
    public Supplier<Block> registerNoItem(String path, Function<BlockBehaviour.Properties, Block> factory) {
        return registerBlock(path, factory);
    }

    private Supplier<Block> registerBlock(String path, Function<BlockBehaviour.Properties, Block> factory) {
        AtomicReference<Block> ref = new AtomicReference<>();
        queued.add(() -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
            Block block = factory.apply(BlockBehaviour.Properties.of());
            Registry.register(BuiltInRegistries.BLOCK, id, block);
            ref.set(block);
        });
        return () -> {
            Block v = ref.get();
            if (v == null) {
                throw new IllegalStateException(
                        "Block " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return v;
        };
    }

    String modId() {
        return modId;
    }

    void bind() {
        for (Runnable r : queued) r.run();
        queued.clear();
    }
}

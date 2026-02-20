package wd40.lubricant.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wd40.lubricant.api.BlockRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

// Fabric implementation. Constructs the block via the user's factory, then registers
// it to BuiltInRegistries.BLOCK. Relies on fabric-api delaying the registry freeze
// past mod load.
final class FabricBlockRegistry implements BlockRegistry {

    private final String modId;
    private final List<Runnable> queued = new ArrayList<>();

    FabricBlockRegistry(String modId) {
        this.modId = modId;
    }

    @Override
    public Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory) {
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

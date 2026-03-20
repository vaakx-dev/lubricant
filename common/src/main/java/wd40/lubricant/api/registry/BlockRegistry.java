package wd40.lubricant.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers {@link Block}s under one mod's namespace. {@link #register} also
 * implies a matching BlockItem; {@link #registerNoItem} skips it.
 *
 * <p>Plain data - see {@link ItemRegistry} for the lifecycle. Loader entry
 * points read {@link #ALL} and commit to vanilla / NeoForge registries.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Blocks">Blocks wiki</a></p>
 */
public final class BlockRegistry {

    public static final List<BlockRegistry> ALL = new CopyOnWriteArrayList<>();

    public final String modId;
    public final List<Entry> entries = new ArrayList<>();
    public final Set<String> noItemPaths = new HashSet<>();

    public record Entry(String path, Function<BlockBehaviour.Properties, Block> factory, AtomicReference<Block> ref) {}

    public static BlockRegistry create(String modId) {
        BlockRegistry registry = new BlockRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private BlockRegistry(String modId) {
        this.modId = modId;
    }

    public Supplier<Block> register(String path, Function<BlockBehaviour.Properties, Block> factory) {
        return queue(path, factory);
    }

    public Supplier<Block> registerNoItem(String path, Function<BlockBehaviour.Properties, Block> factory) {
        noItemPaths.add(path);
        return queue(path, factory);
    }

    private Supplier<Block> queue(String path, Function<BlockBehaviour.Properties, Block> factory) {
        Entry entry = new Entry(path, factory, new AtomicReference<>());
        entries.add(entry);
        return () -> {
            Block block = entry.ref.get();
            if (block == null) {
                throw new IllegalStateException(
                        "Block " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return block;
        };
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            out.add(ResourceLocation.fromNamespaceAndPath(modId, entry.path));
        }
        return out;
    }

    public Set<ResourceLocation> noItemIds() {
        Set<ResourceLocation> out = new HashSet<>(noItemPaths.size());
        for (String path : noItemPaths) {
            out.add(ResourceLocation.fromNamespaceAndPath(modId, path));
        }
        return out;
    }
}

package wd40.lubricant.api.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Registers {@link BlockEntityType}s under one mod's namespace. Plain data -
 * see {@link ItemRegistry} for the lifecycle. Loader entry points read
 * {@link #ALL} and commit to vanilla / NeoForge registries.
 *
 * <p>The {@code factory} is a {@code BiFunction<BlockPos, BlockState, T>} -
 * typically a constructor reference like {@code MyBE::new}. The
 * {@code validBlocks} varargs lists the {@link Block}s that may instantiate
 * this BE; pass the suppliers returned by {@link BlockRegistry#register} so
 * the references resolve after binding.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Block-Entities">Block Entities wiki</a></p>
 */
public final class BlockEntityRegistry {

    public static final List<BlockEntityRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry<? extends BlockEntity>> entries = new ArrayList<>();

    /** One queued registration. Loader sets {@code ref} once the type exists. */
    public record Entry<T extends BlockEntity>(
            String path,
            BiFunction<BlockPos, BlockState, T> factory,
            List<Supplier<? extends Block>> validBlocks,
            AtomicReference<BlockEntityType<T>> ref) {}

    public static BlockEntityRegistry create(String modId) {
        BlockEntityRegistry registry = new BlockEntityRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private BlockEntityRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Queue a block-entity-type registration.
     *
     * @param path         path under the mod's namespace, e.g. {@code "counter"}
     * @param factory      constructor reference (e.g. {@code CounterBE::new})
     * @param validBlocks  block suppliers (typically the result of {@link BlockRegistry#register})
     */
    @SafeVarargs
    public final <T extends BlockEntity> Supplier<BlockEntityType<T>> register(
            String path,
            BiFunction<BlockPos, BlockState, T> factory,
            Supplier<? extends Block>... validBlocks) {
        Entry<T> entry = new Entry<>(path, factory, List.of(validBlocks), new AtomicReference<>());
        entries.add(entry);
        return () -> {
            BlockEntityType<T> type = entry.ref.get();
            if (type == null) {
                throw new IllegalStateException(
                        "BlockEntityType " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return type;
        };
    }

    public String modId() {
        return modId;
    }

    public List<Entry<? extends BlockEntity>> entries() {
        return Collections.unmodifiableList(entries);
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry<?> entry : entries) {
            out.add(ResourceLocation.fromNamespaceAndPath(modId, entry.path()));
        }
        return out;
    }
}

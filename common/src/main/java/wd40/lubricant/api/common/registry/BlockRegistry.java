package wd40.lubricant.api.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import wd40.lubricant.core.Datagen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Registers {@link Block}s under one mod's namespace. {@link #register} also
 * implies a matching BlockItem; {@link #registerNoItem} skips it.
 *
 * <p>Returns the constructed {@link Block} as a typed field (no Supplier wrapper).
 * The generic preserves custom block subclasses at the call site:
 * {@code public static final Counter MY = Blocks.register("counter", Counter::new);}.</p>
 *
 * <pre>{@code
 * public static final BlockRegistry BLOCKS = BlockRegistry.create("mymod");
 * public static final Block GEAR = BLOCKS.register("gear", props -> new Block(props));
 * }</pre>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Blocks">Blocks wiki</a></p>
 */
public final class BlockRegistry {

    public static final List<BlockRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry<?>> entries = new ArrayList<>();
    private final Set<String> noItemPaths = new HashSet<>();

    public static final class Entry<B extends Block> {
        private final String path;
        private final Function<BlockBehaviour.Properties, B> factory;
        private final B bound;

        Entry(String path, Function<BlockBehaviour.Properties, B> factory, B bound) {
            this.path = path;
            this.factory = factory;
            this.bound = bound;
        }

        public String path() { return path; }
        public Function<BlockBehaviour.Properties, B> factory() { return factory; }
        public B bound() { return bound; }
    }

    public static BlockRegistry create(String modId) {
        BlockRegistry registry = new BlockRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private BlockRegistry(String modId) {
        this.modId = modId;
    }

    public <B extends Block> B register(String path, Function<BlockBehaviour.Properties, B> factory) {
        return queue(path, factory);
    }

    public <B extends Block> B registerNoItem(String path, Function<BlockBehaviour.Properties, B> factory) {
        noItemPaths.add(path);
        return queue(path, factory);
    }

    private <B extends Block> B queue(String path, Function<BlockBehaviour.Properties, B> factory) {
        // Datagen JVM has no MC Bootstrap and would crash on Block construction. Datagen
        // providers only need ids/paths anyway, so we skip the factory and return null.
        if (Datagen.IS_DATAGEN) {
            entries.add(new Entry<>(path, factory, null));
            return null;
        }
        BlockBehaviour.Properties props = BlockBehaviour.Properties.of();
        B block = factory.apply(props);
        entries.add(new Entry<>(path, factory, block));
        return block;
    }

    public String modId() {
        return modId;
    }

    public List<Entry<?>> entries() {
        return Collections.unmodifiableList(entries);
    }

    /** Whether {@code register} for this path was {@code registerNoItem} (skip the BlockItem). */
    public boolean isNoItem(String path) {
        return noItemPaths.contains(path);
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry<?> entry : entries) {
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

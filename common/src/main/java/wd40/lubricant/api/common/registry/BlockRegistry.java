package wd40.lubricant.api.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

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
 * <p>Returns a {@link RegistrySupplier} - construction is deferred to the
 * loader's binding phase to satisfy NeoForge's frozen-registry rule. Access
 * the value with {@link RegistrySupplier#get} from runtime code (event
 * handlers, render lambdas, tab displayItems callbacks); the id is available
 * immediately via {@link RegistrySupplier#id}.</p>
 *
 * <pre>{@code
 * public static final BlockRegistry BLOCKS = BlockRegistry.create("mymod");
 * public static final RegistrySupplier<Block> GEAR = BLOCKS.register("gear",
 *         props -> new Block(props.strength(2.0f)));
 * }</pre>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Blocks">Blocks wiki</a></p>
 */
public final class BlockRegistry {

    public static final List<BlockRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry<? extends Block>> entries = new ArrayList<>();
    private final Set<String> noItemPaths = new HashSet<>();

    /** One queued registration. Loader writes {@code bound} during the binding phase. */
    public static final class Entry<B extends Block> implements RegistrySupplier<B> {
        private final String path;
        private final ResourceLocation id;
        private final Function<BlockBehaviour.Properties, B> factory;
        private B bound;

        Entry(String path, ResourceLocation id, Function<BlockBehaviour.Properties, B> factory) {
            this.path = path;
            this.id = id;
            this.factory = factory;
        }

        public String path() { return path; }
        public Function<BlockBehaviour.Properties, B> factory() { return factory; }
        public B bound() { return bound; }

        /** Loader writes the constructed Block here during binding. */
        public void setBound(B block) { this.bound = block; }

        @Override public B get() { return bound; }
        @Override public ResourceLocation id() { return id; }
    }

    public static BlockRegistry create(String modId) {
        BlockRegistry registry = new BlockRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private BlockRegistry(String modId) {
        this.modId = modId;
    }

    public <B extends Block> RegistrySupplier<B> register(String path, Function<BlockBehaviour.Properties, B> factory) {
        return queue(path, factory);
    }

    public <B extends Block> RegistrySupplier<B> registerNoItem(String path, Function<BlockBehaviour.Properties, B> factory) {
        noItemPaths.add(path);
        return queue(path, factory);
    }

    private <B extends Block> Entry<B> queue(String path, Function<BlockBehaviour.Properties, B> factory) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
        Entry<B> entry = new Entry<>(path, id, factory);
        entries.add(entry);
        return entry;
    }

    public String modId() {
        return modId;
    }

    public List<Entry<? extends Block>> entries() {
        return Collections.unmodifiableList(entries);
    }

    /** Whether {@code register} for this path was {@code registerNoItem} (skip the BlockItem). */
    public boolean isNoItem(String path) {
        return noItemPaths.contains(path);
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry<?> entry : entries) {
            out.add(entry.id);
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

package wd40.lubricant.api.common.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Registers {@link Item}s under one mod's namespace. Plain data - no SPI
 * lookup during {@code create} or {@code register}. The loader entry point
 * reads {@link #ALL} and constructs each {@link Entry} in its registration
 * phase.
 *
 * <p>Returns a {@link RegistrySupplier} - construction is deferred to the
 * loader's binding phase. Access the value with
 * {@link RegistrySupplier#get} from runtime code; the id is available
 * immediately via {@link RegistrySupplier#id}.</p>
 *
 * <pre>{@code
 * public static final ItemRegistry ITEMS = ItemRegistry.create("mymod");
 * public static final RegistrySupplier<Item> COG = ITEMS.register("cog",
 *         props -> new Item(props.stacksTo(64)));
 * }</pre>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Items">Items wiki</a></p>
 */
public final class ItemRegistry {

    /** Every registry instance ever created in this JVM, in creation order. */
    public static final List<ItemRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry<? extends Item>> entries = new ArrayList<>();

    /** One queued registration. Loader writes {@code bound} during the binding phase. */
    public static final class Entry<I extends Item> implements RegistrySupplier<I> {
        private final String path;
        private final ResourceLocation id;
        private final Function<Item.Properties, I> factory;
        private I bound;

        Entry(String path, ResourceLocation id, Function<Item.Properties, I> factory) {
            this.path = path;
            this.id = id;
            this.factory = factory;
        }

        public String path() { return path; }
        public Function<Item.Properties, I> factory() { return factory; }
        public I bound() { return bound; }

        /** Loader writes the constructed Item here during binding. */
        public void setBound(I item) { this.bound = item; }

        @Override public I get() { return bound; }
        @Override public ResourceLocation id() { return id; }
    }

    public static ItemRegistry create(String modId) {
        ItemRegistry registry = new ItemRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private ItemRegistry(String modId) {
        this.modId = modId;
    }

    public <I extends Item> RegistrySupplier<I> register(String path, Function<Item.Properties, I> factory) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
        Entry<I> entry = new Entry<>(path, id, factory);
        entries.add(entry);
        return entry;
    }

    public String modId() {
        return modId;
    }

    /** Read-only view of the queued registrations. Loader iterates and binds each. */
    public List<Entry<? extends Item>> entries() {
        return Collections.unmodifiableList(entries);
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry<?> entry : entries) {
            out.add(entry.id);
        }
        return out;
    }
}

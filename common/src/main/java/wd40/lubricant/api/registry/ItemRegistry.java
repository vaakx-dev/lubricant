package wd40.lubricant.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import wd40.lubricant.core.Datagen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;

/**
 * Registers {@link Item}s under one mod's namespace. Plain data - no SPI lookup
 * during {@code create} or {@code register}. The loader entry point reads
 * {@link #ALL} and commits each {@link Entry} into the loader's registry pipeline.
 *
 * <p>Returns the constructed {@link Item} as a typed field (no Supplier wrapper).
 * Lubricant controls {@link Item.Properties} instantiation so loader-required
 * setup (e.g. NeoForge's future {@code setId(...)} call) can be applied
 * transparently before the user factory runs.</p>
 *
 * <pre>{@code
 * public static final ItemRegistry ITEMS = ItemRegistry.create("mymod");
 * public static final Item COG = ITEMS.register("cog", props -> new Item(props.stacksTo(64)));
 * }</pre>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Items">Items wiki</a></p>
 */
public final class ItemRegistry {

    /** Every registry instance ever created in this JVM, in creation order. */
    public static final List<ItemRegistry> ALL = new CopyOnWriteArrayList<>();

    private final String modId;
    private final List<Entry<?>> entries = new ArrayList<>();

    /** One queued registration. Loader binds the result into vanilla and stores it back via setBound(). */
    public static final class Entry<I extends Item> {
        private final String path;
        private final Function<Item.Properties, I> factory;
        private I bound;

        Entry(String path, Function<Item.Properties, I> factory) {
            this.path = path;
            this.factory = factory;
        }

        public String path() { return path; }
        public Function<Item.Properties, I> factory() { return factory; }

        /** Loader writes the constructed Item here so consumer-side Item fields see the live instance. */
        @SuppressWarnings("unchecked")
        public void setBound(Item item) { this.bound = (I) item; }
        public I bound() { return bound; }
    }

    public static ItemRegistry create(String modId) {
        ItemRegistry registry = new ItemRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private ItemRegistry(String modId) {
        this.modId = modId;
    }

    /**
     * Construct and register an item. The {@code factory} receives a fresh
     * {@link Item.Properties}; configure it and return the Item. Lubricant
     * stores the result for binding into the loader's item registry and
     * returns the same instance for assignment to a {@code public static final Item} field.
     */
    public <I extends Item> I register(String path, Function<Item.Properties, I> factory) {
        Entry<I> entry = new Entry<>(path, factory);
        entries.add(entry);
        // Datagen JVM has no MC Bootstrap and would crash on Item construction. Datagen
        // providers only need ids/paths anyway, so we skip the factory and return null.
        if (Datagen.IS_DATAGEN) return null;
        Item.Properties props = new Item.Properties();
        I item = factory.apply(props);
        entry.setBound(item);
        return item;
    }

    public String modId() {
        return modId;
    }

    /** Read-only view of the queued registrations. Loader iterates and binds each. */
    public List<Entry<?>> entries() {
        return Collections.unmodifiableList(entries);
    }

    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry<?> entry : entries) {
            out.add(ResourceLocation.fromNamespaceAndPath(modId, entry.path));
        }
        return out;
    }
}

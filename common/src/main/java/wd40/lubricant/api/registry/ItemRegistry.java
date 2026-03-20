package wd40.lubricant.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Registers {@link Item}s under one mod's namespace. Each {@code register} call
 * stores a {@link Entry}; the loader entry point ({@code LubricantFabric},
 * {@code LubricantNeoForge}) reads {@link #ALL} at the right phase and commits
 * the entries to that loader's registry pipeline.
 *
 * <p>This class is plain data - no SPI lookup happens during {@code create} or
 * {@code register}. That's why {@code Items.<clinit>} works on a stripped JVM
 * (datagen) without any loader on the classpath.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Items">Items wiki</a></p>
 */
public final class ItemRegistry {

    /** Every registry instance ever created in this JVM, in creation order. */
    public static final List<ItemRegistry> ALL = new CopyOnWriteArrayList<>();

    public final String modId;
    public final List<Entry> entries = new ArrayList<>();

    /** One queued registration. The loader sets {@code ref} once the Item exists. */
    public record Entry(String path, Function<Item.Properties, Item> factory, AtomicReference<Item> ref) {}

    public static ItemRegistry create(String modId) {
        ItemRegistry registry = new ItemRegistry(modId);
        ALL.add(registry);
        return registry;
    }

    private ItemRegistry(String modId) {
        this.modId = modId;
    }

    /** Calling {@code .get()} on the returned supplier before the loader binds throws. */
    public Supplier<Item> register(String path, Function<Item.Properties, Item> factory) {
        Entry entry = new Entry(path, factory, new AtomicReference<>());
        entries.add(entry);
        return () -> {
            Item item = entry.ref.get();
            if (item == null) {
                throw new IllegalStateException(
                        "Item " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return item;
        };
    }

    /** Every id passed to {@link #register}, in registration order. Read at any phase. */
    public List<ResourceLocation> ids() {
        List<ResourceLocation> out = new ArrayList<>(entries.size());
        for (Entry entry : entries) {
            out.add(ResourceLocation.fromNamespaceAndPath(modId, entry.path));
        }
        return out;
    }
}

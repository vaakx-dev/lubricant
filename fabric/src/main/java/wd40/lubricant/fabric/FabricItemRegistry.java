package wd40.lubricant.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import wd40.lubricant.api.registry.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Supplier;

// Fabric implementation. Constructs the item via the user's factory, then registers
// it to BuiltInRegistries.ITEM. Relies on fabric-api delaying the registry freeze
// past mod load - without fabric-api on the classpath this would crash on freeze.
final class FabricItemRegistry implements ItemRegistry {

    private final String modId;
    private final List<Runnable> queued = new ArrayList<>();

    FabricItemRegistry(String modId) {
        this.modId = modId;
    }

    @Override
    public Supplier<Item> register(String path, Function<Item.Properties, Item> factory) {
        AtomicReference<Item> ref = new AtomicReference<>();
        queued.add(() -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
            Item item = factory.apply(new Item.Properties());
            Registry.register(BuiltInRegistries.ITEM, id, item);
            ref.set(item);
        });
        return () -> {
            Item v = ref.get();
            if (v == null) {
                throw new IllegalStateException(
                        "Item " + modId + ":" + path + " was accessed before lubricant bootstrap completed");
            }
            return v;
        };
    }

    String modId() {
        return modId;
    }

    int queuedSize() {
        return queued.size();
    }

    void bind() {
        for (Runnable r : queued) r.run();
        queued.clear();
    }
}

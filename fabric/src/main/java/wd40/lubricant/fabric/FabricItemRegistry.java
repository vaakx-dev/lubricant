package wd40.lubricant.fabric;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import wd40.lubricant.api.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

// Fabric implementation. Holds register() calls in a queue until bind() runs them
// against vanilla's BuiltInRegistries.ITEM. Bind is called by LubricantFabric's
// onInitialize after Bootstrap.loadAllInit completes.
final class FabricItemRegistry implements ItemRegistry {

    private final String modId;
    private final List<Runnable> queued = new ArrayList<>();

    FabricItemRegistry(String modId) {
        this.modId = modId;
    }

    @Override
    public Supplier<Item> register(String path, Supplier<Item> factory) {
        AtomicReference<Item> ref = new AtomicReference<>();
        queued.add(() -> {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
            Item item = Registry.register(BuiltInRegistries.ITEM, id, factory.get());
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

    void bind() {
        for (Runnable r : queued) r.run();
        queued.clear();
    }
}

package wd40.lubricant.neoforge;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.registry.ItemRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

// NeoForge implementation. NeoForge's DeferredRegister.Items.registerItem takes a
// Function<Item.Properties, ? extends Item> directly - it handles setId internally.
final class NeoForgeItemRegistry implements ItemRegistry {

    final String modId;
    final DeferredRegister.Items deferred;
    private final List<ResourceLocation> ids = new ArrayList<>();

    NeoForgeItemRegistry(String modId) {
        this.modId = modId;
        this.deferred = DeferredRegister.createItems(modId);
    }

    @Override
    public Supplier<Item> register(String path, Function<Item.Properties, Item> factory) {
        ids.add(ResourceLocation.fromNamespaceAndPath(modId, path));
        DeferredItem<Item> ref = deferred.registerItem(path, factory);
        return ref;
    }

    @Override
    public List<ResourceLocation> ids() {
        return List.copyOf(ids);
    }

    void attach(IEventBus modBus) {
        deferred.register(modBus);
    }
}

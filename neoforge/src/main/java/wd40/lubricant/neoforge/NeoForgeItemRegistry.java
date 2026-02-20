package wd40.lubricant.neoforge;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.ItemRegistry;

import java.util.function.Function;
import java.util.function.Supplier;

// NeoForge implementation. NeoForge's DeferredRegister.Items.registerItem takes a
// Function<Item.Properties, ? extends Item> directly - it handles setId internally.
final class NeoForgeItemRegistry implements ItemRegistry {

    final String modId;
    final DeferredRegister.Items deferred;

    NeoForgeItemRegistry(String modId) {
        this.modId = modId;
        this.deferred = DeferredRegister.createItems(modId);
    }

    @Override
    public Supplier<Item> register(String path, Function<Item.Properties, Item> factory) {
        DeferredItem<Item> ref = deferred.registerItem(path, factory);
        return ref;
    }

    void attach(IEventBus modBus) {
        deferred.register(modBus);
    }
}

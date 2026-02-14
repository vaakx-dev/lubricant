package wd40.lubricant.neoforge;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.ItemRegistry;

import java.util.function.Supplier;

// NeoForge implementation. Wraps NeoForge's DeferredRegister.Items. DeferredItem<Item>
// implements Supplier<Item>, so the public API contract holds.
final class NeoForgeItemRegistry implements ItemRegistry {

    final String modId;
    final DeferredRegister.Items deferred;

    NeoForgeItemRegistry(String modId) {
        this.modId = modId;
        this.deferred = DeferredRegister.createItems(modId);
    }

    @Override
    public Supplier<Item> register(String path, Supplier<Item> factory) {
        DeferredItem<Item> ref = deferred.register(path, factory);
        return ref;
    }

    void attach(IEventBus modBus) {
        deferred.register(modBus);
    }
}

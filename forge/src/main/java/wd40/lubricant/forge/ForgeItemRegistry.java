package wd40.lubricant.forge;

import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wd40.lubricant.api.ItemRegistry;

import java.util.function.Supplier;

// Forge implementation. Wraps Forge's DeferredRegister<Item>. The RegistryObject
// returned by deferred.register implements Supplier<Item> directly, so the public
// API contract holds.
final class ForgeItemRegistry implements ItemRegistry {

    final String modId;
    final DeferredRegister<Item> deferred;

    ForgeItemRegistry(String modId) {
        this.modId = modId;
        this.deferred = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    @Override
    public Supplier<Item> register(String path, Supplier<Item> factory) {
        RegistryObject<Item> ro = deferred.register(path, factory);
        return ro;
    }

    void attach(IEventBus modBus) {
        deferred.register(modBus);
    }
}

package wd40.lubricant.forge;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import wd40.lubricant.api.ItemRegistry;

import java.util.function.Function;
import java.util.function.Supplier;

// Forge implementation. Wraps Forge's DeferredRegister<Item>. Forge's DR takes a
// Supplier<Item> so we adapt: build Item.Properties with the ResourceKey set first,
// then invoke the user's factory.
final class ForgeItemRegistry implements ItemRegistry {

    final String modId;
    final DeferredRegister<Item> deferred;

    ForgeItemRegistry(String modId) {
        this.modId = modId;
        this.deferred = DeferredRegister.create(ForgeRegistries.ITEMS, modId);
    }

    @Override
    public Supplier<Item> register(String path, Function<Item.Properties, Item> factory) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, path);
        ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
        RegistryObject<Item> ro = deferred.register(path, () -> {
            Item.Properties props = new Item.Properties().setId(key);
            return factory.apply(props);
        });
        return ro;
    }

    void attach(IEventBus modBus) {
        deferred.register(modBus);
    }
}

package wd40.lubricant.neoforge;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.lubricant.core.Bootstrap;
import wd40.lubricant.core.Services;
import wd40.lubricant.neoforge.net.Net;

@Mod("lubricant")
public final class Entry {

    public Entry(IEventBus lubricantBus) {
        Bootstrap.loadAllInit();

        Services.net();
        lubricantBus.addListener(Net.INSTANCE::onRegister);

        for (BlockRegistry registry : BlockRegistry.ALL) {
            attachBlocks(registry, busFor(registry.modId(), lubricantBus));
        }
        for (ItemRegistry registry : ItemRegistry.ALL) {
            attachItems(registry, busFor(registry.modId(), lubricantBus));
        }
    }

    private static void attachBlocks(BlockRegistry registry, IEventBus bus) {
        DeferredRegister.Blocks blocks = DeferredRegister.createBlocks(registry.modId());
        DeferredRegister.Items blockItems = DeferredRegister.createItems(registry.modId());
        for (BlockRegistry.Entry entry : registry.entries()) {
            // The lambda runs once during the registry event; capture the result into entry.ref
            // so user code reading the Supplier<Block> sees the live Block.
            var deferred = blocks.registerBlock(entry.path(), props -> {
                Block block = entry.factory().apply(props);
                entry.ref().set(block);
                return block;
            });
            if (!registry.isNoItem(entry.path())) {
                blockItems.registerSimpleBlockItem(deferred);
            }
        }
        blocks.register(bus);
        blockItems.register(bus);
    }

    private static void attachItems(ItemRegistry registry, IEventBus bus) {
        DeferredRegister.Items items = DeferredRegister.createItems(registry.modId());
        for (ItemRegistry.Entry entry : registry.entries()) {
            items.registerItem(entry.path(), props -> {
                Item item = entry.factory().apply(props);
                entry.ref().set(item);
                return item;
            });
        }
        items.register(bus);
    }

    private static IEventBus busFor(String modId, IEventBus lubricantBus) {
        if ("lubricant".equals(modId)) return lubricantBus;
        ModContainer container = ModList.get().getModContainerById(modId)
                .orElseThrow(() -> new IllegalStateException(
                        "lubricant: no NeoForge mod container for modId=" + modId));
        IEventBus bus = container.getEventBus();
        if (bus == null) {
            throw new IllegalStateException(
                    "lubricant: mod " + modId + " has no event bus yet");
        }
        return bus;
    }
}

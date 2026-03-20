package wd40.lubricant.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.lubricant.internal.Bootstrap;

public final class LubricantFabric implements ModInitializer {

    private static final Logger LOG = LoggerFactory.getLogger("lubricant");

    @Override
    public void onInitialize() {
        Bootstrap.loadAllInit();

        // Bind blocks before items so item factories can reference Blocks.X.get()
        // when constructing BlockItems.
        for (BlockRegistry registry : BlockRegistry.ALL) {
            for (BlockRegistry.Entry entry : registry.entries) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(registry.modId, entry.path());
                Block block = entry.factory().apply(BlockBehaviour.Properties.of());
                Registry.register(BuiltInRegistries.BLOCK, id, block);
                entry.ref().set(block);
                if (!registry.noItemPaths.contains(entry.path())) {
                    Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
                }
            }
        }
        for (ItemRegistry registry : ItemRegistry.ALL) {
            for (ItemRegistry.Entry entry : registry.entries) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(registry.modId, entry.path());
                Item item = entry.factory().apply(new Item.Properties());
                Registry.register(BuiltInRegistries.ITEM, id, item);
                entry.ref().set(item);
            }
        }

        LOG.info("[lubricant] init complete on Fabric ({} block reg(s), {} item reg(s))",
                BlockRegistry.ALL.size(), ItemRegistry.ALL.size());
    }
}

package wd40.lubricant.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wd40.lubricant.api.registry.BlockEntityRegistry;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.CreativeTabRegistry;
import wd40.lubricant.api.registry.EntityRegistry;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.lubricant.api.registry.ParticleRegistry;
import wd40.lubricant.api.registry.SoundRegistry;
import wd40.lubricant.core.Bootstrap;
import wd40.lubricant.core.Services;

public final class Entry implements ModInitializer {

    private static final Logger LOG = LoggerFactory.getLogger("lubricant");

    @Override
    public void onInitialize() {
        Bootstrap.loadAllInit();

        // Bind blocks before items so item factories may reference Blocks.X.get().
        for (BlockRegistry registry : BlockRegistry.ALL) {
            String modId = registry.modId();
            for (BlockRegistry.Entry entry : registry.entries()) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entry.path());
                Block block = entry.factory().apply(BlockBehaviour.Properties.of());
                Registry.register(BuiltInRegistries.BLOCK, id, block);
                entry.ref().set(block);
                if (!registry.isNoItem(entry.path())) {
                    Registry.register(BuiltInRegistries.ITEM, id, new BlockItem(block, new Item.Properties()));
                }
            }
        }
        for (ItemRegistry registry : ItemRegistry.ALL) {
            String modId = registry.modId();
            for (ItemRegistry.Entry entry : registry.entries()) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entry.path());
                Item item = entry.factory().apply(new Item.Properties());
                Registry.register(BuiltInRegistries.ITEM, id, item);
                entry.ref().set(item);
            }
        }
        // Block entities require their valid blocks to already exist - depends on the block loop above.
        for (BlockEntityRegistry registry : BlockEntityRegistry.ALL) {
            String modId = registry.modId();
            for (BlockEntityRegistry.Entry<?> entry : registry.entries()) {
                bindBlockEntity(entry, modId);
            }
        }
        for (EntityRegistry registry : EntityRegistry.ALL) {
            String modId = registry.modId();
            for (EntityRegistry.Entry<?> entry : registry.entries()) {
                bindEntity(entry, modId);
            }
        }
        for (SoundRegistry registry : SoundRegistry.ALL) {
            String modId = registry.modId();
            for (SoundRegistry.Entry entry : registry.entries()) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entry.path());
                SoundEvent sound = SoundEvent.createVariableRangeEvent(id);
                Registry.register(BuiltInRegistries.SOUND_EVENT, id, sound);
                entry.ref().set(sound);
            }
        }
        for (ParticleRegistry registry : ParticleRegistry.ALL) {
            String modId = registry.modId();
            for (ParticleRegistry.Entry entry : registry.entries()) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entry.path());
                SimpleParticleType type = FabricParticleTypes.simple(entry.overrideLimiter());
                Registry.register(BuiltInRegistries.PARTICLE_TYPE, id, type);
                entry.ref().set(type);
            }
        }
        for (CreativeTabRegistry registry : CreativeTabRegistry.ALL) {
            String modId = registry.modId();
            for (CreativeTabRegistry.Entry entry : registry.entries()) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entry.path());
                CreativeModeTab.Builder builder = FabricItemGroup.builder();
                entry.configure().accept(builder);
                CreativeModeTab tab = builder.build();
                Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, id, tab);
                entry.ref().set(tab);
            }
        }

        // Fire setup() listeners now that every registry binding is done. Modders can
        // safely touch Items/Blocks/etc. .get() inside these callbacks (e.g. to call
        // FlowerPotBlock.addPlant(...) which mutates a static map after both blocks exist).
        Services.events().fireSetup();

        LOG.info("[lubricant] init complete on Fabric ({} block reg(s), {} item reg(s), {} BE reg(s), {} entity reg(s), {} sound reg(s), {} particle reg(s), {} creative tab reg(s))",
                BlockRegistry.ALL.size(), ItemRegistry.ALL.size(),
                BlockEntityRegistry.ALL.size(), EntityRegistry.ALL.size(),
                SoundRegistry.ALL.size(), ParticleRegistry.ALL.size(), CreativeTabRegistry.ALL.size());
    }

    @SuppressWarnings("DataFlowIssue")  // BlockEntityType.Builder.build accepts null DataFixerType
    private static <T extends BlockEntity> void bindBlockEntity(BlockEntityRegistry.Entry<T> entry, String modId) {
        Block[] blocks = entry.validBlocks().stream().map(java.util.function.Supplier::get).toArray(Block[]::new);
        BlockEntityType<T> type = BlockEntityType.Builder.of(entry.factory()::apply, blocks).build(null);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                ResourceLocation.fromNamespaceAndPath(modId, entry.path()), type);
        entry.ref().set(type);
    }

    private static <T extends Entity> void bindEntity(EntityRegistry.Entry<T> entry, String modId) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(modId, entry.path());
        EntityType<T> type = entry.builder().build(id.toString());
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);
        entry.ref().set(type);
    }
}

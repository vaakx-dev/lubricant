package wd40.lubricant.neoforge;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.registry.BlockEntityRegistry;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.CreativeTabRegistry;
import wd40.lubricant.api.registry.EntityRegistry;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.lubricant.api.registry.ParticleRegistry;
import wd40.lubricant.api.registry.SoundRegistry;
import wd40.lubricant.core.Bootstrap;
import wd40.lubricant.core.Services;
import wd40.lubricant.neoforge.client.Renderers;
import wd40.lubricant.neoforge.data.BlockEntities;
import wd40.lubricant.neoforge.data.Entities;
import wd40.lubricant.neoforge.data.Stacks;
import wd40.lubricant.neoforge.net.Net;

@Mod("lubricant")
public final class Entry {

    public Entry(IEventBus lubricantBus) {
        Bootstrap.loadAllInit();

        Services.net();
        Services.stacks();
        Services.blockEntities();
        Services.entities();
        lubricantBus.addListener(Net.INSTANCE::onRegister);
        lubricantBus.addListener(Stacks.INSTANCE::onRegister);
        lubricantBus.addListener(BlockEntities.INSTANCE::onRegister);
        lubricantBus.addListener(Entities.INSTANCE::onRegister);

        // Force ServiceLoader to instantiate the renderer helper (sets Renderers.INSTANCE)
        // before the client-only wiring tries to read it. Safe on dedicated server: returns null.
        if (Services.renderers() != null) {
            Renderers.attachListenerIfClient(lubricantBus);
        }

        for (BlockRegistry registry : BlockRegistry.ALL) {
            attachBlocks(registry, busFor(registry.modId(), lubricantBus));
        }
        for (ItemRegistry registry : ItemRegistry.ALL) {
            attachItems(registry, busFor(registry.modId(), lubricantBus));
        }
        for (BlockEntityRegistry registry : BlockEntityRegistry.ALL) {
            attachBlockEntities(registry, busFor(registry.modId(), lubricantBus));
        }
        for (EntityRegistry registry : EntityRegistry.ALL) {
            attachEntities(registry, busFor(registry.modId(), lubricantBus));
        }
        for (SoundRegistry registry : SoundRegistry.ALL) {
            attachSounds(registry, busFor(registry.modId(), lubricantBus));
        }
        for (ParticleRegistry registry : ParticleRegistry.ALL) {
            attachParticles(registry, busFor(registry.modId(), lubricantBus));
        }
        for (CreativeTabRegistry registry : CreativeTabRegistry.ALL) {
            attachCreativeTabs(registry, busFor(registry.modId(), lubricantBus));
        }

        // Fire setup() listeners during FMLCommonSetupEvent. enqueueWork moves the call
        // onto the main thread - any listener that touches a non-thread-safe vanilla map
        // (e.g. FlowerPotBlock.POTTED_BY_CONTENT via FlowerPotBlock.addPlant) is then safe.
        lubricantBus.addListener((FMLCommonSetupEvent event) ->
                event.enqueueWork(() -> Services.events().fireSetup()));
    }

    private static void attachBlocks(BlockRegistry registry, IEventBus bus) {
        // Items/Blocks are constructed eagerly inside the registry's register() call (during
        // consumer Init class load). Here we just register the already-built instances with
        // NeoForge's DeferredRegister, which then commits them at RegisterEvent time.
        DeferredRegister.Blocks blocks = DeferredRegister.createBlocks(registry.modId());
        DeferredRegister.Items blockItems = DeferredRegister.createItems(registry.modId());
        for (BlockRegistry.Entry<?> entry : registry.entries()) {
            Block block = entry.bound();
            var deferred = blocks.register(entry.path(), () -> block);
            if (!registry.isNoItem(entry.path())) {
                blockItems.registerSimpleBlockItem(deferred);
            }
        }
        blocks.register(bus);
        blockItems.register(bus);
    }

    private static void attachItems(ItemRegistry registry, IEventBus bus) {
        DeferredRegister.Items items = DeferredRegister.createItems(registry.modId());
        for (ItemRegistry.Entry<?> entry : registry.entries()) {
            Item item = entry.bound();
            items.register(entry.path(), () -> item);
        }
        items.register(bus);
    }

    private static void attachBlockEntities(BlockEntityRegistry registry, IEventBus bus) {
        DeferredRegister<BlockEntityType<?>> types = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, registry.modId());
        for (BlockEntityRegistry.Entry<?> entry : registry.entries()) {
            registerOneBlockEntity(types, entry);
        }
        types.register(bus);
    }

    @SuppressWarnings("DataFlowIssue")  // BlockEntityType.Builder.build accepts null DataFixerType
    private static <T extends BlockEntity> void registerOneBlockEntity(
            DeferredRegister<BlockEntityType<?>> types, BlockEntityRegistry.Entry<T> entry) {
        types.register(entry.path(), () -> {
            Block[] blocks = entry.validBlocks().toArray(new Block[0]);
            BlockEntityType<T> type = BlockEntityType.Builder.of(entry.factory()::apply, blocks).build(null);
            entry.ref().set(type);
            return type;
        });
    }

    private static void attachEntities(EntityRegistry registry, IEventBus bus) {
        DeferredRegister<EntityType<?>> types = DeferredRegister.create(Registries.ENTITY_TYPE, registry.modId());
        for (EntityRegistry.Entry<?> entry : registry.entries()) {
            registerOneEntity(types, entry, registry.modId());
        }
        types.register(bus);
    }

    private static <T extends Entity> void registerOneEntity(
            DeferredRegister<EntityType<?>> types, EntityRegistry.Entry<T> entry, String modId) {
        types.register(entry.path(), () -> {
            EntityType<T> type = entry.builder().build(modId + ":" + entry.path());
            entry.ref().set(type);
            return type;
        });
    }

    private static void attachSounds(SoundRegistry registry, IEventBus bus) {
        DeferredRegister<SoundEvent> sounds = DeferredRegister.create(Registries.SOUND_EVENT, registry.modId());
        for (SoundRegistry.Entry entry : registry.entries()) {
            sounds.register(entry.path(), () -> {
                SoundEvent sound = SoundEvent.createVariableRangeEvent(
                        ResourceLocation.fromNamespaceAndPath(registry.modId(), entry.path()));
                entry.ref().set(sound);
                return sound;
            });
        }
        sounds.register(bus);
    }

    private static void attachParticles(ParticleRegistry registry, IEventBus bus) {
        DeferredRegister<ParticleType<?>> particles = DeferredRegister.create(Registries.PARTICLE_TYPE, registry.modId());
        for (ParticleRegistry.Entry entry : registry.entries()) {
            particles.register(entry.path(), () -> {
                SimpleParticleType type = new SimpleParticleType(entry.overrideLimiter()) {};
                entry.ref().set(type);
                return type;
            });
        }
        particles.register(bus);
    }

    private static void attachCreativeTabs(CreativeTabRegistry registry, IEventBus bus) {
        DeferredRegister<CreativeModeTab> tabs = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, registry.modId());
        for (CreativeTabRegistry.Entry entry : registry.entries()) {
            tabs.register(entry.path(), () -> {
                CreativeModeTab.Builder builder = CreativeModeTab.builder();
                entry.configure().accept(builder);
                CreativeModeTab tab = builder.build();
                entry.ref().set(tab);
                return tab;
            });
        }
        tabs.register(bus);
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

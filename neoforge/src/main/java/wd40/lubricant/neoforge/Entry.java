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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.registries.DeferredRegister;
import wd40.lubricant.api.block.entity.BlockEntityRegistry;
import wd40.lubricant.api.block.BlockRegistry;
import wd40.lubricant.api.item.CreativeTabRegistry;
import wd40.lubricant.api.entity.EntityRegistry;
import wd40.lubricant.api.item.ItemRegistry;
import wd40.lubricant.api.particle.ParticleRegistry;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import wd40.lubricant.api.inventory.MenuRegistry;
import wd40.lubricant.api.sounds.SoundRegistry;
import wd40.lubricant.internal.Bootstrap;
import wd40.lubricant.internal.Services;
import wd40.lubricant.neoforge.client.Renderers;
import wd40.lubricant.neoforge.data.BlockEntities;
import wd40.lubricant.neoforge.data.Entities;
import wd40.lubricant.neoforge.data.Stacks;
import wd40.lubricant.neoforge.network.Network;

@Mod("lubricant")
public final class Entry {

    public Entry(IEventBus lubricantBus) {
        Bootstrap.loadCommon();
        Bootstrap.loadServer();
        if (FMLEnvironment.dist.isClient()) {
            Bootstrap.loadClient();
        }

        Services.network();
        Services.stacks();
        Services.blockEntities();
        Services.entities();
        lubricantBus.addListener(Network.INSTANCE::onRegister);
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
        for (MenuRegistry registry : MenuRegistry.ALL) {
            attachMenus(registry, busFor(registry.modId(), lubricantBus));
        }

        // Fire ServerEvent.SETUP listeners during FMLCommonSetupEvent. enqueueWork moves
        // the call onto the main thread - any listener that touches a non-thread-safe vanilla
        // map (e.g. FlowerPotBlock.POTTED_BY_CONTENT via FlowerPotBlock.addPlant) is safe.
        lubricantBus.addListener((FMLCommonSetupEvent event) ->
                event.enqueueWork(() -> Services.events().fireServerSetup()));

        // Fire ClientEvent.SETUP during FMLClientSetupEvent on client only.
        if (FMLEnvironment.dist.isClient()) {
            lubricantBus.addListener((FMLClientSetupEvent event) ->
                    event.enqueueWork(() -> Services.events().fireClientSetup()));
        }
    }

    private static void attachBlocks(BlockRegistry registry, IEventBus bus) {
        // The user factory runs INSIDE the DeferredRegister supplier, which fires inside
        // RegisterEvent for BLOCK - the only window NeoForge unfreezes the registry, letting
        // Block.<init>'s createIntrusiveHolder call succeed.
        DeferredRegister.Blocks blocks = DeferredRegister.createBlocks(registry.modId());
        DeferredRegister.Items blockItems = DeferredRegister.createItems(registry.modId());
        for (BlockRegistry.Entry<?> entry : registry.entries()) {
            var deferred = registerOneBlock(blocks, entry);
            if (!registry.isNoItem(entry.path())) {
                blockItems.registerSimpleBlockItem(deferred);
            }
        }
        blocks.register(bus);
        blockItems.register(bus);
    }

    private static <B extends Block> net.neoforged.neoforge.registries.DeferredBlock<B> registerOneBlock(
            DeferredRegister.Blocks blocks, BlockRegistry.Entry<B> entry) {
        return blocks.register(entry.path(), () -> {
            B block = entry.factory().apply(BlockBehaviour.Properties.of());
            entry.setBound(block);
            return block;
        });
    }

    private static void attachItems(ItemRegistry registry, IEventBus bus) {
        DeferredRegister.Items items = DeferredRegister.createItems(registry.modId());
        for (ItemRegistry.Entry<?> entry : registry.entries()) {
            registerOneItem(items, entry);
        }
        items.register(bus);
    }

    private static <I extends Item> void registerOneItem(
            DeferredRegister.Items items, ItemRegistry.Entry<I> entry) {
        items.register(entry.path(), () -> {
            I item = entry.factory().apply(new Item.Properties());
            entry.setBound(item);
            return item;
        });
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
            Block[] blocks = entry.validBlocks().stream().map(java.util.function.Supplier::get).toArray(Block[]::new);
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

    private static void attachMenus(MenuRegistry registry, IEventBus bus) {
        DeferredRegister<MenuType<?>> menus = DeferredRegister.create(Registries.MENU, registry.modId());
        for (MenuRegistry.Entry<?> entry : registry.entries()) {
            registerOneMenu(menus, entry);
        }
        menus.register(bus);
    }

    private static <T extends AbstractContainerMenu> void registerOneMenu(
            DeferredRegister<MenuType<?>> menus, MenuRegistry.Entry<T> entry) {
        menus.register(entry.path(), () -> {
            MenuType<T> type = new MenuType<>(
                    (containerId, inv) -> entry.factory().create(containerId, inv),
                    FeatureFlags.VANILLA_SET);
            entry.setBound(type);
            return type;
        });
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

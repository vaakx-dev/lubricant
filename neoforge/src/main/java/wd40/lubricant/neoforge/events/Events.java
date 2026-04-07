package wd40.lubricant.neoforge.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import wd40.lubricant.api.events.EntityInteractListener;
import wd40.lubricant.api.events.Event;
import wd40.lubricant.api.events.ItemUseListener;
import wd40.lubricant.core.events.BridgedEvent;
import wd40.lubricant.core.events.EventHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// NeoForge impl of EventHelper. All seven of these events live on the global
// NeoForge.EVENT_BUS (the "game bus"), not on per-mod buses, so we don't need any
// modId/bus lookup like the registry path does.
public final class Events implements EventHelper {

    private final Event<Consumer<MinecraftServer>> serverTick = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((ServerTickEvent.Post e) -> l.accept(e.getServer())));

    private final Event<Consumer<MinecraftServer>> serverStart = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((ServerStartingEvent e) -> l.accept(e.getServer())));

    private final Event<Consumer<MinecraftServer>> serverStop = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((ServerStoppingEvent e) -> l.accept(e.getServer())));

    private final Event<Consumer<ServerPlayer>> playerJoin = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedInEvent e) -> l.accept((ServerPlayer) e.getEntity())));

    private final Event<Consumer<ServerPlayer>> playerLeave = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> l.accept((ServerPlayer) e.getEntity())));

    private final Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((RegisterCommandsEvent e) -> l.accept(e.getDispatcher())));

    private final Event<ItemUseListener> itemUse = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.RightClickItem e) -> {
                InteractionResult r = l.onUse(e.getEntity(), e.getLevel(), e.getHand());
                if (r != InteractionResult.PASS) {
                    e.setCanceled(true);
                    e.setCancellationResult(r);
                }
            }));

    private final Event<EntityInteractListener> entityInteract = new BridgedEvent<>(
            l -> NeoForge.EVENT_BUS.addListener((PlayerInteractEvent.EntityInteract e) -> {
                InteractionResult r = l.onInteract(e.getEntity(), e.getTarget(), e.getHand());
                if (r != InteractionResult.PASS) {
                    e.setCanceled(true);
                    e.setCancellationResult(r);
                }
            }));

    private final List<Runnable> setupQueue = new ArrayList<>();
    private final Event<Runnable> setup = new BridgedEvent<>(setupQueue::add);

    @Override public Event<Consumer<MinecraftServer>> serverTick()  { return serverTick; }
    @Override public Event<Consumer<MinecraftServer>> serverStart() { return serverStart; }
    @Override public Event<Consumer<MinecraftServer>> serverStop()  { return serverStop; }
    @Override public Event<Consumer<ServerPlayer>>    playerJoin()  { return playerJoin; }
    @Override public Event<Consumer<ServerPlayer>>    playerLeave() { return playerLeave; }
    @Override public Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands() { return commands; }
    @Override public Event<ItemUseListener>           itemUse()     { return itemUse; }
    @Override public Event<EntityInteractListener>    entityInteract() { return entityInteract; }
    @Override public Event<Runnable>                  setup()       { return setup; }

    @Override
    public void fireSetup() {
        for (Runnable task : setupQueue) task.run();
        setupQueue.clear();
    }
}

package wd40.lubricant.fabric.events;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import wd40.lubricant.api.events.EntityInteractListener;
import wd40.lubricant.api.events.Event;
import wd40.lubricant.api.events.ItemUseListener;
import wd40.lubricant.core.events.BridgedEvent;
import wd40.lubricant.core.events.EventHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

// Fabric impl of EventHelper. Each Event<L> wraps a one-shot subscribe -> Fabric API
// register call. Constructed once by ServiceLoader on first Services.events() call.
public final class Events implements EventHelper {

    private final Event<Consumer<MinecraftServer>> serverTick = new BridgedEvent<>(
            l -> ServerTickEvents.END_SERVER_TICK.register(l::accept));

    private final Event<Consumer<MinecraftServer>> serverStart = new BridgedEvent<>(
            l -> ServerLifecycleEvents.SERVER_STARTING.register(l::accept));

    private final Event<Consumer<MinecraftServer>> serverStop = new BridgedEvent<>(
            l -> ServerLifecycleEvents.SERVER_STOPPING.register(l::accept));

    private final Event<Consumer<ServerPlayer>> playerJoin = new BridgedEvent<>(
            l -> ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> l.accept(handler.player)));

    private final Event<Consumer<ServerPlayer>> playerLeave = new BridgedEvent<>(
            l -> ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> l.accept(handler.player)));

    private final Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands = new BridgedEvent<>(
            l -> CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> l.accept(dispatcher)));

    private final Event<ItemUseListener> itemUse = new BridgedEvent<>(
            l -> UseItemCallback.EVENT.register((player, level, hand) -> {
                InteractionResult result = l.onUse(player, level, hand);
                return new InteractionResultHolder<>(result, player.getItemInHand(hand));
            }));

    private final Event<EntityInteractListener> entityInteract = new BridgedEvent<>(
            l -> UseEntityCallback.EVENT.register((player, level, hand, target, hit) ->
                    l.onInteract(player, target, hand)));

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

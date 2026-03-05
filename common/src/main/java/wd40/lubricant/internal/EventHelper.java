package wd40.lubricant.internal;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.api.events.Event;
import wd40.lubricant.api.events.ItemUseListener;

import java.util.function.Consumer;

// Loader-specific service interface backing the public Events API. One impl per loader
// (FabricEventHelper, NeoForgeEventHelper), discovered via JDK ServiceLoader from
// META-INF/services/wd40.lubricant.internal.EventHelper.
public interface EventHelper {
    Event<Consumer<MinecraftServer>> serverTick();
    Event<Consumer<MinecraftServer>> serverStart();
    Event<Consumer<MinecraftServer>> serverStop();
    Event<Consumer<ServerPlayer>> playerJoin();
    Event<Consumer<ServerPlayer>> playerLeave();
    Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands();
    Event<ItemUseListener> itemUse();
}

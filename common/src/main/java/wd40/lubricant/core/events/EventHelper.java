package wd40.lubricant.core.events;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.api.server.events.EntityInteractListener;
import wd40.lubricant.api.common.events.Event;
import wd40.lubricant.api.server.events.ItemUseListener;

import java.util.function.Consumer;

/**
 * Loader-specific source of {@link Event} instances backing the public
 * {@code Events} API. One implementation per loader, discovered via JDK
 * {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.events.Events}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.events.Events}</li>
 * </ul>
 *
 * <p>Each impl constructs {@link BridgedEvent}s that wire subscribers into the
 * loader's underlying event system - Fabric API callback registries on Fabric,
 * {@code NeoForge.EVENT_BUS} listeners on NeoForge.</p>
 */
public interface EventHelper {
    Event<Consumer<MinecraftServer>> serverTick();
    Event<Consumer<MinecraftServer>> serverStart();
    Event<Consumer<MinecraftServer>> serverStop();
    Event<Consumer<ServerPlayer>> playerJoin();
    Event<Consumer<ServerPlayer>> playerLeave();
    Event<Consumer<CommandDispatcher<CommandSourceStack>>> commands();
    Event<ItemUseListener> itemUse();
    Event<EntityInteractListener> entityInteract();

    /**
     * Fires once after lubricant binds every registry on the server side.
     * Listeners run on the main server thread.
     */
    Event<Runnable> serverSetup();

    /**
     * Drain the server-setup queue. Fabric: tail of {@code onInitialize}.
     * NeoForge: inside {@code FMLCommonSetupEvent.enqueueWork}. Idempotent.
     */
    void fireServerSetup();

    /**
     * Fires once after lubricant binds every registry on the client side.
     * Never fires on dedicated server.
     */
    Event<Runnable> clientSetup();

    /**
     * Drain the client-setup queue. Fabric: tail of
     * {@code ClientEntry#onInitializeClient}. NeoForge: inside
     * {@code FMLClientSetupEvent.enqueueWork}. Idempotent.
     */
    void fireClientSetup();
}

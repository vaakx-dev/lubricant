package wd40.lubricant.neoforge.net;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import wd40.lubricant.api.net.C2SPayload;
import wd40.lubricant.api.net.handlers.ClientContext;
import wd40.lubricant.api.net.handlers.ClientHandler;
import wd40.lubricant.api.net.S2CPayload;
import wd40.lubricant.api.net.handlers.ServerContext;
import wd40.lubricant.api.net.handlers.ServerHandler;
import wd40.lubricant.core.net.NetHelper;

import java.util.ArrayList;
import java.util.List;

// NeoForge impl of NetHelper. NeoForge requires payload registration through the
// mod-bus RegisterPayloadHandlersEvent, which we can't call directly from
// Net.toClient at API-call time. So we queue every registration into `pending` and
// drain the queue inside the event handler.
//
// Entry wires the listener: lubricantBus.addListener(INSTANCE::onRegister) after
// Bootstrap.loadAllInit() has filled the queue.
public final class Net implements NetHelper {

    public static volatile Net INSTANCE;

    private final List<Pending<?>> pending = new ArrayList<>();

    public Net() {
        INSTANCE = this;
    }

    @Override
    public <T extends CustomPacketPayload> S2CPayload<T> toClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ClientHandler<T> handler) {
        pending.add(new ClientboundPending<>(type, codec, handler));
        return new S2C<>();
    }

    @Override
    public <T extends CustomPacketPayload> C2SPayload<T> toServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ServerHandler<T> handler) {
        pending.add(new ServerboundPending<>(type, codec, handler));
        return new C2S<>();
    }

    public void onRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("1");
        for (Pending<?> p : pending) {
            p.register(registrar);
        }
        pending.clear();
    }

    private sealed interface Pending<T extends CustomPacketPayload> permits ClientboundPending, ServerboundPending {
        void register(PayloadRegistrar registrar);
    }

    private record ClientboundPending<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ClientHandler<T> handler
    ) implements Pending<T> {
        @Override
        public void register(PayloadRegistrar registrar) {
            registrar.playToClient(type, codec, (payload, ctx) ->
                    ctx.enqueueWork(() -> handler.handle(payload, clientCtx(ctx))));
        }
    }

    private record ServerboundPending<T extends CustomPacketPayload>(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ServerHandler<T> handler
    ) implements Pending<T> {
        @Override
        public void register(PayloadRegistrar registrar) {
            registrar.playToServer(type, codec, (payload, ctx) ->
                    ctx.enqueueWork(() -> handler.handle(payload, serverCtx(ctx))));
        }
    }

    private static ClientContext clientCtx(IPayloadContext ctx) {
        Minecraft client = Minecraft.getInstance();
        LocalPlayer player = (LocalPlayer) ctx.player();
        return new ClientContextImpl(client, player);
    }

    private static ServerContext serverCtx(IPayloadContext ctx) {
        ServerPlayer player = (ServerPlayer) ctx.player();
        return new ServerContextImpl(player.getServer(), player);
    }

    private record ClientContextImpl(Minecraft client, LocalPlayer player) implements ClientContext {}
    private record ServerContextImpl(MinecraftServer server, ServerPlayer player) implements ServerContext {}
}

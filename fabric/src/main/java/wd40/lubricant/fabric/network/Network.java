package wd40.lubricant.fabric.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import wd40.lubricant.api.network.payload.C2SPayload;
import wd40.lubricant.api.network.handlers.client.ClientHandler;
import wd40.lubricant.api.network.payload.S2CPayload;
import wd40.lubricant.api.network.handlers.server.ServerContext;
import wd40.lubricant.api.network.handlers.server.ServerHandler;
import wd40.lubricant.internal.network.NetworkHelper;
import wd40.lubricant.fabric.network.ClientReceivers;

// Fabric impl of NetworkHelper. Discovered via JDK ServiceLoader.
//
// Fabric's PayloadTypeRegistry is global+static, so registration happens
// immediately when the consumer mod calls Net.toClient / Net.toServer (via the
// Bootstrap.loadAllInit() pass inside Entry.onInitialize - which is inside a
// ModInitializer, the correct phase).
//
// Client-only API calls (ClientPlayNetworking) are routed through ClientReceivers,
// which is @Environment(CLIENT) and never loaded on dedicated servers.
public final class Network implements NetworkHelper {

    private static final boolean IS_CLIENT =
            FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;

    @Override
    public <T extends CustomPacketPayload> S2CPayload<T> toClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ClientHandler<T> handler) {
        PayloadTypeRegistry.playS2C().register(type, codec);
        if (IS_CLIENT) {
            ClientReceivers.register(type, handler);
        }
        return new S2C<>();
    }

    @Override
    public <T extends CustomPacketPayload> C2SPayload<T> toServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ServerHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> {
            MinecraftServer server = ctx.server();
            ServerPlayer player = ctx.player();
            server.execute(() -> handler.handle(payload, new ServerContextImpl(server, player)));
        });
        return new C2S<>();
    }

    private record ServerContextImpl(MinecraftServer server, ServerPlayer player) implements ServerContext {}
}

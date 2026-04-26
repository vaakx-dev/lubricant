package wd40.lubricant.fabric.network;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.network.handlers.client.ClientContext;
import wd40.lubricant.api.network.handlers.client.ClientHandler;

// All Fabric client-only networking calls. Annotated @Environment(CLIENT) so
// fabric-loader strips this class on dedicated servers - prevents NoClassDefFoundError
// when the JVM loads Net (which references this class).
@Environment(EnvType.CLIENT)
public final class ClientReceivers {

    public static <T extends CustomPacketPayload> void register(
            CustomPacketPayload.Type<T> type,
            ClientHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> {
            ctx.client().execute(() -> handler.handle(payload, new ContextImpl(ctx.client(), ctx.player())));
        });
    }

    public static void send(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    private record ContextImpl(Minecraft client, LocalPlayer player) implements ClientContext {}

    private ClientReceivers() {}
}

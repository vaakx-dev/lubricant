package wd40.lubricant.fabric;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.net.handlers.ClientContext;
import wd40.lubricant.api.net.handlers.ClientHandler;

// All Fabric client-only networking calls. Annotated @Environment(CLIENT) so
// fabric-loader strips this class on dedicated servers - prevents NoClassDefFoundError
// when the JVM loads FabricNetHelper (which references this class).
@Environment(EnvType.CLIENT)
final class FabricNetClientReceivers {

    static <T extends CustomPacketPayload> void register(
            CustomPacketPayload.Type<T> type,
            ClientHandler<T> handler) {
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, ctx) -> {
            ctx.client().execute(() -> handler.handle(payload, new ContextImpl(ctx.client(), ctx.player())));
        });
    }

    static void send(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    private record ContextImpl(Minecraft client, LocalPlayer player) implements ClientContext {}

    private FabricNetClientReceivers() {}
}

package wd40.lubricant.api.net.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Handler for a payload arriving on the client (sent from server). Receives the
// decoded payload + a ClientContext with Minecraft + the local player.
@FunctionalInterface
public interface ClientHandler<T extends CustomPacketPayload> {
    void handle(T payload, ClientContext ctx);
}

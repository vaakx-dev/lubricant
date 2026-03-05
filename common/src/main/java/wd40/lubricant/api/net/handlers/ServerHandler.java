package wd40.lubricant.api.net.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Handler for a payload arriving on the server (sent from a client). Receives the
// decoded payload + a ServerContext with the MinecraftServer + the sending player.
@FunctionalInterface
public interface ServerHandler<T extends CustomPacketPayload> {
    void handle(T payload, ServerContext ctx);
}

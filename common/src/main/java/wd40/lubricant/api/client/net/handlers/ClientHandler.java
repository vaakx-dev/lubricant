package wd40.lubricant.api.client.net.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Server-to-client payload handler. Runs on the main client thread. */
@FunctionalInterface
public interface ClientHandler<T extends CustomPacketPayload> {
    void handle(T payload, ClientContext ctx);
}

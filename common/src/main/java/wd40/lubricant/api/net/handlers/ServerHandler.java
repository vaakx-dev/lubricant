package wd40.lubricant.api.net.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/** Client-to-server payload handler. Runs on the main server thread. */
@FunctionalInterface
public interface ServerHandler<T extends CustomPacketPayload> {
    void handle(T payload, ServerContext ctx);
}

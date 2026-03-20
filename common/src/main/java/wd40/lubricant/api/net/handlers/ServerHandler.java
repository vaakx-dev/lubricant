package wd40.lubricant.api.net.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Receives a payload sent from a client to this server. Registered via
 * {@link wd40.lubricant.api.net.Net#toServer}.
 *
 * <p>Runs on the main server thread - safe to mutate the world, send packets,
 * or modify entities directly.</p>
 */
@FunctionalInterface
public interface ServerHandler<T extends CustomPacketPayload> {
    void handle(T payload, ServerContext ctx);
}

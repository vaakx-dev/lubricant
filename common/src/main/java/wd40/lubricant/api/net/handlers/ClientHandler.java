package wd40.lubricant.api.net.handlers;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Receives a payload sent from the server to this client. Registered via
 * {@link wd40.lubricant.api.net.Net#toClient}.
 *
 * <p>Runs on the main client thread - safe to touch the GUI, world, or local
 * player directly without further synchronization.</p>
 */
@FunctionalInterface
public interface ClientHandler<T extends CustomPacketPayload> {
    void handle(T payload, ClientContext ctx);
}

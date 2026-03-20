package wd40.lubricant.api.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client-to-server payload handle returned by {@link Net#toServer}. The only
 * send shape is {@link #sendToServer} - the compiler refuses every other
 * send variant.
 *
 * <p>Calling {@code sendToServer} from server-side code throws at runtime
 * (no client connection exists). The compile-time direction split prevents
 * the symmetric mistake but can't catch this one - structure your code so
 * {@code sendToServer} is only invoked from client paths.</p>
 */
public interface C2SPayload<T extends CustomPacketPayload> {

    /** Sends from this client to its connected server. */
    void sendToServer(T payload);
}

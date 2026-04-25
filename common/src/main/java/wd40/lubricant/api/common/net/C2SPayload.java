package wd40.lubricant.api.common.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Client-to-server payload handle. Returned by {@link Net#toServer}. The compiler
 * refuses every other send variant - direction mismatches are compile errors.
 *
 * <p>Calling {@code sendToServer} from server-side code throws at runtime
 * (no client connection exists).</p>
 */
public interface C2SPayload<T extends CustomPacketPayload> {
    void sendToServer(T payload);
}

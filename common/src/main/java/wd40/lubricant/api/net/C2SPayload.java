package wd40.lubricant.api.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

// Client-to-server payload handle returned by Net.toServer. Has only sendToServer -
// the compiler refuses to call sendTo / sendToAll / sendToDimension / sendToTracking
// on this type, catching direction-mismatch bugs at compile time.
public interface C2SPayload<T extends CustomPacketPayload> {
    void sendToServer(T payload);
}

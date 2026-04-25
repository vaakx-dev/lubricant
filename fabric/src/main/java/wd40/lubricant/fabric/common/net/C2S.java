package wd40.lubricant.fabric.common.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.common.net.C2SPayload;
import wd40.lubricant.fabric.client.net.ClientReceivers;

// Fabric impl of C2SPayload. sendToServer is a client-only operation (calls
// ClientPlayNetworking.send), so it routes through ClientReceivers, which is
// itself only ever class-loaded on a client environment.
final class C2S<T extends CustomPacketPayload> implements C2SPayload<T> {

    @Override
    public void sendToServer(T payload) {
        ClientReceivers.send(payload);
    }
}

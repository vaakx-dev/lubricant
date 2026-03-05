package wd40.lubricant.fabric;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.net.C2SPayload;

// Fabric impl of C2SPayload. sendToServer is a client-only operation (calls
// ClientPlayNetworking.send), so it routes through FabricNetClientReceivers,
// which is itself only ever class-loaded on a client environment.
final class FabricC2S<T extends CustomPacketPayload> implements C2SPayload<T> {

    @Override
    public void sendToServer(T payload) {
        FabricNetClientReceivers.send(payload);
    }
}

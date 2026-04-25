package wd40.lubricant.neoforge.common.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.PacketDistributor;
import wd40.lubricant.api.common.net.C2SPayload;

// NeoForge impl of C2SPayload. sendToServer is only valid on a client connection;
// calling it from server-side code throws a runtime error inside PacketDistributor
// (matching Fabric's behavior - the compile-time C2S/S2C split prevents API misuse,
// but it can't catch "call this client-only method while running on dedicated server").
final class C2S<T extends CustomPacketPayload> implements C2SPayload<T> {

    @Override
    public void sendToServer(T payload) {
        PacketDistributor.sendToServer(payload);
    }
}

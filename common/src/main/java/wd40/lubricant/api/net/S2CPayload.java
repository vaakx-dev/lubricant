package wd40.lubricant.api.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

// Server-to-client payload handle returned by Net.toClient. Has only the send
// shapes that make sense for an S2C payload - the compiler refuses to call
// sendToServer on this type, catching direction-mismatch bugs at compile time.
public interface S2CPayload<T extends CustomPacketPayload> {
    void sendTo(ServerPlayer player, T payload);
    void sendToAll(MinecraftServer server, T payload);
    void sendToDimension(ServerLevel level, T payload);
    void sendToTracking(Entity entity, T payload);
}

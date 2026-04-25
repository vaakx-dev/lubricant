package wd40.lubricant.api.common.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Server-to-client payload handle. Returned by {@link Net#toClient}. The compiler
 * refuses {@code sendToServer} on this type - direction mismatches are compile
 * errors, never runtime.
 */
public interface S2CPayload<T extends CustomPacketPayload> {

    void sendTo(ServerPlayer player, T payload);
    void sendToAll(MinecraftServer server, T payload);
    void sendToDimension(ServerLevel level, T payload);

    /** Only players whose client is currently tracking this entity (in render range). */
    void sendToTracking(Entity entity, T payload);
}

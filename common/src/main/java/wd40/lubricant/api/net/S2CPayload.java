package wd40.lubricant.api.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

/**
 * Server-to-client payload handle returned by {@link Net#toClient}. All four
 * send methods route through the loader's broadcast facility - lubricant picks
 * the right one per loader.
 *
 * <p>The compiler refuses {@code sendToServer} on this type - that lives on
 * {@link C2SPayload}. Direction mismatch is a compile error, not a runtime one.</p>
 */
public interface S2CPayload<T extends CustomPacketPayload> {

    /** Sends to one specific player. */
    void sendTo(ServerPlayer player, T payload);

    /** Broadcasts to every connected player on this server. */
    void sendToAll(MinecraftServer server, T payload);

    /** Broadcasts to every player currently in the given dimension. */
    void sendToDimension(ServerLevel level, T payload);

    /** Sends to every player whose client is tracking this entity (within their render range). */
    void sendToTracking(Entity entity, T payload);
}

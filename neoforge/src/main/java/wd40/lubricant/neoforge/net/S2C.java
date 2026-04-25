package wd40.lubricant.neoforge.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import wd40.lubricant.api.common.net.S2CPayload;

// NeoForge impl of S2CPayload. PacketDistributor handles all four send shapes
// natively without iteration helpers.
final class S2C<T extends CustomPacketPayload> implements S2CPayload<T> {

    @Override
    public void sendTo(ServerPlayer player, T payload) {
        PacketDistributor.sendToPlayer(player, payload);
    }

    @Override
    public void sendToAll(MinecraftServer server, T payload) {
        PacketDistributor.sendToAllPlayers(payload);
    }

    @Override
    public void sendToDimension(ServerLevel level, T payload) {
        PacketDistributor.sendToPlayersInDimension(level, payload);
    }

    @Override
    public void sendToTracking(Entity entity, T payload) {
        PacketDistributor.sendToPlayersTrackingEntity(entity, payload);
    }
}

package wd40.lubricant.fabric.network;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import wd40.lubricant.api.network.payload.S2CPayload;

// Fabric impl of S2CPayload. All four sends route through ServerPlayNetworking.send,
// which is server-side API and safe on dedicated servers + integrated servers.
final class S2C<T extends CustomPacketPayload> implements S2CPayload<T> {

    @Override
    public void sendTo(ServerPlayer player, T payload) {
        ServerPlayNetworking.send(player, payload);
    }

    @Override
    public void sendToAll(MinecraftServer server, T payload) {
        for (ServerPlayer player : PlayerLookup.all(server)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void sendToDimension(ServerLevel level, T payload) {
        for (ServerPlayer player : PlayerLookup.world(level)) {
            ServerPlayNetworking.send(player, payload);
        }
    }

    @Override
    public void sendToTracking(Entity entity, T payload) {
        for (ServerPlayer player : PlayerLookup.tracking(entity)) {
            ServerPlayNetworking.send(player, payload);
        }
    }
}

package wd40.lubricant.internal;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.net.C2SPayload;
import wd40.lubricant.api.net.handlers.ClientHandler;
import wd40.lubricant.api.net.S2CPayload;
import wd40.lubricant.api.net.handlers.ServerHandler;

// Loader-specific service interface backing the public Net API. One impl per
// loader (FabricNetHelper, NeoForgeNetHelper), discovered via JDK ServiceLoader
// from META-INF/services/wd40.lubricant.internal.NetHelper.
public interface NetHelper {
    <T extends CustomPacketPayload> S2CPayload<T> toClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ClientHandler<T> handler);

    <T extends CustomPacketPayload> C2SPayload<T> toServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ServerHandler<T> handler);
}

package wd40.lubricant.api.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.net.handlers.ClientHandler;
import wd40.lubricant.api.net.handlers.ServerHandler;
import wd40.lubricant.internal.Services;

// Public entry point for declaring custom network payloads. Two static accessors -
// toClient (S2C) and toServer (C2S) - each registers a payload type + codec +
// receive handler with the loader and returns a direction-typed handle for sending.
//
// Usage:
//   public static final S2CPayload<MyPayload> CHANNEL = Net.toClient(
//           MyPayload.TYPE, MyPayload.CODEC,
//           (payload, ctx) -> ...);
//   CHANNEL.sendTo(player, new MyPayload(...));
//
// Handlers run on the main game thread; lubricant does the enqueueWork hop on
// NeoForge internally. Call these from any class loaded by Bootstrap.loadAllInit().
public final class Net {

    private Net() {}

    public static <T extends CustomPacketPayload> S2CPayload<T> toClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ClientHandler<T> handler) {
        return Services.net().toClient(type, codec, handler);
    }

    public static <T extends CustomPacketPayload> C2SPayload<T> toServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ServerHandler<T> handler) {
        return Services.net().toServer(type, codec, handler);
    }
}

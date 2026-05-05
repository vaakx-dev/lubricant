package wd40.lubricant.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.network.handlers.client.ClientHandler;
import wd40.lubricant.api.network.handlers.server.ServerHandler;
import wd40.lubricant.api.network.payload.C2SPayload;
import wd40.lubricant.api.network.payload.S2CPayload;
import wd40.lubricant.internal.Services;

/**
 * Declares custom network payloads. Direction is enforced at compile time -
 * an {@link S2CPayload} can't call {@code sendToServer} and vice versa.
 *
 * <p>Receive handlers run on the main game thread on both loaders.</p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/Networking">Networking wiki</a></p>
 */
public final class Network {

    private Network() {}

    public static <T extends CustomPacketPayload> S2CPayload<T> toClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ClientHandler<T> handler) {
        return Services.network().toClient(type, codec, handler);
    }

    public static <T extends CustomPacketPayload> C2SPayload<T> toServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ServerHandler<T> handler) {
        return Services.network().toServer(type, codec, handler);
    }
}

package wd40.lubricant.api.common.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.client.net.handlers.ClientHandler;
import wd40.lubricant.api.server.net.handlers.ServerHandler;
import wd40.lubricant.core.Services;

/**
 * Declares custom network payloads. Direction is enforced at compile time -
 * an {@link S2CPayload} can't call {@code sendToServer} and vice versa.
 *
 * <p>Receive handlers run on the main game thread on both loaders.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Networking">Networking wiki</a></p>
 */
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

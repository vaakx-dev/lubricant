package wd40.lubricant.api.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.net.handlers.ClientHandler;
import wd40.lubricant.api.net.handlers.ServerHandler;
import wd40.lubricant.internal.Services;

/**
 * Declares custom network payloads. Two static accessors - {@link #toClient}
 * (S2C) and {@link #toServer} (C2S) - register a payload type, codec, and
 * receive handler with the loader and return a direction-typed handle.
 *
 * <p>The returned handle's send shapes are restricted to its direction at
 * compile time: an {@link S2CPayload} has only server-to-client send methods,
 * a {@link C2SPayload} has only {@code sendToServer}. Cross-direction sends
 * fail to compile, never to runtime errors.</p>
 *
 * <h3>Usage</h3>
 *
 * <pre>{@code
 * public static final S2CPayload<HelloPayload> HELLO = Net.toClient(
 *         HelloPayload.TYPE, HelloPayload.CODEC,
 *         (payload, ctx) -> System.out.println("got tick " + payload.tickStamp()));
 *
 * // server side, anywhere:
 * HELLO.sendToAll(server, new HelloPayload(server.getTickCount()));
 * }</pre>
 *
 * <p>Receive handlers run on the main game thread on both loaders - lubricant
 * does the {@code enqueueWork} hop on NeoForge internally.</p>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/Networking">the
 * Networking wiki page</a> for the payload triple, codec patterns, and bridge
 * diagrams.</p>
 */
public final class Net {

    private Net() {}

    /**
     * Registers a server-to-client payload. The codec installs immediately on
     * Fabric and queues for {@code RegisterPayloadHandlersEvent} on NeoForge.
     *
     * @return a handle whose send methods target one or more clients
     */
    public static <T extends CustomPacketPayload> S2CPayload<T> toClient(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ClientHandler<T> handler) {
        return Services.net().toClient(type, codec, handler);
    }

    /**
     * Registers a client-to-server payload.
     *
     * @return a handle whose only send method is {@link C2SPayload#sendToServer}
     */
    public static <T extends CustomPacketPayload> C2SPayload<T> toServer(
            CustomPacketPayload.Type<T> type,
            StreamCodec<? super RegistryFriendlyByteBuf, T> codec,
            ServerHandler<T> handler) {
        return Services.net().toServer(type, codec, handler);
    }
}

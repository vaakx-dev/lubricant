package wd40.lubricant.internal;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.net.C2SPayload;
import wd40.lubricant.api.net.S2CPayload;
import wd40.lubricant.api.net.handlers.ClientHandler;
import wd40.lubricant.api.net.handlers.ServerHandler;

/**
 * Loader-specific factory for networking payload handles. One implementation
 * per loader, discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.FabricNetHelper} - registers
 *       payloads immediately via {@code PayloadTypeRegistry.playS2C/.playC2S}.</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.NeoForgeNetHelper} - queues
 *       registrations and drains them inside a {@code RegisterPayloadHandlersEvent}
 *       listener attached to lubricant's mod bus.</li>
 * </ul>
 *
 * <p>Both impls wrap receive handlers to run on the main game thread.</p>
 */
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

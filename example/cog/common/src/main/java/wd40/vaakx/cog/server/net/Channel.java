package wd40.vaakx.cog.server.net;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.net.Net;
import wd40.lubricant.api.net.S2CPayload;
import wd40.vaakx.cog.Cog;

/**
 * Cog's network channel. Declares the {@link HelloPayload} type and the S2C
 * handle. The actual send loop lives in {@link wd40.vaakx.cog.server.Server}.
 *
 * <p>Each payload needs three vanilla pieces: a {@link CustomPacketPayload}
 * (typically a record), a {@link CustomPacketPayload.Type} carrying the id,
 * and a {@link StreamCodec} for serialization. Lubricant's {@link Net} takes
 * the Type + codec separately and returns a direction-typed handle.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Networking">Networking wiki</a></p>
 */
public final class Channel implements Init {

    public record HelloPayload(int tickStamp) implements CustomPacketPayload {
        public static final Type<HelloPayload> TYPE = new Type<>(
                ResourceLocation.fromNamespaceAndPath(Cog.ID, "hello"));

        public static final StreamCodec<RegistryFriendlyByteBuf, HelloPayload> CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_INT, HelloPayload::tickStamp,
                        HelloPayload::new);

        @Override public Type<HelloPayload> type() { return TYPE; }
    }

    public static final S2CPayload<HelloPayload> HELLO = Net.toClient(
            HelloPayload.TYPE, HelloPayload.CODEC,
            (payload, ctx) -> Cog.LOG.info("client received hello @ tick {}", payload.tickStamp()));

    public Channel() {}
}

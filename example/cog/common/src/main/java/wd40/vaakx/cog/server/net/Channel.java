package wd40.vaakx.cog.server.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.lubricant.api.Init;
import wd40.lubricant.api.net.Payloads;
import wd40.lubricant.api.net.S2CPayload;
import wd40.vaakx.cog.Cog;

/**
 * Cog's network channel. Declares the {@link HelloPayload} type and the S2C handle.
 * The actual send loop lives in {@link wd40.vaakx.cog.server.Server}.
 *
 * <p>The codec is auto-derived from the record's components. To override, declare
 * a {@code public static final StreamCodec<RegistryFriendlyByteBuf, HelloPayload> CODEC}
 * field on the record - lubricant detects it and uses it instead.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Networking">Networking wiki</a></p>
 */
public final class Channel implements Init {

    private static final Payloads PAYLOADS = Payloads.create(Cog.ID);

    public record HelloPayload(int tickStamp) implements CustomPacketPayload {
        public static final Type<HelloPayload> TYPE = PAYLOADS.type("hello");
        @Override public Type<HelloPayload> type() { return TYPE; }
    }

    public static final S2CPayload<HelloPayload> HELLO = PAYLOADS.toClient(
            HelloPayload.class,
            (payload, ctx) -> Cog.LOG.info("client received hello @ tick {}", payload.tickStamp()));

    public Channel() {}
}

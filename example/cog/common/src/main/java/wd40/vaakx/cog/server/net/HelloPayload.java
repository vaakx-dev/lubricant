package wd40.vaakx.cog.server.net;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import wd40.vaakx.cog.common.net.Channel;

/**
 * S2C payload: server tells the client about a tick stamp. Lives in
 * {@code server/net/} because the server is the author (constructs and sends it);
 * the matching {@link wd40.vaakx.cog.client.net.HelloHandler} on the client
 * side just reacts.
 *
 * <p>The codec is auto-derived from the record's components. To override,
 * declare a {@code public static final StreamCodec<RegistryFriendlyByteBuf, HelloPayload> CODEC}
 * field on the record - lubricant detects it and uses it instead.</p>
 */
public record HelloPayload(int tickStamp) implements CustomPacketPayload {

    public static final Type<HelloPayload> TYPE = Channel.PAYLOADS.type("hello");

    @Override
    public Type<HelloPayload> type() {
        return TYPE;
    }
}

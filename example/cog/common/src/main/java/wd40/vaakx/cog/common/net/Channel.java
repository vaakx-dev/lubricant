package wd40.vaakx.cog.common.net;

import wd40.lubricant.api.init.CommonInit;
import wd40.lubricant.api.network.Payloads;
import wd40.lubricant.api.network.payload.S2CPayload;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.client.net.HelloHandler;
import wd40.vaakx.cog.server.net.HelloPayload;

/**
 * Cog's network channel registration. Owns the per-mod {@link Payloads} factory
 * and the typed S2C/C2S handles that the rest of cog calls into.
 *
 * <p>This class is the only place that wires payloads into lubricant. It is
 * common (loaded on both sides) because the same handle is used by senders on
 * one side and receivers on the other.</p>
 *
 * <p>Per cog's package convention:
 * <ul>
 *   <li>S2C payload <i>records</i> live in {@code server/net/} - the server
 *       authors them.</li>
 *   <li>C2S payload records live in {@code client/net/} - the client authors them.</li>
 *   <li>Handlers live opposite to the record (so an S2C record in server/net
 *       has its handler in client/net).</li>
 * </ul></p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/Networking">Networking wiki</a></p>
 */
public final class Channel implements CommonInit {

    public static final Payloads PAYLOADS = Payloads.create(Cog.ID);

    public static final S2CPayload<HelloPayload> HELLO = PAYLOADS.toClient(
            HelloPayload.class, HelloHandler::handle);

    public Channel() {}
}

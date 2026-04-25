package wd40.vaakx.cog.client.net;

import wd40.lubricant.api.net.handlers.ClientContext;
import wd40.vaakx.cog.Cog;
import wd40.vaakx.cog.server.net.HelloPayload;

/**
 * Client-side handler for {@link HelloPayload}. Lives opposite the record
 * (record in {@code server/net/}, handler in {@code client/net/}) per cog's
 * convention - handlers run on the receiving side.
 */
public final class HelloHandler {

    public static void handle(HelloPayload payload, ClientContext ctx) {
        Cog.LOG.info("client received hello @ tick {}", payload.tickStamp());
    }

    private HelloHandler() {}
}

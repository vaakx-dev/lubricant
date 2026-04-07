package wd40.vaakx.cog.client;

import wd40.lubricant.api.Init;
import wd40.lubricant.api.client.Renderers;
import wd40.vaakx.cog.server.entities.Entities;

/**
 * All of cog's client-side setup in one place. Mirrors {@link wd40.vaakx.cog.server.Server}
 * for the client side.
 *
 * <p>Loaded as a regular {@link Init} - listed in
 * {@code META-INF/services/wd40.lubricant.api.Init}. Lubricant's
 * {@link Renderers} facade short-circuits to no-op on dedicated server, so
 * this class is safe to load on any side; the actual renderer registration
 * only takes effect on the client.</p>
 */
public final class Client implements Init {

    static {
        Renderers.entity(Entities.SPINNING_COG, SpinningCogRenderer::new);
    }

    public Client() {}
}

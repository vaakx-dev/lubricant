package wd40.lubricant.api.init;

/**
 * Marker for classes that should load only on the client side. Static blocks
 * here may safely reference {@code net.minecraft.client.*} types, register
 * {@link EntityRenderers}, {@link ParticleRenderers}, key bindings, HUD
 * elements - anything that would crash on a dedicated server.
 *
 * <p>List your implementations in
 * {@code META-INF/services/wd40.lubricant.api.init.ClientInit}. The loader
 * skips this list on dedicated server; on client, every listed class loads
 * once during client init.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/How-It-Works">How It Works wiki</a></p>
 */
public interface ClientInit {
}

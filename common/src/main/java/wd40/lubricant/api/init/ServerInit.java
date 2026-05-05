package wd40.lubricant.api.init;

/**
 * Marker for classes that should load only on the logical server side. Static
 * blocks here run on dedicated server and on a client running an integrated
 * (single-player) server, but never on a client connecting to a remote server
 * with no local server thread. Typical home for {@code ServerEvent}-style
 * subscribers, command registration, and any state that lives on the server tick.
 *
 * <p>List your implementations in
 * {@code META-INF/services/wd40.lubricant.api.init.ServerInit}.</p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/How-It-Works">How It Works wiki</a></p>
 */
public interface ServerInit {
}

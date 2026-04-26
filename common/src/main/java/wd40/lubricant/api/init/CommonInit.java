package wd40.lubricant.api.init;

/**
 * Marker for classes lubricant should force-load on every side. Static blocks
 * here run on both dedicated server and client - typical home for registry
 * declarations, attached-data keys, packet type registration. Anything that
 * needs to exist before either side does runtime work.
 *
 * <p>List your implementations in
 * {@code META-INF/services/wd40.lubricant.api.init.CommonInit}. Loading runs
 * the class's static initializers, which is where registration code goes.
 * Implementations need a public no-arg constructor (ServiceLoader requirement);
 * the body stays empty since work happens in {@code <clinit>} before it runs.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/How-It-Works">How It Works wiki</a></p>
 */
public interface CommonInit {
}

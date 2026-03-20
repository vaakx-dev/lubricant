package wd40.lubricant.api;

/**
 * Marker for classes lubricant should force-load at startup.
 *
 * <p>List your implementations in {@code META-INF/services/wd40.lubricant.api.Init}.
 * Loading runs the class's static initializers, which is where registration code
 * goes. Implementations need a public no-arg constructor (ServiceLoader requirement);
 * the body stays empty since work happens in {@code <clinit>} before it runs.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/How-It-Works">How It Works wiki</a></p>
 */
public interface Init {
}

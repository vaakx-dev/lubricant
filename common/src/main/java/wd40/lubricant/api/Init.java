package wd40.lubricant.api;

/**
 * Marker interface for classes lubricant should force-load at startup.
 *
 * <p>Implementations are discovered via JDK {@link java.util.ServiceLoader}: list
 * each implementing class by FQN in
 * {@code META-INF/services/wd40.lubricant.api.Init} inside your mod's resources.
 * At lubricant boot, every listed class is loaded, which runs its {@code <clinit>}
 * (static field initializers and {@code static {}} blocks). The static initializer
 * is where your registration code lives:</p>
 *
 * <pre>{@code
 * public final class Items implements Init {
 *     public static final ItemRegistry ITEMS = ItemRegistry.create("mymod");
 *     public static final Supplier<Item> WRENCH = ITEMS.register("wrench", ...);
 *     public Items() {}
 * }
 * }</pre>
 *
 * <p>The interface is intentionally empty - lubricant doesn't call any method on
 * your class. The mere act of loading the class is what runs your code, by way
 * of standard JVM class-initialization.</p>
 *
 * <p>Implementations need a public no-arg constructor (required by
 * {@link java.util.ServiceLoader}). The body can be empty - the work has already
 * happened in {@code <clinit>} by the time the constructor runs.</p>
 *
 * <p>See <a href="https://github.com/vaakxxx/lubricant/wiki/How-It-Works">the
 * "How It Works" wiki page</a> for the full chain from loader entry to your
 * static block.</p>
 */
public interface Init {
}

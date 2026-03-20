package wd40.lubricant.internal;

import wd40.lubricant.api.Init;

import java.util.ServiceLoader;

/**
 * Force-loads every {@link Init} implementation via JDK {@link ServiceLoader}.
 *
 * <p>Iterating the {@code ServiceLoader} instantiates each provider class in
 * turn. The first reference to each class triggers JVM class initialization,
 * which runs the class's {@code <clinit>} - i.e. its static field initializers
 * and {@code static {}} blocks. That's where consumer mods put their
 * registration code.</p>
 *
 * <p>Loader entry points ({@code LubricantFabric.onInitialize},
 * {@code LubricantNeoForge}'s constructor) call {@link #loadAllInit} once each,
 * during their own startup. After this method returns, every consumer mod's
 * registration code has run.</p>
 *
 * @implNote The for-each loop body is empty by design - touching the iterator
 * variable is enough to load the class. We don't call any method on the
 * provider because {@link Init} has none.
 */
public final class Bootstrap {

    /**
     * Iterates every {@code Init} provider on the classpath, forcing each to
     * load. Idempotent - calling twice loads each class only once (JVM caches
     * loaded classes).
     */
    public static void loadAllInit() {
        for (Init unused : ServiceLoader.load(Init.class)) {
            // touched -> JVM ran <clinit> on the class. Nothing else to do per service.
        }
    }

    private Bootstrap() {}
}

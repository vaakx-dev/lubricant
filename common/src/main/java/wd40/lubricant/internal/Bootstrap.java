package wd40.lubricant.internal;

import wd40.lubricant.api.init.ClientInit;
import wd40.lubricant.api.init.CommonInit;
import wd40.lubricant.api.init.ServerInit;

import java.util.ServiceLoader;

/**
 * Force-loads {@link CommonInit}, {@link ServerInit}, and {@link ClientInit}
 * implementations via JDK {@link ServiceLoader}.
 *
 * <p>Iterating a {@code ServiceLoader} instantiates each provider class in
 * turn. The first reference to each class triggers JVM class initialization,
 * which runs the class's {@code <clinit>} - i.e. its static field initializers
 * and {@code static {}} blocks. That's where consumer mods put their
 * registration code.</p>
 *
 * <p>Loader entry points wire the sides:
 * <ul>
 *   <li>Fabric: {@link #loadCommon} + {@link #loadServer} from {@code Entry#onInitialize};
 *       {@link #loadClient} from {@code ClientEntry#onInitializeClient}.</li>
 *   <li>NeoForge: {@link #loadCommon} + {@link #loadServer} always; {@link #loadClient}
 *       only when {@code FMLEnvironment.dist.isClient()}.</li>
 * </ul></p>
 *
 * <p>Each method is idempotent - calling twice loads each class only once
 * (JVM caches loaded classes).</p>
 *
 * @implNote The for-each loop body is empty by design - touching the iterator
 * variable is enough to load the class.
 */
public final class Bootstrap {

    public static void loadCommon() {
        for (CommonInit unused : ServiceLoader.load(CommonInit.class)) {
            // touched -> JVM ran <clinit>
        }
    }

    public static void loadServer() {
        for (ServerInit unused : ServiceLoader.load(ServerInit.class)) {
            // touched -> JVM ran <clinit>
        }
    }

    public static void loadClient() {
        for (ClientInit unused : ServiceLoader.load(ClientInit.class)) {
            // touched -> JVM ran <clinit>
        }
    }

    private Bootstrap() {}
}

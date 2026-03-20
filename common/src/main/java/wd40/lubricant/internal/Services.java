package wd40.lubricant.internal;

import java.util.ServiceLoader;

/**
 * Lazy lookup table for loader-specific helper services. The public API
 * (e.g. {@link wd40.lubricant.api.registry.ItemRegistry#create}) calls into
 * these accessors rather than touching loader code directly, keeping the API
 * loader-agnostic.
 *
 * <p>Each helper is loaded via JDK {@link ServiceLoader} on first access and
 * cached for the JVM lifetime. The provider files live in each loader module's
 * resources at {@code META-INF/services/wd40.lubricant.internal.<HelperName>}.</p>
 *
 * <p>Threading: races on first call may instantiate duplicate helpers, but only
 * one wins the assignment to the volatile field. Helpers must be safe to
 * construct multiple times (lubricant's are - they're stateless or self-init).</p>
 */
public final class Services {

    private static volatile EventHelper EVENTS;
    private static volatile NetHelper NET;

    /** The loader-specific {@link EventHelper}, used by {@code Events}. */
    public static EventHelper events() {
        EventHelper local = EVENTS;
        if (local == null) {
            local = ServiceLoader.load(EventHelper.class).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No lubricant EventHelper service found - is the lubricant loader module on the classpath?"));
            EVENTS = local;
        }
        return local;
    }

    /** The loader-specific {@link NetHelper}, used by {@code Net}. */
    public static NetHelper net() {
        NetHelper local = NET;
        if (local == null) {
            local = ServiceLoader.load(NetHelper.class).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No lubricant NetHelper service found - is the lubricant loader module on the classpath?"));
            NET = local;
        }
        return local;
    }

    private Services() {}
}

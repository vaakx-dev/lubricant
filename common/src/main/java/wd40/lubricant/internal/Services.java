package wd40.lubricant.internal;

import java.util.ServiceLoader;

// Looks up the loader-specific RegistryHelper via JDK ServiceLoader.
// Cached on first access. Threading note: races on first call produce duplicate
// helpers but the same one ends up assigned, so it's fine.
public final class Services {

    private static volatile RegistryHelper REGISTRY;
    private static volatile EventHelper EVENTS;

    public static RegistryHelper registry() {
        RegistryHelper local = REGISTRY;
        if (local == null) {
            local = ServiceLoader.load(RegistryHelper.class).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No lubricant RegistryHelper service found - is the lubricant loader module on the classpath?"));
            REGISTRY = local;
        }
        return local;
    }

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

    private Services() {}
}

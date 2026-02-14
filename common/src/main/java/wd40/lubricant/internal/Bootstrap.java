package wd40.lubricant.internal;

import wd40.lubricant.api.Init;

import java.util.ServiceLoader;

// Force-loads every Init service implementation. Iterating ServiceLoader instantiates
// each provider, which triggers its class's static initializer. That's where mod authors
// declare ItemRegistry.create + .register() calls, so this is what fills the registry queues.
//
// Loader entry points call this once during their own startup.
public final class Bootstrap {

    public static void loadAllInit() {
        for (Init unused : ServiceLoader.load(Init.class)) {
            // touched -> JVM ran <clinit> on the class. Nothing else to do per service.
        }
    }

    private Bootstrap() {}
}

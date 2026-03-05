package wd40.lubricant.internal;

import wd40.lubricant.api.Event;

import java.util.function.Consumer;

// Generic Event impl shared by both loaders. The bridge function knows how to wire a
// listener into the underlying loader event - the EventHelper just builds one of these
// per event with a closure that calls e.g. ServerTickEvents.END_SERVER_TICK.register(...)
// or NeoForge.EVENT_BUS.addListener(...).
public final class BridgedEvent<L> implements Event<L> {

    private final Consumer<L> bridge;

    public BridgedEvent(Consumer<L> bridge) {
        this.bridge = bridge;
    }

    @Override
    public void subscribe(L listener) {
        bridge.accept(listener);
    }
}

package wd40.lubricant.core.events;

import wd40.lubricant.api.events.Event;

import java.util.function.Consumer;

/**
 * Generic {@link Event} implementation shared by both loader impls.
 *
 * <p>The {@code bridge} function captures how to wire a listener of type
 * {@code L} into the underlying loader event. The {@link EventHelper} impl
 * builds one of these per event with a closure that calls e.g.
 * {@code ServerTickEvents.END_SERVER_TICK.register(...)} on Fabric or
 * {@code NeoForge.EVENT_BUS.addListener(...)} on NeoForge.</p>
 *
 * <p>Subscribers don't outlive the JVM - there's no {@code unsubscribe} because
 * loader event registries don't generally support removal either.</p>
 */
public final class BridgedEvent<L> implements Event<L> {

    private final Consumer<L> bridge;

    /** @param bridge wires a listener into the loader's underlying event */
    public BridgedEvent(Consumer<L> bridge) {
        this.bridge = bridge;
    }

    @Override
    public void subscribe(L listener) {
        bridge.accept(listener);
    }
}

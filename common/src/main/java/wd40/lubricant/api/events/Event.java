package wd40.lubricant.api.events;

/**
 * One subscription point for a specific kind of event. Returned by accessor
 * methods on {@link Events}. Call {@link #subscribe} with a listener (typically
 * a lambda) to wire it to the underlying loader event.
 *
 * <p>The type parameter {@code L} is the listener shape - a {@link java.util.function.Consumer}
 * over a vanilla MC type for most events, or {@link ItemUseListener} for the
 * right-click-item event.</p>
 *
 * <p>Subscribe from any class lubricant force-loads, typically a static block
 * in your own {@link wd40.lubricant.api.Init} implementation.</p>
 */
public interface Event<L> {

    /** Wires {@code listener} to fire when this event happens. Listeners aren't unsubscribable. */
    void subscribe(L listener);
}

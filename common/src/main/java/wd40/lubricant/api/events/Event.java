package wd40.lubricant.api.events;

/** Subscription point returned by {@link Events} accessors. Listeners aren't unsubscribable. */
public interface Event<L> {
    void subscribe(L listener);
}

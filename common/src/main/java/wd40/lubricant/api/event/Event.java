package wd40.lubricant.api.event;

/** Subscription point returned by event-holder constants. Listeners aren't unsubscribable. */
public interface Event<L> {
    void register(L listener);
}

package wd40.lubricant.api;

// One subscription point for a specific kind of event. The type parameter L is the
// listener shape (typically a Consumer<...> over a vanilla MC type, or a custom
// functional interface like ItemUseListener).
//
// Subscribe from any class that runs during Bootstrap.loadAllInit() - typically a
// static initializer in your own Init implementation.
public interface Event<L> {
    void subscribe(L listener);
}

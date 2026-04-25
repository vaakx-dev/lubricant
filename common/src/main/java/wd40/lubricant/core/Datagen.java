package wd40.lubricant.core;

import wd40.lubricant.api.common.events.Event;

/**
 * Static flag tracking whether the current JVM is the datagen runner. The
 * datagen JVM (started by {@code wd40.lubricant.datagen.DatagenMain}) has no
 * loader module on classpath, so every {@link Services} lookup would otherwise
 * fail. Modder code reads attached-data {@link wd40.lubricant.api.common.data.DataKey}s
 * and event subscribers from class-init blocks, and those static blocks run
 * during datagen as a side effect of the datagen entry referencing the mod's
 * {@code Items}, {@code Blocks}, etc. classes.
 *
 * <p>{@link #IS_DATAGEN} is set by {@code DatagenMain.main} via a system
 * property. Every facade in {@code api.events}, {@code api.net}, and
 * {@code api.data} checks this flag and returns a no-op result instead of
 * looking up Services. Result: registrations and subscriptions during datagen
 * are silently skipped; no exception, no side effect.</p>
 */
public final class Datagen {

    public static final boolean IS_DATAGEN = "true".equals(System.getProperty("lubricant.datagen"));

    /** Shared no-op {@link Event} - {@code subscribe} ignores the listener. */
    @SuppressWarnings("rawtypes")
    private static final Event NO_OP_EVENT = listener -> {};

    @SuppressWarnings("unchecked")
    public static <L> Event<L> noOpEvent() {
        return (Event<L>) NO_OP_EVENT;
    }

    private Datagen() {}
}

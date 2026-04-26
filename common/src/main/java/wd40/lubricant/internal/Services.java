package wd40.lubricant.internal;

import wd40.lubricant.internal.rendering.RendererHelper;
import wd40.lubricant.internal.data.BlockEntityHelper;
import wd40.lubricant.internal.data.EntityHelper;
import wd40.lubricant.internal.data.StackHelper;
import wd40.lubricant.internal.event.EventHelper;
import wd40.lubricant.internal.network.NetworkHelper;

import java.util.ServiceLoader;

/**
 * Lazy lookup table for loader-specific helper services. The public API
 * (e.g. {@link wd40.lubricant.api.item.ItemRegistry#create}) calls into
 * these accessors rather than touching loader code directly, keeping the API
 * loader-agnostic.
 *
 * <p>Each helper is loaded via JDK {@link ServiceLoader} on first access and
 * cached for the JVM lifetime. The provider files live in each loader module's
 * resources at {@code META-INF/services/wd40.lubricant.internal.event.EventHelper},
 * {@code wd40.lubricant.internal.network.NetworkHelper}, and
 * {@code wd40.lubricant.internal.data.StackHelper}.</p>
 *
 * <p>Threading: races on first call may instantiate duplicate helpers, but only
 * one wins the assignment to the volatile field. Helpers must be safe to
 * construct multiple times (lubricant's are - they're stateless or self-init).</p>
 */
public final class Services {

    private static volatile EventHelper EVENTS;
    private static volatile NetworkHelper NET;
    private static volatile StackHelper STACKS;
    private static volatile BlockEntityHelper BLOCK_ENTITIES;
    private static volatile EntityHelper ENTITIES;
    private static volatile RendererHelper RENDERERS;
    private static volatile boolean RENDERERS_LOADED;

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

    /** The loader-specific {@link NetworkHelper}, used by {@code Network}. */
    public static NetworkHelper network() {
        NetworkHelper local = NET;
        if (local == null) {
            local = ServiceLoader.load(NetworkHelper.class).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No lubricant NetworkHelper service found - is the lubricant loader module on the classpath?"));
            NET = local;
        }
        return local;
    }

    /** The loader-specific {@link StackHelper}, used by {@code Stacks}. */
    public static StackHelper stacks() {
        StackHelper local = STACKS;
        if (local == null) {
            local = ServiceLoader.load(StackHelper.class).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No lubricant StackHelper service found - is the lubricant loader module on the classpath?"));
            STACKS = local;
        }
        return local;
    }

    /** The loader-specific {@link BlockEntityHelper}, used by {@code BlockEntities}. */
    public static BlockEntityHelper blockEntities() {
        BlockEntityHelper local = BLOCK_ENTITIES;
        if (local == null) {
            local = ServiceLoader.load(BlockEntityHelper.class).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No lubricant BlockEntityHelper service found - is the lubricant loader module on the classpath?"));
            BLOCK_ENTITIES = local;
        }
        return local;
    }

    /** The loader-specific {@link EntityHelper}, used by {@code Entities}. */
    public static EntityHelper entities() {
        EntityHelper local = ENTITIES;
        if (local == null) {
            local = ServiceLoader.load(EntityHelper.class).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            "No lubricant EntityHelper service found - is the lubricant loader module on the classpath?"));
            ENTITIES = local;
        }
        return local;
    }

    /**
     * The loader-specific {@link RendererHelper}, or {@code null} if none is
     * available. Unlike the other accessors, this one returns null instead of
     * throwing - renderers are inherently client-only, and on a dedicated
     * server the loader's renderer impl service file is absent, which is a
     * normal runtime state, not an error.
     */
    public static RendererHelper renderers() {
        if (RENDERERS_LOADED) return RENDERERS;
        synchronized (Services.class) {
            if (RENDERERS_LOADED) return RENDERERS;
            try {
                RENDERERS = ServiceLoader.load(RendererHelper.class).findFirst().orElse(null);
            } catch (Throwable ignored) {
                RENDERERS = null;  // class stripped on this side, etc.
            }
            RENDERERS_LOADED = true;
            return RENDERERS;
        }
    }

    private Services() {}
}

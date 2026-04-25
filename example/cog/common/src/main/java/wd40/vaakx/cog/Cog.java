package wd40.vaakx.cog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod-wide constants for cog (the lubricant example mod). One file per mod at
 * the package root holding two statics: {@link #ID} and {@link #LOG}.
 *
 * <p>Reference {@code Cog.ID} everywhere a mod id is needed. The string MUST
 * match:</p>
 * <ul>
 *   <li>{@code fabric.mod.json} -&gt; {@code "id"}</li>
 *   <li>{@code neoforge.mods.toml} -&gt; {@code [[mods]].modId}</li>
 *   <li>The {@code @Mod(...)} annotation on {@code CogNeoForge}</li>
 *   <li>The lookup namespace in {@code META-INF/services/wd40.lubricant.api.common.CommonInit}
 *       paths and resource locations like {@code cog:wrench}</li>
 * </ul>
 *
 * <p>If any of these drift out of sync, the mod loads but registrations land
 * under the wrong namespace - subtle bugs, no obvious errors.</p>
 *
 * <p>{@link #LOG} is the mod-wide slf4j {@link Logger} tagged with the mod id,
 * so log lines render as {@code [cog/INFO] message}. Use it everywhere instead
 * of creating per-class loggers.</p>
 */
public final class Cog {
    public static final String ID = "cog";

    public static final Logger LOG = LoggerFactory.getLogger(ID);

    private Cog() {}
}

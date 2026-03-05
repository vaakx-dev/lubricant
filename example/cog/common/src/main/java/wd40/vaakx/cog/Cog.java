package wd40.vaakx.cog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mod-wide constants. Any time you need the mod id, reference {@link #ID} -
 * never hardcode the string elsewhere. Convention is to keep this class tiny
 * and put it at the package root so every other class reaches it via one
 * import.
 *
 * The string here MUST match:
 *   - fabric.mod.json's "id"
 *   - quilt.mod.json's quilt_loader.id
 *   - neoforge.mods.toml's [[mods]].modId
 *   - the @Mod annotation in your CogNeoForge entry class
 *   - the META-INF/services/wd40.lubricant.api.Init filename's lookup
 *
 * If they get out of sync, things break in subtle ways.
 *
 * {@link #LOG} is the mod-wide slf4j logger - tagged with the mod id so log
 * lines render as {@code [cog/INFO]}. Reuse it everywhere instead of creating
 * per-class loggers.
 */
public final class Cog {
    public static final String ID = "cog";

    public static final Logger LOG = LoggerFactory.getLogger(ID);

    private Cog() {}
}

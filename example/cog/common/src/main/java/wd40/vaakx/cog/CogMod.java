package wd40.vaakx.cog;

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
 */
public final class CogMod {
    public static final String ID = "cog";

    private CogMod() {}
}

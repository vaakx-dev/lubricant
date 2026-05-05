package wd40.vaakx.cog.common.config;

/**
 * Cog's config POJO. Public fields so GSON serializes them with no annotations.
 * Defaults assigned inline; missing fields after a partial JSON read fall back
 * to whatever the constructor leaves them at.
 */
public final class CogConfig {

    /** How much each Counter block right-click adds to the per-BE click counter. */
    public int counterStep = 1;

    /** If true, cog logs every COGS-tagged item use to the server log. */
    public boolean logTagUses = true;

    public CogConfig() {}
}

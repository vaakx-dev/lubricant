package wd40.vaakx.cog.common.config;

import wd40.lubricant.api.config.Config;
import wd40.lubricant.api.init.CommonInit;
import wd40.vaakx.cog.Cog;

/**
 * Cog's loaded config. Same lifecycle as
 * {@link wd40.vaakx.cog.common.items.Items} - see Items.java.
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/Config">Config wiki</a></p>
 */
public final class Configs implements CommonInit {

    public static final Config<CogConfig> COG =
            Config.load(Cog.ID, CogConfig.class, CogConfig::new);

    public Configs() {}
}

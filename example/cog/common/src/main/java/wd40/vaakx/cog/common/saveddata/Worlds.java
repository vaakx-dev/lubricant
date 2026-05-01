package wd40.vaakx.cog.common.saveddata;

import com.mojang.serialization.Codec;
import wd40.lubricant.api.init.CommonInit;
import wd40.lubricant.api.saveddata.WorldData;
import wd40.vaakx.cog.Cog;

/**
 * Per-world totals persisted via {@link WorldData}. Same lifecycle as
 * {@link wd40.vaakx.cog.common.items.Items} - see Items.java.
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/WorldData">WorldData wiki</a></p>
 */
public final class Worlds implements CommonInit {

    /** Total Counter-block clicks across all sessions in this dimension. */
    public static final WorldData<Integer> COUNTER_TOTAL =
            WorldData.register(Cog.ID, "counter_total", Codec.INT, () -> 0);

    public Worlds() {}
}

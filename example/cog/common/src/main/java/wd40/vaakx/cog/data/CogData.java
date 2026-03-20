package wd40.vaakx.cog.data;

import wd40.lubricant.api.data.DataInit;
import wd40.lubricant.api.data.LubricantData;
import wd40.vaakx.cog.blocks.Blocks;
import wd40.vaakx.cog.items.Items;

/**
 * Cog's datagen entry. Loaded via ServiceLoader at datagen time only - not at
 * normal mod boot. Listed in
 * {@code META-INF/services/wd40.lubricant.api.data.DataInit}.
 *
 * <p>Each {@code defaults(...)} call asks lubricant to emit the standard JSON
 * lubricant would otherwise have to be hand-written: item/generated models,
 * cube_all blockstates + block models, and lang entries derived from the id.
 * Any file already present in {@code src/main/resources/assets/cog/...} wins.</p>
 */
public final class CogData implements DataInit {

    @Override
    public void onData(LubricantData d) {
        d.defaults(Items.ITEMS);
        d.defaults(Blocks.BLOCKS);
    }

    public CogData() {}
}

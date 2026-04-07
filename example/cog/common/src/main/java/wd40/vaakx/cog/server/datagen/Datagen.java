package wd40.vaakx.cog.server.datagen;

import wd40.lubricant.api.datagen.DatagenInit;
import wd40.vaakx.cog.server.blocks.Blocks;
import wd40.vaakx.cog.server.items.Items;
import wd40.vaakx.cog.server.particles.Particles;
import wd40.vaakx.cog.server.sounds.Sounds;
import wd40.vaakx.cog.server.tabs.Tabs;

/**
 * Cog's datagen entry. Loaded via ServiceLoader at datagen time only - not at
 * normal mod boot. Listed in
 * {@code META-INF/services/wd40.lubricant.api.datagen.DatagenInit}.
 *
 * <p>Each {@code defaults(...)} call asks lubricant to emit the standard JSON
 * cog would otherwise have to hand-write: item/generated models, cube_all
 * blockstates + block models, and lang entries derived from the id. Any file
 * already present in {@code src/main/resources/assets/cog/...} wins.</p>
 *
 * <p>The simple name {@code Datagen} matches the lubricant facade type
 * ({@link wd40.lubricant.api.datagen.Datagen}); this file uses a fully-qualified
 * reference for the facade in the method signature to dodge the import clash.</p>
 */
public final class Datagen implements DatagenInit {

    @Override
    public void onDatagen(wd40.lubricant.api.datagen.Datagen datagen) {
        datagen.defaults(Items.ITEMS);
        datagen.defaults(Blocks.BLOCKS);
        datagen.defaults(Sounds.SOUNDS);
        datagen.defaults(Particles.PARTICLES);
        datagen.defaults(Tabs.TABS);
    }

    public Datagen() {}
}

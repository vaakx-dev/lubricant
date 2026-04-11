package wd40.vaakx.cog.server.datagen;

import wd40.lubricant.api.datagen.DataGenerator;
import wd40.lubricant.api.datagen.DatagenInit;
import wd40.lubricant.api.datagen.Pack;
import wd40.lubricant.api.datagen.providers.BlockstateProvider;
import wd40.lubricant.api.datagen.providers.ItemModelProvider;
import wd40.lubricant.api.datagen.providers.ParticlesProvider;
import wd40.lubricant.api.datagen.providers.SoundsProvider;
import wd40.lubricant.api.datagen.providers.TabProvider;
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
 * <p>Each built-in provider takes the registry it cares about and emits the
 * standard JSON cog would otherwise hand-write: item/generated models, cube_all
 * blockstates + block models, sounds.json, particles JSON, lang. Files already
 * present in {@code src/main/resources/assets/cog/...} are skipped.</p>
 */
public final class Datagen implements DatagenInit {

    @Override
    public void onDatagen(DataGenerator gen) {
        Pack pack = gen.createPack();
        pack.addProvider(out -> new ItemModelProvider(out, Items.ITEMS));
        pack.addProvider(out -> new BlockstateProvider(out, Blocks.BLOCKS));
        pack.addProvider(out -> new SoundsProvider(out, Sounds.SOUNDS));
        pack.addProvider(out -> new ParticlesProvider(out, Particles.PARTICLES));
        pack.addProvider(out -> new TabProvider(out, Tabs.TABS));
    }

    public Datagen() {}
}

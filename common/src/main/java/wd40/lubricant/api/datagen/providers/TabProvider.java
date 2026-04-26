package wd40.lubricant.api.datagen.providers;

import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.datagen.DataOutput;
import wd40.lubricant.api.datagen.DataProvider;
import wd40.lubricant.api.datagen.providers.support.Names;
import wd40.lubricant.api.item.CreativeTabRegistry;

/**
 * Writes an {@code itemGroup.<modid>.<path>} lang entry per tab. Tabs need no
 * other JSON.
 */
public final class TabProvider implements DataProvider {

    private final DataOutput out;
    private final CreativeTabRegistry registry;

    public TabProvider(DataOutput out, CreativeTabRegistry registry) {
        this.out = out;
        this.registry = registry;
    }

    @Override
    public void generate() {
        for (ResourceLocation id : registry.ids()) {
            out.addLang(id.getNamespace(),
                    "itemGroup." + id.getNamespace() + "." + id.getPath(),
                    Names.titleCase(id.getPath()));
        }
    }
}

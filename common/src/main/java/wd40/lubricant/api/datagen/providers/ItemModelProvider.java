package wd40.lubricant.api.datagen.providers;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.datagen.DataOutput;
import wd40.lubricant.api.datagen.DataProvider;
import wd40.lubricant.api.datagen.providers.support.Names;
import wd40.lubricant.api.registry.ItemRegistry;

/**
 * Writes {@code item/generated} models for every entry in an {@link ItemRegistry}
 * plus {@code item.<modid>.<path>} lang entries. Skips any model file the
 * consumer hand-authored under {@code src/main/resources/assets/...}.
 */
public final class ItemModelProvider implements DataProvider {

    private final DataOutput out;
    private final ItemRegistry registry;

    public ItemModelProvider(DataOutput out, ItemRegistry registry) {
        this.out = out;
        this.registry = registry;
    }

    @Override
    public void generate() {
        for (ResourceLocation id : registry.ids()) {
            String relPath = id.getNamespace() + "/models/item/" + id.getPath() + ".json";
            if (!out.isHandAuthored(relPath)) {
                JsonObject json = new JsonObject();
                json.addProperty("parent", "item/generated");
                JsonObject tex = new JsonObject();
                tex.addProperty("layer0", id.getNamespace() + ":item/" + id.getPath());
                json.add("textures", tex);
                out.writeJson(relPath, json);
            }
            out.addLang(id.getNamespace(),
                    "item." + id.getNamespace() + "." + id.getPath(),
                    Names.titleCase(id.getPath()));
        }
    }
}

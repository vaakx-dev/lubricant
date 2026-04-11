package wd40.lubricant.api.datagen.providers;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.datagen.DataOutput;
import wd40.lubricant.api.datagen.DataProvider;
import wd40.lubricant.api.datagen.providers.support.Names;
import wd40.lubricant.api.registry.BlockRegistry;

import java.util.Set;

/**
 * Writes default blockstate + block model + (when not no-item) BlockItem model
 * for every entry in a {@link BlockRegistry}, plus block lang entries. Skips
 * any file the consumer hand-authored.
 */
public final class BlockstateProvider implements DataProvider {

    private final DataOutput out;
    private final BlockRegistry registry;

    public BlockstateProvider(DataOutput out, BlockRegistry registry) {
        this.out = out;
        this.registry = registry;
    }

    @Override
    public void generate() {
        Set<ResourceLocation> noItem = registry.noItemIds();
        for (ResourceLocation id : registry.ids()) {
            String modelId = id.getNamespace() + ":block/" + id.getPath();

            String blockstatePath = id.getNamespace() + "/blockstates/" + id.getPath() + ".json";
            if (!out.isHandAuthored(blockstatePath)) {
                JsonObject variant = new JsonObject();
                variant.addProperty("model", modelId);
                JsonObject variants = new JsonObject();
                variants.add("", variant);
                JsonObject blockstate = new JsonObject();
                blockstate.add("variants", variants);
                out.writeJson(blockstatePath, blockstate);
            }

            String blockModelPath = id.getNamespace() + "/models/block/" + id.getPath() + ".json";
            if (!out.isHandAuthored(blockModelPath)) {
                JsonObject blockModel = new JsonObject();
                blockModel.addProperty("parent", "block/cube_all");
                JsonObject tex = new JsonObject();
                tex.addProperty("all", id.getNamespace() + ":block/" + id.getPath());
                blockModel.add("textures", tex);
                out.writeJson(blockModelPath, blockModel);
            }

            if (!noItem.contains(id)) {
                String itemModelPath = id.getNamespace() + "/models/item/" + id.getPath() + ".json";
                if (!out.isHandAuthored(itemModelPath)) {
                    JsonObject itemModel = new JsonObject();
                    itemModel.addProperty("parent", modelId);
                    out.writeJson(itemModelPath, itemModel);
                }
            }

            out.addLang(id.getNamespace(),
                    "block." + id.getNamespace() + "." + id.getPath(),
                    Names.titleCase(id.getPath()));
        }
    }
}

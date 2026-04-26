package wd40.lubricant.api.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.datagen.DataOutput;
import wd40.lubricant.api.datagen.DataProvider;
import wd40.lubricant.api.particle.ParticleRegistry;

/**
 * Writes a {@code particles/<path>.json} per particle with a single texture
 * {@code <modid>:<path>}. Hand-author the file for fancier sprite lists.
 */
public final class ParticlesProvider implements DataProvider {

    private final DataOutput out;
    private final ParticleRegistry registry;

    public ParticlesProvider(DataOutput out, ParticleRegistry registry) {
        this.out = out;
        this.registry = registry;
    }

    @Override
    public void generate() {
        for (ResourceLocation id : registry.ids()) {
            String relPath = id.getNamespace() + "/particles/" + id.getPath() + ".json";
            if (out.isHandAuthored(relPath)) continue;
            JsonObject json = new JsonObject();
            JsonArray textures = new JsonArray();
            textures.add(id.toString());
            json.add("textures", textures);
            out.writeJson(relPath, json);
        }
    }
}

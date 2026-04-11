package wd40.lubricant.api.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.datagen.DataOutput;
import wd40.lubricant.api.datagen.DataProvider;
import wd40.lubricant.api.datagen.providers.support.Names;
import wd40.lubricant.api.registry.SoundRegistry;

/**
 * Writes a {@code sounds.json} entry per sound (subtitle key + ogg path in the
 * mod's namespace) plus a {@code subtitles.<modid>.<path>} lang entry. Skips
 * sounds the consumer hand-authored in their own {@code sounds.json}.
 */
public final class SoundsProvider implements DataProvider {

    private final DataOutput out;
    private final SoundRegistry registry;

    public SoundsProvider(DataOutput out, SoundRegistry registry) {
        this.out = out;
        this.registry = registry;
    }

    @Override
    public void generate() {
        // sounds.json is one file per mod - if hand-authored, skip ALL sound entries (the user's
        // file replaces ours wholesale; this matches existing 'skip-if-exists' semantics).
        boolean handAuthored = out.isHandAuthored(registry.modId() + "/sounds.json");
        for (ResourceLocation id : registry.ids()) {
            String subtitleKey = "subtitles." + id.getNamespace() + "." + id.getPath();
            if (!handAuthored) {
                JsonObject body = new JsonObject();
                body.addProperty("subtitle", subtitleKey);
                JsonArray samples = new JsonArray();
                samples.add(id.toString());
                body.add("sounds", samples);
                out.addSoundEntry(id.getNamespace(), id.getPath(), body);
            }
            out.addLang(id.getNamespace(), subtitleKey, Names.titleCase(id.getPath()));
        }
    }
}

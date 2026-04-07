package wd40.lubricant.api.datagen;

import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.CreativeTabRegistry;
import wd40.lubricant.api.registry.ItemRegistry;
import wd40.lubricant.api.registry.ParticleRegistry;
import wd40.lubricant.api.registry.SoundRegistry;

import java.util.List;

/**
 * Modder-facing facade passed to {@link DatagenInit#onDatagen}. The high-level
 * {@code defaults(...)} methods cover the 90% case (item/generated for items,
 * cube_all for blocks, title-case lang). The low-level methods exist for the
 * rest.
 *
 * <p>Files lubricant would otherwise generate are skipped if a hand-authored
 * file already lives in the mod's {@code src/main/resources/assets/...}.</p>
 */
public interface Assets {

    /** Default item model + lang entry for every id in the registry. */
    void defaults(ItemRegistry items);

    /** Default blockstate, block model, item model (unless no-item), and lang. */
    void defaults(BlockRegistry blocks);

    /**
     * Per sound: an entry in {@code assets/<modid>/sounds.json} pointing at
     * {@code sounds/<path>.ogg} in this mod's namespace, plus a
     * {@code subtitles.<modid>.<path>} lang entry. Aggregated per modid - one
     * sounds.json per mod regardless of how many sounds.
     */
    void defaults(SoundRegistry sounds);

    /**
     * Per particle: a {@code particles/<path>.json} file with one texture
     * {@code <modid>:<path>}. Hand-author the file for fancier sprite lists.
     */
    void defaults(ParticleRegistry particles);

    /**
     * Per tab: a {@code itemGroup.<modid>.<path>} lang entry with the path
     * title-cased. Tabs need no other JSON.
     */
    void defaults(CreativeTabRegistry tabs);

    /** Aggregate sounds.json entry: one sound id, list of sample file references, and an optional subtitle key. */
    void soundEvent(ResourceLocation id, List<String> sampleNames, String subtitleKey);

    /** Particle definition file with explicit texture list. */
    void particle(ResourceLocation id, List<ResourceLocation> textures);

    void itemModel(ResourceLocation id, String parent, ResourceLocation layer0);

    void blockstate(ResourceLocation id, ResourceLocation modelId);

    void blockModel(ResourceLocation id, String parent, ResourceLocation textureAll);

    /** Add a translation key under {@code modId}'s en_us.json. {@code modId} disambiguates output file. */
    void lang(String modId, String key, String value);
}

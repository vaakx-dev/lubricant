package wd40.lubricant.api.data;

import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.ItemRegistry;

/**
 * Modder-facing facade passed to {@link DataInit#onData}. The high-level
 * {@code defaults(...)} methods cover the 90% case (item/generated for items,
 * cube_all for blocks, title-case lang). The low-level methods exist for the
 * rest.
 *
 * <p>Files lubricant would otherwise generate are skipped if a hand-authored
 * file already lives in the mod's {@code src/main/resources/assets/...}.</p>
 */
public interface LubricantData {

    /** Default item model + lang entry for every id in the registry. */
    void defaults(ItemRegistry items);

    /** Default blockstate, block model, item model (unless no-item), and lang. */
    void defaults(BlockRegistry blocks);

    void itemModel(ResourceLocation id, String parent, ResourceLocation layer0);

    void blockstate(ResourceLocation id, ResourceLocation modelId);

    void blockModel(ResourceLocation id, String parent, ResourceLocation textureAll);

    /** Add a translation key under {@code modId}'s en_us.json. {@code modId} disambiguates output file. */
    void lang(String modId, String key, String value);
}

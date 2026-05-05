package wd40.lubricant.api.tags;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;

/**
 * Mints {@link TagKey} handles. Tag membership is purely a datapack concern -
 * declare a {@link TagKey} once, populate it via
 * {@link wd40.lubricant.api.datagen.providers.TagsProvider} (or a hand-authored
 * tag JSON), then check at runtime via {@code stack.is(TAG)} /
 * {@code blockState.is(TAG)} /  {@code entity.getType().is(TAG)}.
 *
 * <pre>{@code
 * public static final TagKey<Item> COGS = Tags.item("mymod", "cogs");
 * }</pre>
 *
 * <p>Multiple mods can mint a {@link TagKey} with the same id and extend the
 * same logical tag - the loader merges all matching JSONs at world load.</p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/Tags">Tags wiki</a></p>
 */
public final class Tags {

    private Tags() {}

    public static TagKey<Item> item(String modId, String path) {
        return TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    public static TagKey<Block> block(String modId, String path) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    public static TagKey<EntityType<?>> entity(String modId, String path) {
        return TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    public static TagKey<Fluid> fluid(String modId, String path) {
        return TagKey.create(Registries.FLUID, ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    public static TagKey<GameEvent> gameEvent(String modId, String path) {
        return TagKey.create(Registries.GAME_EVENT, ResourceLocation.fromNamespaceAndPath(modId, path));
    }

    /** Mint a {@link TagKey} for any registry. */
    public static <T> TagKey<T> of(ResourceKey<? extends Registry<T>> registry, String modId, String path) {
        return TagKey.create(registry, ResourceLocation.fromNamespaceAndPath(modId, path));
    }
}

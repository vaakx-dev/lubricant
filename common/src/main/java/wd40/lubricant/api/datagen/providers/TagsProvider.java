package wd40.lubricant.api.datagen.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
import wd40.lubricant.api.datagen.DataOutput;
import wd40.lubricant.api.datagen.DataProvider;
import wd40.lubricant.api.registry.RegistrySupplier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Emits tag JSON files at {@code data/<tag-namespace>/tags/<dir>/<tag-path>.json}
 * for the directives staged via the {@link Builder}. Hand-authored tag files are
 * skipped.
 *
 * <pre>{@code
 * pack.addProvider(out -> new TagsProvider(out, builder -> {
 *     builder.addItem(Items.COGS, Items.COG, Items.GREASED_COG);
 *     builder.addBlock(Blocks.GEARS, Blocks.GEAR);
 * }));
 * }</pre>
 *
 * <p>Default {@code "replace": false} - emitted JSON extends rather than
 * overwrites any same-id tag from other mods or vanilla. Multiple mods adding
 * to the same tag id all merge at world load.</p>
 */
public final class TagsProvider implements DataProvider {

    private final DataOutput out;
    private final Consumer<Builder> populate;

    public TagsProvider(DataOutput out, Consumer<Builder> populate) {
        this.out = out;
        this.populate = populate;
    }

    @Override
    public void generate() {
        Builder builder = new Builder();
        populate.accept(builder);
        for (Map.Entry<TagKey<?>, List<String>> entry : builder.entries.entrySet()) {
            TagKey<?> tag = entry.getKey();
            String relPath = tag.location().getNamespace()
                    + "/tags/" + dirFor(tag.registry())
                    + "/" + tag.location().getPath() + ".json";
            if (out.isHandAuthoredData(relPath)) continue;

            JsonObject json = new JsonObject();
            json.addProperty("replace", false);
            JsonArray values = new JsonArray();
            for (String value : entry.getValue()) values.add(value);
            json.add("values", values);
            out.writeData(relPath, json);
        }
    }

    private static String dirFor(ResourceKey<? extends Registry<?>> registry) {
        if (registry == Registries.ITEM) return "items";
        if (registry == Registries.BLOCK) return "blocks";
        if (registry == Registries.ENTITY_TYPE) return "entity_types";
        if (registry == Registries.FLUID) return "fluids";
        if (registry == Registries.GAME_EVENT) return "game_events";
        return registry.location().getPath();
    }

    public static final class Builder {

        private final Map<TagKey<?>, List<String>> entries = new HashMap<>();

        @SafeVarargs
        public final Builder addItem(TagKey<Item> tag, RegistrySupplier<? extends Item>... items) {
            return addAll(tag, items);
        }

        @SafeVarargs
        public final Builder addBlock(TagKey<Block> tag, RegistrySupplier<? extends Block>... blocks) {
            return addAll(tag, blocks);
        }

        @SafeVarargs
        public final Builder addEntity(TagKey<EntityType<?>> tag, RegistrySupplier<? extends EntityType<?>>... types) {
            return addAll(tag, types);
        }

        @SafeVarargs
        public final Builder addFluid(TagKey<Fluid> tag, RegistrySupplier<? extends Fluid>... fluids) {
            return addAll(tag, fluids);
        }

        @SafeVarargs
        public final Builder addGameEvent(TagKey<GameEvent> tag, RegistrySupplier<? extends GameEvent>... events) {
            return addAll(tag, events);
        }

        /** Add raw ids - useful for cross-mod / vanilla references and for tag-of-tag includes. */
        public Builder add(TagKey<?> tag, ResourceLocation... ids) {
            List<String> bucket = entries.computeIfAbsent(tag, key -> new ArrayList<>());
            for (ResourceLocation id : ids) bucket.add(id.toString());
            return this;
        }

        /** Include another tag (resolved as {@code "#namespace:path"} in the JSON). */
        public Builder include(TagKey<?> tag, TagKey<?>... included) {
            List<String> bucket = entries.computeIfAbsent(tag, key -> new ArrayList<>());
            for (TagKey<?> ref : included) bucket.add("#" + ref.location());
            return this;
        }

        @SafeVarargs
        private Builder addAll(TagKey<?> tag, RegistrySupplier<?>... suppliers) {
            List<String> bucket = entries.computeIfAbsent(tag, key -> new ArrayList<>());
            for (RegistrySupplier<?> supplier : suppliers) bucket.add(supplier.id().toString());
            return this;
        }
    }
}

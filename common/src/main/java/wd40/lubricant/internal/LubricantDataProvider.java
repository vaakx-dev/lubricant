package wd40.lubricant.internal;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import wd40.lubricant.api.data.DataInit;
import wd40.lubricant.api.data.LubricantData;
import wd40.lubricant.api.registry.BlockRegistry;
import wd40.lubricant.api.registry.ItemRegistry;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

/**
 * Vanilla {@link DataProvider} that drives lubricant datagen.
 *
 * <p>Invoked from {@link LubricantDataMain} via a tiny path-backed
 * {@link CachedOutput} - no loader involvement. Loads every {@link DataInit}
 * service, calls each with a {@link LubricantData} buffer, then flushes JSON.</p>
 */
public final class LubricantDataProvider implements DataProvider {

    private final PackOutput output;

    public LubricantDataProvider(PackOutput output) {
        this.output = output;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path assetsRoot = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK);

        Buffer buf = new Buffer();
        for (DataInit init : ServiceLoader.load(DataInit.class)) {
            init.onData(buf);
        }

        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (Buffer.Entry e : buf.entries) {
            writes.add(DataProvider.saveStable(cache, e.json, assetsRoot.resolve(e.relPath)));
        }
        for (var lang : buf.langByModId.entrySet()) {
            JsonObject merged = new JsonObject();
            for (var kv : lang.getValue().entrySet()) merged.addProperty(kv.getKey(), kv.getValue());
            Path p = assetsRoot.resolve(lang.getKey() + "/lang/en_us.json");
            writes.add(DataProvider.saveStable(cache, merged, p));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Lubricant default assets";
    }

    /** Collects everything DataInit emits, then flushes once. */
    private static final class Buffer implements LubricantData {

        record Entry(String relPath, JsonElement json) {}

        final List<Entry> entries = new ArrayList<>();
        // Per-modId so two mods sharing one datagen pass don't trample each other's lang.
        final TreeMap<String, TreeMap<String, String>> langByModId = new TreeMap<>();

        @Override
        public void defaults(ItemRegistry registry) {
            for (ResourceLocation id : registry.ids()) {
                itemModel(id, "item/generated", id.withPrefix("item/"));
                lang("item." + id.getNamespace() + "." + id.getPath(), titleCase(id.getPath()));
            }
        }

        @Override
        public void defaults(BlockRegistry registry) {
            var noItem = registry.noItemIds();
            for (ResourceLocation id : registry.ids()) {
                ResourceLocation modelId = id.withPrefix("block/");
                blockstate(id, modelId);
                blockModel(id, "block/cube_all", id.withPrefix("block/"));
                if (!noItem.contains(id)) {
                    JsonObject m = new JsonObject();
                    m.addProperty("parent", modelId.toString());
                    entries.add(new Entry(
                            id.getNamespace() + "/models/item/" + id.getPath() + ".json", m));
                }
                lang("block." + id.getNamespace() + "." + id.getPath(), titleCase(id.getPath()));
            }
        }

        @Override
        public void itemModel(ResourceLocation id, String parent, ResourceLocation layer0) {
            JsonObject json = new JsonObject();
            json.addProperty("parent", parent);
            JsonObject tex = new JsonObject();
            tex.addProperty("layer0", layer0.toString());
            json.add("textures", tex);
            entries.add(new Entry(
                    id.getNamespace() + "/models/item/" + id.getPath() + ".json", json));
        }

        @Override
        public void blockstate(ResourceLocation id, ResourceLocation modelId) {
            JsonObject variant = new JsonObject();
            variant.addProperty("model", modelId.toString());
            JsonObject variants = new JsonObject();
            variants.add("", variant);
            JsonObject json = new JsonObject();
            json.add("variants", variants);
            entries.add(new Entry(
                    id.getNamespace() + "/blockstates/" + id.getPath() + ".json", json));
        }

        @Override
        public void blockModel(ResourceLocation id, String parent, ResourceLocation textureAll) {
            JsonObject json = new JsonObject();
            json.addProperty("parent", parent);
            JsonObject tex = new JsonObject();
            tex.addProperty("all", textureAll.toString());
            json.add("textures", tex);
            entries.add(new Entry(
                    id.getNamespace() + "/models/block/" + id.getPath() + ".json", json));
        }

        @Override
        public void lang(String key, String value) {
            String modId = key.substring(key.indexOf('.') + 1, key.indexOf('.', key.indexOf('.') + 1));
            langByModId.computeIfAbsent(modId, k -> new TreeMap<>()).put(key, value);
        }

        private static String titleCase(String path) {
            StringBuilder sb = new StringBuilder();
            for (String part : path.split("_")) {
                if (part.isEmpty()) continue;
                sb.append(Character.toUpperCase(part.charAt(0))).append(part.substring(1)).append(' ');
            }
            return sb.toString().trim();
        }
    }

}

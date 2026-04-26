package wd40.lubricant.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import wd40.lubricant.api.datagen.DataGenerator;
import wd40.lubricant.api.datagen.DataOutput;
import wd40.lubricant.api.datagen.DataProvider;
import wd40.lubricant.api.init.DatagenInit;
import wd40.lubricant.api.datagen.Pack;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Vanilla {@link net.minecraft.data.DataProvider} that drives lubricant datagen.
 *
 * <p>Invoked from {@link DatagenMain} via a tiny path-backed {@link CachedOutput}.
 * Loads every {@link DatagenInit} service, gives each a {@link DataGenerator},
 * runs every attached {@link DataProvider} against a shared sink, then flushes
 * accumulated JSON / lang / sound entries to disk.</p>
 */
public final class Provider implements net.minecraft.data.DataProvider {

    private final PackOutput output;
    private final Path handAuthoredRoot;  // null = no skip-if-exists check

    public Provider(PackOutput output) {
        this(output, null);
    }

    public Provider(PackOutput output, Path handAuthoredRoot) {
        this.output = output;
        this.handAuthoredRoot = handAuthoredRoot;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput cache) {
        Path assetsRoot = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK);
        Sink sink = new Sink(handAuthoredRoot);
        Generator gen = new Generator(sink);

        for (DatagenInit init : ServiceLoader.load(DatagenInit.class)) {
            init.onDatagen(gen);
        }
        gen.runAll();

        List<CompletableFuture<?>> writes = new ArrayList<>();
        for (Sink.Entry e : sink.entries) {
            writes.add(net.minecraft.data.DataProvider.saveStable(cache, e.json, assetsRoot.resolve(e.relPath)));
        }
        for (var sounds : sink.soundsByModId.entrySet()) {
            String relPath = sounds.getKey() + "/sounds.json";
            JsonObject merged = new JsonObject();
            for (var entry : sounds.getValue().entrySet()) merged.add(entry.getKey(), entry.getValue());
            writes.add(net.minecraft.data.DataProvider.saveStable(cache, merged, assetsRoot.resolve(relPath)));
        }
        for (var lang : sink.langByModId.entrySet()) {
            String relPath = lang.getKey() + "/lang/en_us.json";
            JsonObject merged = new JsonObject();
            for (var kv : lang.getValue().entrySet()) merged.addProperty(kv.getKey(), kv.getValue());
            writes.add(net.minecraft.data.DataProvider.saveStable(cache, merged, assetsRoot.resolve(relPath)));
        }
        return CompletableFuture.allOf(writes.toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Lubricant default assets";
    }

    /** Constructs Pack instances and runs every provider attached to each. */
    private static final class Generator implements DataGenerator {
        private final Sink sink;
        private final List<PackImpl> packs = new ArrayList<>();

        Generator(Sink sink) { this.sink = sink; }

        @Override
        public Pack createPack() {
            PackImpl pack = new PackImpl(sink);
            packs.add(pack);
            return pack;
        }

        void runAll() {
            for (PackImpl pack : packs) pack.runAll();
        }
    }

    private static final class PackImpl implements Pack {
        private final Sink sink;
        private final List<DataProvider> providers = new ArrayList<>();

        PackImpl(Sink sink) { this.sink = sink; }

        @Override
        public void addProvider(Function<DataOutput, ? extends DataProvider> factory) {
            providers.add(factory.apply(sink));
        }

        void runAll() {
            for (DataProvider provider : providers) provider.generate();
        }
    }

    /** Aggregating sink. Buffers everything providers emit, written by run() at the end. */
    private static final class Sink implements DataOutput {

        record Entry(String relPath, JsonElement json) {}

        final List<Entry> entries = new ArrayList<>();
        final TreeMap<String, TreeMap<String, String>> langByModId = new TreeMap<>();
        final TreeMap<String, TreeMap<String, JsonObject>> soundsByModId = new TreeMap<>();

        private final Path handAuthoredRoot;

        Sink(Path handAuthoredRoot) {
            this.handAuthoredRoot = handAuthoredRoot;
        }

        @Override
        public void writeJson(String relPath, JsonElement json) {
            if (isHandAuthored(relPath)) return;
            entries.add(new Entry(relPath, json));
        }

        @Override
        public void addLang(String modId, String key, String value) {
            langByModId.computeIfAbsent(modId, k -> new TreeMap<>()).put(key, value);
        }

        @Override
        public void addSoundEntry(String modId, String soundPath, JsonObject body) {
            soundsByModId.computeIfAbsent(modId, k -> new TreeMap<>()).put(soundPath, body);
        }

        @Override
        public boolean isHandAuthored(String relPath) {
            if (handAuthoredRoot == null) return false;
            return Files.exists(handAuthoredRoot.resolve("assets").resolve(relPath));
        }
    }
}

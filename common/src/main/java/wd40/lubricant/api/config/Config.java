package wd40.lubricant.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import wd40.lubricant.internal.Datagen;
import wd40.lubricant.internal.Services;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * GSON-backed config file living at
 * {@code <gameDir>/config/<fileName>.json}. The {@code T} POJO defines the
 * shape - public fields, no annotations needed; default GSON conventions
 * apply.
 *
 * <pre>{@code
 * public final class CogConfig {
 *     public boolean logCogClicks = true;
 *     public int counterMultiplier = 1;
 * }
 *
 * public static final Config<CogConfig> CONFIG =
 *     Config.load(Cog.ID, CogConfig.class, CogConfig::new);
 *
 * if (CONFIG.get().logCogClicks) { ... }
 * CONFIG.get().counterMultiplier = 2;
 * CONFIG.save();
 * }</pre>
 *
 * <p>{@link #load} reads the file if it exists and writes the defaults if it
 * doesn't, so first boot always produces a populated file the modder (or the
 * end user) can edit. Mutate the value object then call {@link #save} to
 * persist; or call {@link #reload} to discard in-memory edits and re-read from
 * disk.</p>
 *
 * <p>During datagen no file is touched - the held value is the supplied
 * default and {@code save}/{@code reload} are no-ops.</p>
 *
 * <p><a href="https://github.com/vaakx-dev/lubricant/wiki/Config">Config wiki</a></p>
 *
 * @param <T> the POJO type holding the config fields
 */
public final class Config<T> {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Class<T> type;
    private final Supplier<T> defaults;
    private final Path file;       // null in datagen mode
    private T value;

    private Config(Class<T> type, Supplier<T> defaults, Path file) {
        this.type = type;
        this.defaults = defaults;
        this.file = file;
        this.value = defaults.get();
    }

    /** Load (or create) {@code <configDir>/<modId>.json}. */
    public static <T> Config<T> load(String modId, Class<T> type, Supplier<T> defaults) {
        return load(modId, modId, type, defaults);
    }

    /** Load (or create) {@code <configDir>/<fileName>.json}. */
    public static <T> Config<T> load(String modId, String fileName, Class<T> type, Supplier<T> defaults) {
        if (Datagen.IS_DATAGEN) return new Config<>(type, defaults, null);
        Path file = Services.config().configDir().resolve(fileName + ".json");
        Config<T> config = new Config<>(type, defaults, file);
        config.reload();
        return config;
    }

    /** The current in-memory value. Mutate it, then call {@link #save}. */
    public T get() { return value; }

    /** Replace the in-memory value (does not persist - call {@link #save} after). */
    public void set(T value) { this.value = value; }

    /** Write the in-memory value to disk. */
    public void save() {
        if (file == null) return;
        try {
            Files.createDirectories(file.getParent());
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(value, type, writer);
            }
        } catch (IOException error) {
            throw new UncheckedIOException("failed to write config " + file, error);
        }
    }

    /** Re-read the file. Creates it from defaults if absent. */
    public void reload() {
        if (file == null) return;
        if (!Files.exists(file)) {
            value = defaults.get();
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(file)) {
            T parsed = GSON.fromJson(reader, type);
            value = parsed != null ? parsed : defaults.get();
        } catch (IOException error) {
            throw new UncheckedIOException("failed to read config " + file, error);
        }
    }
}

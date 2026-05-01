package wd40.lubricant.api.datagen;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Shared sink that {@link DataProvider}s write through. Lubricant flushes the
 * accumulated entries to disk after every provider has run.
 *
 * <p>Files lubricant would otherwise generate are skipped if a hand-authored
 * file already lives in the consumer's {@code src/main/resources/assets/...} -
 * call {@link #isHandAuthored} from a provider to decide whether to skip
 * upstream of the write call.</p>
 */
public interface DataOutput {

    /**
     * Write a JSON file at {@code <assets-root>/<relPath>}. If the same
     * {@code relPath} is hand-authored, the write is skipped.
     */
    void writeJson(String relPath, JsonElement json);

    /**
     * Write a JSON file at {@code <data-root>/<relPath>} (i.e. under
     * {@code data/} rather than {@code assets/}). For tag JSONs, recipes,
     * loot tables, advancements, and similar datapack-side files. Skipped
     * if {@link #isHandAuthoredData} reports the file is already present.
     */
    void writeData(String relPath, JsonElement json);

    /**
     * Add a translation key under {@code <modId>}'s {@code en_us.json}.
     * Aggregated per modid - one lang file per mod.
     */
    void addLang(String modId, String key, String value);

    /**
     * Add an entry to {@code <modId>}'s {@code sounds.json}. Aggregated per
     * modid - one sounds.json per mod regardless of how many sounds.
     */
    void addSoundEntry(String modId, String soundPath, JsonObject body);

    /** True if {@code <handAuthoredRoot>/assets/<relPath>} exists. */
    boolean isHandAuthored(String relPath);

    /** True if {@code <handAuthoredRoot>/data/<relPath>} exists. */
    boolean isHandAuthoredData(String relPath);
}

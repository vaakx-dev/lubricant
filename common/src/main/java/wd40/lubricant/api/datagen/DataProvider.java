package wd40.lubricant.api.datagen;

/**
 * One contributor to the data pack. Implement this for custom providers (e.g.
 * recipes, tags, advancements). Built-in providers like
 * {@link wd40.lubricant.api.datagen.providers.ItemModelProvider} ship with
 * lubricant for the common cases.
 */
public interface DataProvider {

    /** Called once. Use the {@link DataOutput} given at construction to emit JSON / lang. */
    void generate();
}

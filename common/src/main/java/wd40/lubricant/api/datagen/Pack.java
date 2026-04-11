package wd40.lubricant.api.datagen;

import java.util.function.Function;

/**
 * Bucket of {@link DataProvider}s. Add as many as needed. They run in order
 * once {@code DatagenInit.onDatagen} returns.
 */
public interface Pack {

    /**
     * Add a provider. The factory receives the shared {@link DataOutput} so
     * the provider can write JSON entries, lang keys, and aggregated sound /
     * particle data.
     */
    void addProvider(Function<DataOutput, ? extends DataProvider> factory);
}

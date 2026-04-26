package wd40.lubricant.api.init;

import wd40.lubricant.api.datagen.DataGenerator;

/**
 * Mod-side entry point for lubricant's datagen. Each implementor is loaded via
 * JDK ServiceLoader at datagen time (not normal mod boot) and called once with
 * a {@link DataGenerator}.
 *
 * <p>Discovery: list the FQN under
 * {@code META-INF/services/wd40.lubricant.api.init.DatagenInit}.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Datagen">Datagen wiki</a></p>
 */
public interface DatagenInit {
    void onDatagen(DataGenerator gen);
}

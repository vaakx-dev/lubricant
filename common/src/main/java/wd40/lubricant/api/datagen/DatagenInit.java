package wd40.lubricant.api.datagen;

/**
 * Mod-side entry point for lubricant's datagen. Each implementor is loaded via
 * JDK ServiceLoader at datagen time (not normal mod boot) and called once with
 * a {@link Datagen} facade.
 *
 * <p>Discovery: list the FQN under
 * {@code META-INF/services/wd40.lubricant.api.datagen.DatagenInit}.</p>
 *
 * <p><a href="https://github.com/vaakxxx/lubricant/wiki/Datagen">Datagen wiki</a></p>
 */
public interface DatagenInit {
    void onDatagen(Datagen datagen);
}

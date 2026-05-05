package wd40.lubricant.internal.config;

import java.nio.file.Path;

/**
 * Loader-specific source for the game's config directory backing the public
 * {@link wd40.lubricant.api.config.Config} facade. One implementation per loader,
 * discovered via JDK {@link java.util.ServiceLoader}:
 *
 * <ul>
 *   <li>Fabric: {@code wd40.lubricant.fabric.config.Config}</li>
 *   <li>NeoForge: {@code wd40.lubricant.neoforge.config.Config}</li>
 * </ul>
 *
 * <p>Both implementations resolve to {@code <gameDir>/config/} - lubricant
 * just routes through the loader-native path API rather than guessing from
 * {@code user.dir}.</p>
 */
public interface ConfigHelper {

    /** Absolute path of the game's config directory. Always exists by the time mods load. */
    Path configDir();
}

package wd40.lubricant.neoforge.config;

import net.neoforged.fml.loading.FMLPaths;
import wd40.lubricant.internal.config.ConfigHelper;

import java.nio.file.Path;

public final class Config implements ConfigHelper {

    @Override
    public Path configDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}

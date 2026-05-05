package wd40.lubricant.fabric.config;

import net.fabricmc.loader.api.FabricLoader;
import wd40.lubricant.internal.config.ConfigHelper;

import java.nio.file.Path;

public final class Config implements ConfigHelper {

    @Override
    public Path configDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}

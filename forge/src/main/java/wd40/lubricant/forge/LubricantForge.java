package wd40.lubricant.forge;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wd40.lubricant.api.Init;
import wd40.lubricant.internal.Bootstrap;

import java.util.ServiceLoader;

@Mod("lubricant")
public final class LubricantForge {

    private static final Logger LOG = LoggerFactory.getLogger("lubricant");

    public LubricantForge() {
        LOG.info("[lubricant] @Mod constructor starting on Forge");

        int seen = 0;
        for (Init init : ServiceLoader.load(Init.class)) {
            LOG.info("[lubricant] ServiceLoader found Init impl: {}", init.getClass().getName());
            seen++;
        }
        LOG.info("[lubricant] ServiceLoader saw {} Init impl(s) (default cl)", seen);
        if (seen == 0) {
            ClassLoader ctx = Thread.currentThread().getContextClassLoader();
            LOG.info("[lubricant] retrying with thread context classloader: {}", ctx);
            for (Init init : ServiceLoader.load(Init.class, ctx)) {
                LOG.info("[lubricant] ServiceLoader (ctx) found Init impl: {}", init.getClass().getName());
                seen++;
            }
        }

        Bootstrap.loadAllInit();

        LOG.info("[lubricant] ForgeRegistryHelper.ALL_ITEMS size: {}", ForgeRegistryHelper.ALL_ITEMS.size());

        for (ForgeItemRegistry r : ForgeRegistryHelper.ALL_ITEMS) {
            LOG.info("[lubricant] resolving event bus for modId={}", r.modId);
            ModContainer container = ModList.get().getModContainerById(r.modId)
                    .orElseThrow(() -> new IllegalStateException(
                            "lubricant: no Forge mod container for modId=" + r.modId));
            LOG.info("[lubricant]   container class: {}", container.getClass().getName());
            if (!(container instanceof FMLModContainer fml)) {
                throw new IllegalStateException(
                        "lubricant: " + r.modId + " is not a Java mod (got " + container.getClass().getName() + ")");
            }
            r.attach(fml.getEventBus());
            LOG.info("[lubricant]   attached for modId={}", r.modId);
        }

        LOG.info("[lubricant] @Mod constructor complete");
    }
}

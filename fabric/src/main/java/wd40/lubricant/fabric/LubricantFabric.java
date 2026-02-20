package wd40.lubricant.fabric;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wd40.lubricant.api.Init;
import wd40.lubricant.internal.Bootstrap;

import java.util.ServiceLoader;

public final class LubricantFabric implements ModInitializer {

    private static final Logger LOG = LoggerFactory.getLogger("lubricant");

    @Override
    public void onInitialize() {
        LOG.info("[lubricant] onInitialize starting on Fabric");

        // Diagnostic: what does ServiceLoader actually see?
        int seen = 0;
        for (Init init : ServiceLoader.load(Init.class)) {
            LOG.info("[lubricant] ServiceLoader found Init impl: {}", init.getClass().getName());
            seen++;
        }
        LOG.info("[lubricant] ServiceLoader saw {} Init impl(s)", seen);

        if (seen == 0) {
            // ServiceLoader didn't find anything via the default classloader. Try the
            // thread context classloader - in some loader dev environments this is
            // the only one that sees mod jars.
            ClassLoader ctx = Thread.currentThread().getContextClassLoader();
            LOG.info("[lubricant] retrying with thread context classloader: {}", ctx);
            for (Init init : ServiceLoader.load(Init.class, ctx)) {
                LOG.info("[lubricant] ServiceLoader (ctx) found Init impl: {}", init.getClass().getName());
                seen++;
            }
            LOG.info("[lubricant] ServiceLoader (ctx) saw {} Init impl(s)", seen);
        }

        Bootstrap.loadAllInit();

        LOG.info("[lubricant] FabricRegistryHelper.ALL_ITEMS size: {}", FabricRegistryHelper.ALL_ITEMS.size());

        for (FabricItemRegistry r : FabricRegistryHelper.ALL_ITEMS) {
            LOG.info("[lubricant] binding registry for modId={}", r.modId());
            r.bind();
        }

        LOG.info("[lubricant] onInitialize complete");
    }
}

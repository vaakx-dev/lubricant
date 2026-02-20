package wd40.lubricant.fabric;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wd40.lubricant.internal.Bootstrap;

public final class LubricantFabric implements ModInitializer {

    private static final Logger LOG = LoggerFactory.getLogger("lubricant");

    @Override
    public void onInitialize() {
        Bootstrap.loadAllInit();

        // Bind blocks BEFORE items - item factories may reference Blocks.X.get() to
        // construct BlockItems, so blocks need to exist first.
        for (FabricBlockRegistry r : FabricRegistryHelper.ALL_BLOCKS) {
            r.bind();
        }
        for (FabricItemRegistry r : FabricRegistryHelper.ALL_ITEMS) {
            r.bind();
        }

        LOG.info("[lubricant] init complete on Fabric ({} block reg(s), {} item reg(s))",
                FabricRegistryHelper.ALL_BLOCKS.size(),
                FabricRegistryHelper.ALL_ITEMS.size());
    }
}

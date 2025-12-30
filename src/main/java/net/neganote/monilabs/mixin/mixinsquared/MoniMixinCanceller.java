package net.neganote.monilabs.mixin.mixinsquared;

import com.bawnorton.mixinsquared.api.MixinCanceller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;

public class MoniMixinCanceller implements MixinCanceller {

    private static final String AAE_CRAFTING_SERVICE_MIXIN = "net.pedroksl.advanced_ae.mixins.cpu.MixinCraftingService";
    private static final Logger LOGGER = LogManager.getLogger("MoniLabs-MixinSquared");
    private static final boolean VERBOSE = Boolean.parseBoolean(System.getProperty("monilabs.mixin.verbose", "false"));

    @Override
    public boolean shouldCancel(List<String> targetClassNames, String mixinClassName) {
        // We cancel whole AAE mixin so we can implement our own that works with ae2cl
        if (Objects.equals(mixinClassName, AAE_CRAFTING_SERVICE_MIXIN)) {
            if (VERBOSE) {
                LOGGER.info("Skipping: {}", mixinClassName);
            }
            return true;
        }
        return false;
    }
}

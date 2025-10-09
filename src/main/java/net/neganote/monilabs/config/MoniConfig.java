package net.neganote.monilabs.config;

import net.neganote.monilabs.MoniLabs;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;
import net.neganote.monilabs.MoniLabs;

@Config(id = MoniLabs.MOD_ID)
public class MoniConfig {

    public static MoniConfig INSTANCE;

    public static ConfigHolder<MoniConfig> CONFIG_HOLDER;

    public static void init() {
        CONFIG_HOLDER = Configuration.registerConfig(MoniConfig.class, ConfigFormats.yaml());
        INSTANCE = CONFIG_HOLDER.getConfigInstance();
    }

    @Configurable
    public ValueConfigs values = new ValueConfigs();

    public static class ValueConfigs {

        @Configurable
        @Configurable.Comment({ "The exponent used each time the Omnic Synthesizer performs an operation." })
        public double omnicSynthesizerExponent = 1.557;

        @Configurable
        @Configurable.Comment({ "Minimum multiplier to antimatter generation (inclusive)." })
        public float antimatterRandomMinInclusive = 0.5f;
        @Configurable
        @Configurable.Comment({ "Maximum multiplier to antimatter generation (exclusive)." })
        public float antimatterRandomMaxExclusive = 2.0f;

        @Configurable
        @Configurable.Comment({ "Whether the microminer is returned upon a microverse reaching zero integrity." })
        public boolean microminerReturnedOnZeroIntegrity = true;

        @Configurable
        @Configurable.Comment({"Force pride month easter eggs regardless of time of year"})
        public boolean forcePrideMonth = false;

        @Configurable
        @Configurable.Comment({
                "The efficiency multiplier for the sculk vat. Would make it this many times less efficient at empty/full compared to half." })
        public double sculkVatEfficiencyMultiplier = 33.0;

        @Configurable
        @Configurable.Comment({ "Fluid ID for Liquid Experience for the Sculk Vat." })
        public String sculkVatExperienceFluidID = "enderio:xp_juice";
    }
}

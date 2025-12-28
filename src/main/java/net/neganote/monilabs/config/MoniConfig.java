package net.neganote.monilabs.config;

import net.neganote.monilabs.MoniLabs;

import dev.toma.configuration.Configuration;
import dev.toma.configuration.config.Config;
import dev.toma.configuration.config.ConfigHolder;
import dev.toma.configuration.config.Configurable;
import dev.toma.configuration.config.format.ConfigFormats;

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
        @Configurable.Comment({ "Whether the microminer is returned upon a microverse reaching zero integrity." })
        public boolean microminerReturnedOnZeroIntegrity = true;

        @Configurable
        @Configurable.Comment({ "Force pride month easter eggs regardless of time of year" })
        public boolean forcePrideMonth = false;

        @Configurable
        @Configurable.Comment({
                "The efficiency multiplier for the Sculk Vat. Would make it this many times less efficient at empty/full compared to half." })
        public double sculkVatEfficiencyMultiplier = 33.0;

        @Configurable
        @Configurable.Comment({ "Fluid ID for Liquid XP for the Sculk Vat." })
        public String sculkVatExperienceFluidID = "enderio:xp_juice";

        @Configurable
        @Configurable.Comment({
                "Whether to include the hostile microverse tooltip for the microverse type sensor hatch." })
        public boolean hostileMicroverseTooltip = false;
    }
}

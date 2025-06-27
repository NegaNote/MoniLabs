package net.neganote.monilabs.config;

import com.gregtechceu.gtceu.api.GTValues;

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
        @Configurable.Comment({ "Minimum multiplier to antimatter generation (inclusive)." })
        public float antimatterRandomMinInclusive = 0.5f;
        @Configurable
        @Configurable.Comment({ "Maximum multiplier to antimatter generation (exclusive)." })
        public float antimatterRandomMaxExclusive = 2.0f;
        @Configurable
        @Configurable.Comment({ "Base amount of EU to generate per millibucket of antimatter." })
        public long euPerAntimatterMillibucket = GTValues.V[GTValues.UEV];
        @Configurable
        @Configurable.Comment({ "Fluid ID for the antimatter fuel." })
        public String antimatterFuelID = "minecraft:water";
        @Configurable
        @Configurable.Comment({ "Fluid ID for the matter to annihilate with the antimatter fuel." })
        public String annihilatableMatterID = "minecraft:lava";
        @Configurable
        @Configurable.Comment({ "Fluid ID for another matter to annihilate, but with bonuses to generation." })
        public String annihilatableMatterBonusID = "gtceu:nitrogen";
        @Configurable
        @Configurable.Comment({ "Bonus multiplier for the second tier of annihilant." })
        public double antimatterSecondTierBonusMultiplier = 4.0;
        @Configurable
        @Configurable.Comment({ "" })
        public boolean microminerReturnedOnZeroIntegrity = true;
    }
}

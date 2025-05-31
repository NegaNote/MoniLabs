package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;

import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;

import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

@SuppressWarnings("unused")
public class MoniRecipeTypes {

    public static GTRecipeType createPrismaCRecipeType(String name) {
        return GTRecipeTypes
                .register(name, GTRecipeTypes.MULTIBLOCK)
                .setMaxSize(IO.BOTH, MoniRecipeCapabilities.CHROMA, 1)
                .addDataInfo(data -> " ")
                .addDataInfo(data -> " ")
                .addDataInfo(data -> {
                    if (data.contains("output_states")) {
                        boolean isRelative = (data.contains("color_change_relative") &&
                                data.getBoolean("color_change_relative"));
                        int outputStatesCount = data.getInt("output_states");

                        if (isRelative) {
                            if (outputStatesCount == 1) {
                                int modulus = data.getInt("output_states_0");
                                return LocalizationUtils.format("monilabs.recipe.result_color_relative",
                                        String.valueOf(modulus));
                            }
                            if (outputStatesCount == Color.COLOR_COUNT) {
                                return LocalizationUtils.format("monilabs.recipe.fully_random_color");
                            }
                            StringBuilder builder = new StringBuilder(
                                    LocalizationUtils.format("monilabs.recipe.color_list_random_start_relative"));
                            for (int i = 0; i < outputStatesCount; i++) {
                                builder.append(LocalizationUtils
                                        .format(Color.getColorFromKey(data.getInt("output_states_" + i)).nameKey));
                                if (i % 3 == 2) {
                                    builder.append("\n");
                                } else {
                                    builder.append(LocalizationUtils.format("monilabs.recipe.color_list_separator"));
                                }

                            }
                            return builder.toString();

                        } else {
                            if (outputStatesCount == 1) {
                                int modulus = data.getInt("output_states_0");
                                return LocalizationUtils.format("monilabs.recipe.result_color",
                                        LocalizationUtils.format(Color.getColorFromKey(modulus).nameKey));
                            }
                            if (outputStatesCount == Color.COLOR_COUNT) {
                                return LocalizationUtils.format("monilabs.recipe.fully_random_color");
                            }
                            StringBuilder builder = new StringBuilder(
                                    LocalizationUtils.format("monilabs.recipe.color_list_random_start"));
                            for (int i = 0; i < outputStatesCount; i++) {
                                builder.append(LocalizationUtils
                                        .format(Color.getColorFromKey(data.getInt("output_states_" + i)).nameKey));
                                if (i % 3 == 2) {
                                    builder.append("\n");
                                } else {
                                    builder.append(LocalizationUtils.format("monilabs.recipe.color_list_separator"));
                                }

                            }
                            return builder.toString();
                        }
                    }
                    // Default behavior
                    return LocalizationUtils.format("monilabs.recipe.fully_random_color");
                })
                .addDataInfo(data -> " ")
                .addDataInfo(data -> " ")
                .addDataInfo(data -> " ")
                .addDataInfo(data -> " ")
                .setMaxIOSize(3, 3, 1, 1)
                .setEUIO(IO.IN)
                .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT);
    }

    public static GTRecipeType OMNIC_SYNTHESIZER_RECIPES = GTRecipeTypes
            .register("omnic_synthesis", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(1, 1, 0, 0)
            .setEUIO(IO.IN)
            .addDataInfo(data -> LocalizationUtils.format("monilabs.recipe.diversity_info"))
            .addDataInfo(data -> " ")
            .addDataInfo(data -> " ")
            .addDataInfo(data -> " ")
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static GTRecipeType CREATIVE_ENERGY_RECIPES = GTRecipeTypes
            .register("omnidimensional_power_singularity", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 1, 0)
            .addDataInfo(data -> LocalizationUtils.format("monilabs.recipe.infinite_power"))
            .addDataInfo(data -> " ")
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.ALWAYS_FULL);

    public static GTRecipeType CREATIVE_DATA_RECIPES = GTRecipeTypes
            .register("omniscience_research_beacon", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(0, 0, 1, 0)
            .addDataInfo(data -> " ")
            .addDataInfo(data -> LocalizationUtils.format("monilabs.recipe.infinite_research"))
            .addDataInfo(data -> " ")
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.ALWAYS_FULL);

    public static void init() {}
}

package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.ColorChangeMode;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Arrays.copyOfRange;

public class MoniRecipeTypes {
    public static final GTRecipeType PRISMATIC_CRUCIBLE_RECIPES = GTRecipeTypes
            .register("prismatic_crucible", GTRecipeTypes.MULTIBLOCK)
            .addDataInfo(data -> {
                if (data.contains("required_color")) {
                    int modulus = data.getInt("required_color");
                    return LocalizationUtils.format("monilabs.recipe.required_color", LocalizationUtils.format(Color.getColorFromKey(modulus).nameKey));
                }
                if (data.contains("required_colors")) {
                    int[] possibleOriginalColors = data.getIntArray("required_colors");
                    if (possibleOriginalColors.length < 2) {
                        return "You made a mistake defining this recipe's input colors.";
                    }

                    StringBuilder builder = new StringBuilder(LocalizationUtils.format("monilabs.recipe.required_colors_start"));
                    int[] truncated = copyOfRange(possibleOriginalColors, 0, possibleOriginalColors.length);
                    builder.append(Arrays.stream(truncated).mapToObj(i -> String.valueOf(possibleOriginalColors[i]))
                            .collect(Collectors.joining(LocalizationUtils.format("monilabs.recipe.color_list_separator"))));

                    return builder.toString();
                }
                return "You made a mistake defining this recipe's input colors.";

                // return LocalizationUtils.format("monilabs.recipe.required_color", LocalizationUtils.format(Color.getColorFromKey(data.getInt("required_color")).nameKey));
            })
            .addDataInfo(data -> {
                int modeSwitchType = data.getInt("mode_switch_type");
                ColorChangeMode mode = ColorChangeMode.getModeFromKey(modeSwitchType);
                if (data.contains("color_change_relative") && data.getBoolean("color_change_relative")) {
                    return switch (mode) {
                        case DETERMINISTIC -> {
                            int modulus = data.getInt("result_color");
                            yield LocalizationUtils.format("monilabs.recipe.result_color_relative", String.valueOf(modulus));
                        }
                        case RANDOM_WITH_LIST -> {
                            int[] possibleNewColors = data.getIntArray("possible_new_colors");
                            if (possibleNewColors.length == 0) {
                                yield "You made a mistake defining this recipe's output colors.";
                            } else if (possibleNewColors.length == 1) {
                                yield LocalizationUtils.format("monilabs.recipe.result_color_relative", String.valueOf(possibleNewColors[0]));
                            } else {
                                StringBuilder builder = new StringBuilder(LocalizationUtils.format("monilabs.recipe.color_list_random_start_relative"));
                                int[] truncated = copyOfRange(possibleNewColors, 0, possibleNewColors.length);
                                builder.append(Arrays.stream(truncated).mapToObj(i -> String.valueOf(possibleNewColors[i]))
                                        .collect(Collectors.joining(LocalizationUtils.format("monilabs.recipe.color_list_separator"))));

                                yield builder.toString();
                            }
                        }
                        case FULL_RANDOM -> LocalizationUtils.format("monilabs.recipe.fully_random_color");
                    };
                } else {
                    return switch (mode) {
                        case DETERMINISTIC -> {
                            int modulus = data.getInt("result_color");
                            yield LocalizationUtils.format("monilabs.recipe.result_color", LocalizationUtils.format(Color.getColorFromKey(modulus).nameKey));
                        }
                        case RANDOM_WITH_LIST -> {
                            int[] possibleNewColors = data.getIntArray("possible_new_colors");
                            if (possibleNewColors.length == 0) {
                                yield "You made a mistake defining this recipe's output colors.";
                            } else if (possibleNewColors.length == 1) {
                                yield LocalizationUtils.format("monilabs.recipe.result_color", LocalizationUtils.format(Color.getColorFromKey(possibleNewColors[0]).nameKey));
                            } else {
                                StringBuilder builder = new StringBuilder(LocalizationUtils.format("monilabs.recipe.color_list_random_start"));
                                int[] truncated = copyOfRange(possibleNewColors, 0, possibleNewColors.length);
                                builder.append(Arrays.stream(truncated).mapToObj(i -> LocalizationUtils.format(Color.getColorFromKey(possibleNewColors[i]).nameKey))
                                        .collect(Collectors.joining(LocalizationUtils.format("monilabs.recipe.color_list_separator"))));

                                yield builder.toString();
                            }
                        }
                        case FULL_RANDOM -> LocalizationUtils.format("monilabs.recipe.fully_random_color");
                    };
                }
            })
            .setMaxIOSize(3, 1, 1, 0)
            .setEUIO(IO.IN)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static void init() {
    }
}

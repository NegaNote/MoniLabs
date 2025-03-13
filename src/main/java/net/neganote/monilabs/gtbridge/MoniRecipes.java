package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.GTValues;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.nbt.CompoundTag;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.ColorChangeMode;

import java.util.function.Consumer;

import static net.minecraft.world.item.Items.*;
import static net.neganote.monilabs.gtbridge.MoniRecipeTypes.PRISMATIC_CRUCIBLE_RECIPES;

public class MoniRecipes {
    @SuppressWarnings("unused")
    public static void init(Consumer<FinishedRecipe> provider) {
        // Used to test prismatic crucible functionality
        PRISMATIC_CRUCIBLE_RECIPES.recipeBuilder("prismatic_crucible_test_recipe_deterministic")
                .addData("required_color", Color.RED.key)
                .addData("result_color", Color.GREEN.key)
                .addData("mode_switch_type", ColorChangeMode.DETERMINISTIC.key)
                .inputItems(RED_DYE, 32)
                .outputItems(GREEN_DYE, 32)
                .EUt(GTValues.VEX[GTValues.EV])
                .duration(200)
                .save(provider);

        PRISMATIC_CRUCIBLE_RECIPES.recipeBuilder("prismatic_crucible_test_recipe_fully_random")
                .addData("required_color", Color.GREEN.key)
                .addData("mode_switch_type", ColorChangeMode.FULL_RANDOM.key)
                .inputItems(GOLD_INGOT, 4)
                .outputItems(IRON_INGOT, 10)
                .EUt(GTValues.VEX[GTValues.EV])
                .duration(200)
                .save(provider);

        PRISMATIC_CRUCIBLE_RECIPES.recipeBuilder("prismatic_crucible_test_recipe_relative")
                .addData("required_color", Color.YELLOW.key)
                .addData("result_color", 2)
                .addData("mode_switch_type", ColorChangeMode.DETERMINISTIC.key)
                .addData("color_change_relative", true)
                .inputItems(IRON_INGOT, 4)
                .outputItems(IRON_INGOT, 10)
                .EUt(GTValues.VEX[GTValues.EV])
                .duration(200)
                .save(provider);

        CompoundTag colorsTag = new CompoundTag();
        colorsTag.putIntArray("required_colors", new int[]{
                Color.RED.key,
                Color.MAGENTA.key
        });
        colorsTag.putIntArray("possible_new_colors", new int[]{
                Color.GREEN.key,
                Color.AZURE.key
        });

        PRISMATIC_CRUCIBLE_RECIPES.recipeBuilder("prismatic_crucible_test_recipe_random_with_list")
                .addData("colors_tag", colorsTag)
                .addData("mode_switch_type", ColorChangeMode.RANDOM_WITH_LIST.key)
                .inputItems(COPPER_INGOT, 4)
                .outputItems(COPPER_BLOCK, 10)
                .EUt(GTValues.VEX[GTValues.EV])
                .duration(200)
                .save(provider);
    }
}

package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.GTValues;
import net.minecraft.data.recipes.FinishedRecipe;
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
    }
}

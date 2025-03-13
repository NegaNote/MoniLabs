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
        PRISMATIC_CRUCIBLE_RECIPES.recipeBuilder("prismatic_crucible_test_recipe")
                .addData("required_color", Color.RED.key)
                .addData("mode_switch_type", ColorChangeMode.FULL_RANDOM.key)
                .inputItems(RED_DYE, 32)
                .outputItems(GREEN_DYE, 32)
                .EUt(GTValues.VEX[GTValues.HV])
                .duration(200)
                .save(provider);
    }
}

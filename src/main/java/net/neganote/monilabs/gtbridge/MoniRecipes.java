package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.GTValues;
import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

import java.util.function.Consumer;

import static net.minecraft.world.item.Items.*;
import static net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities.COLOR;
import static net.neganote.monilabs.gtbridge.MoniRecipeTypes.PRISMATIC_CRUCIBLE_RECIPES;

public class MoniRecipes {
    @SuppressWarnings("unused")
    public static void init(Consumer<FinishedRecipe> provider) {
        // Used to test prismatic crucible functionality
        PRISMATIC_CRUCIBLE_RECIPES.recipeBuilder("prismatic_crucible_test_recipe")
                .input(COLOR, Color.RED.modulus)
                .output(COLOR, Color.GREEN.modulus)
                .inputItems(RED_DYE, 32)
                .outputItems(GREEN_DYE, 32)
                .EUt(GTValues.VEX[GTValues.HV])
                .duration(200)
                .save(provider);
    }
}

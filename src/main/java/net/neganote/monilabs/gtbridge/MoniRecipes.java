package net.neganote.monilabs.gtbridge;

import net.minecraft.data.recipes.FinishedRecipe;

import java.util.function.Consumer;

public class MoniRecipes {
    @SuppressWarnings("unused")
    public static void init(Consumer<FinishedRecipe> provider) {
        MoniRecipeTypes.init();
    }
}

package net.neganote.monilabs.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.monilabs.common.item.MoniItems;
import net.neganote.monilabs.data.tags.MoniTags;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;

import java.util.function.Consumer;

public class MoniRecipes {

    public static void init(Consumer<FinishedRecipe> provider) {
        MoniRecipeTypes.OMNIC_SYNTHESIZER_RECIPES
                .recipeBuilder("omnic_synthesis")
                .inputItems(MoniTags.OMNIC_SYNTHESIZER_INPUT)
                .outputItems(MoniItems.MOTE_OF_OMNIUM.get())
                .EUt(GTValues.VA[GTValues.UEV])
                .duration(1)
                .save(provider);
    }
}

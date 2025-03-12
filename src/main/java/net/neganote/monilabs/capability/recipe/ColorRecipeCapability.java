package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.SerializerInteger;

public class ColorRecipeCapability extends RecipeCapability<Integer> {
    public final static ColorRecipeCapability CAP = new ColorRecipeCapability();

    protected ColorRecipeCapability() {
        super("color", 0xFFFFFFFF, true, 10, SerializerInteger.INSTANCE);
    }
}

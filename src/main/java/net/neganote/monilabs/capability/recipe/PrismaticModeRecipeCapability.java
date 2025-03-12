package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.SerializerInteger;

public class PrismaticModeRecipeCapability extends RecipeCapability<Integer> {
    public static PrismaticModeRecipeCapability CAP = new PrismaticModeRecipeCapability();

    protected PrismaticModeRecipeCapability() {
        super("prismatic_mode", 0xFFFFFFFF, true, 11, SerializerInteger.INSTANCE);
    }
}

package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class MoniRecipeCapabilities {
    public static final ColorRecipeCapability COLOR = ColorRecipeCapability.CAP;
    public static final PrismaticModeRecipeCapability PRISMATIC_MODE = PrismaticModeRecipeCapability.CAP;

    public static void init() {
        GTRegistries.RECIPE_CAPABILITIES.register(COLOR.name, COLOR);
        GTRegistries.RECIPE_CAPABILITIES.register(PRISMATIC_MODE.name, PRISMATIC_MODE);
    }
}

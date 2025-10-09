package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.registry.GTRegistries;

public class MoniRecipeCapabilities {

    public static final ChromaRecipeCapability CHROMA = ChromaRecipeCapability.CAP;
    public static final MicroverseRecipeCapability MICROVERSE = MicroverseRecipeCapability.CAP;

    public static void init() {
        GTRegistries.RECIPE_CAPABILITIES.register(CHROMA.name, CHROMA);
        GTRegistries.RECIPE_CAPABILITIES.register(MICROVERSE.name, MICROVERSE);
    }
}

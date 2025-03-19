package net.neganote.monilabs;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;

import java.util.function.Consumer;

@SuppressWarnings("unused")
@GTAddon
public class MoniLabsGTAddon implements IGTAddon {
    @Override
    public GTRegistrate getRegistrate() {
        return MoniLabs.REGISTRATE;
    }

    @Override
    public void initializeAddon() {

    }

    @Override
    public String addonModId() {
        return MoniLabs.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {
        //CustomTagPrefixes.init();
    }

    // Currently disabled because no recipes are defined in the mod.
    /*
    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        MoniRecipes.init(provider);
    }
    */

    @Override
    public void registerRecipeCapabilities() {
        MoniRecipeCapabilities.init();
    }

    /*
    public static NumberComponent.IntRange PRISMATIC_MODE_RANGE = new NumberComponent.IntRange(0, 2);
    public static final ContentJS<Integer> PRISMATIC_MODE_OUT = new ContentJS<>(PRISMATIC_MODE_RANGE, MoniRecipeCapabilities.PRISMATIC_MODE, true);

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {

    }
    */
}

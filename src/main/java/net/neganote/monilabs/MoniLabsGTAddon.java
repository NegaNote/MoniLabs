package net.neganote.monilabs;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.ContentJS;
import com.mojang.datafixers.util.Pair;
import dev.latvian.mods.kubejs.recipe.component.NumberComponent;
import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.gtbridge.MoniRecipes;

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

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        MoniRecipes.init(provider);
    }

    @Override
    public void registerRecipeCapabilities() {
        MoniRecipeCapabilities.init();
    }

    public static NumberComponent.IntRange COLOR_RANGE = new NumberComponent.IntRange(1, 12);
    public static final ContentJS<Integer> COLOR_OUT = new ContentJS<>(COLOR_RANGE, MoniRecipeCapabilities.COLOR, true);

    public static NumberComponent.IntRange PRISMATIC_MODE_RANGE = new NumberComponent.IntRange(0, 2);
    public static final ContentJS<Integer> PRISMATIC_MODE_OUT = new ContentJS<>(PRISMATIC_MODE_RANGE, MoniRecipeCapabilities.PRISMATIC_MODE, true);

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(MoniRecipeCapabilities.COLOR, Pair.of(null, COLOR_OUT));
        event.registerKey(MoniRecipeCapabilities.PRISMATIC_MODE, Pair.of(null, PRISMATIC_MODE_OUT));
    }

}

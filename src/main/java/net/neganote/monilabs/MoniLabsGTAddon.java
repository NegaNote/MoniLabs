package net.neganote.monilabs;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.ContentJS;

import net.minecraft.data.recipes.FinishedRecipe;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.data.materials.MoniElements;
import net.neganote.monilabs.common.machine.multiblock.Color;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.integration.kjs.recipe.ChromaComponent;
import net.neganote.monilabs.integration.kjs.recipe.MicroverseComponent;
import net.neganote.monilabs.recipe.MoniRecipes;

import com.mojang.datafixers.util.Pair;

import java.util.function.Consumer;

@SuppressWarnings("unused")
@GTAddon
public class MoniLabsGTAddon implements IGTAddon {

    @Override
    public GTRegistrate getRegistrate() {
        return MoniLabs.REGISTRATE;
    }

    @Override
    public void initializeAddon() {}

    @Override
    public String addonModId() {
        return MoniLabs.MOD_ID;
    }

    @Override
    public void registerTagPrefixes() {
        // CustomTagPrefixes.init();
    }

    @Override
    public void addRecipes(Consumer<FinishedRecipe> provider) {
        MoniRecipes.init(provider);
    }

    @Override
    public void registerRecipeCapabilities() {
        MoniRecipeCapabilities.init();
    }

    public static final ChromaComponent CHROMA_COMPONENT = new ChromaComponent();
    public static final ContentJS<Color> CHROMA_IN = new ContentJS<>(CHROMA_COMPONENT,
            MoniRecipeCapabilities.CHROMA, true);

    public static final MicroverseComponent MICROVERSE_COMPONENT = new MicroverseComponent();
    public static final ContentJS<Microverse> MICROVERSE_IN = new ContentJS<>(MICROVERSE_COMPONENT,
            MoniRecipeCapabilities.MICROVERSE, true);

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(MoniRecipeCapabilities.CHROMA, Pair.of(CHROMA_IN, null));
        event.registerKey(MoniRecipeCapabilities.MICROVERSE, Pair.of(MICROVERSE_IN, null));
    }

    @Override
    public void registerElements() {
        MoniElements.init();
    }

    @Override
    public boolean requiresHighTier() {
        return true;
    }
}

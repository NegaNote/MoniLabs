package net.neganote.monilabs;

import com.gregtechceu.gtceu.api.addon.GTAddon;
import com.gregtechceu.gtceu.api.addon.IGTAddon;
import com.gregtechceu.gtceu.api.addon.events.KJSRecipeKeyEvent;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.integration.kjs.recipe.components.ContentJS;
import com.mojang.datafixers.util.Pair;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.integration.kjs.recipe.ChromaComponent;

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


    public static final ChromaComponent CHROMA_COMPONENT = new ChromaComponent();
    public static final ContentJS<PrismaticCrucibleMachine.Color> CHROMA_IN = new ContentJS<>(CHROMA_COMPONENT, MoniRecipeCapabilities.CHROMA, true);

    @Override
    public void registerRecipeKeys(KJSRecipeKeyEvent event) {
        event.registerKey(MoniRecipeCapabilities.CHROMA, Pair.of(CHROMA_IN, null));
    }

}

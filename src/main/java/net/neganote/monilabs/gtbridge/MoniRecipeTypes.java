package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;

import static net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities.COLOR;
import static net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities.PRISMATIC_MODE;

public class MoniRecipeTypes {
    public static final GTRecipeType PRISMATIC_CRUCIBLE_RECIPES = GTRecipeTypes
            .register("prismatic_crucible", GTRecipeTypes.MULTIBLOCK)
            .setMaxIOSize(3, 1, 1, 0)
            .setMaxSize(IO.IN, COLOR, 1)
            .setMaxSize(IO.OUT, COLOR, 1)
            .setMaxSize(IO.OUT, PRISMATIC_MODE, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static void init() {
    }
}

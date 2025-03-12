package net.neganote.monilabs.gtbridge;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.common.data.GTRecipeTypes;
import com.lowdragmc.lowdraglib.gui.texture.ProgressTexture;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

import static net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities.COLOR;
import static net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities.PRISMATIC_MODE;

public class MoniRecipeTypes {
    public static final GTRecipeType PRISMATIC_CRUCIBLE_RECIPES = GTRecipeTypes
            .register("prismatic_crucible", GTRecipeTypes.MULTIBLOCK)
            .addDataInfo(data -> {
                int modulus = data.getInt("required_color");
                return LocalizationUtils.format("monilabs.recipe.color", LocalizationUtils.format(Color.getColorFromModulus(modulus).nameKey));
            })
            .setMaxIOSize(3, 1, 1, 0)
            .setEUIO(IO.IN)
            .setMaxSize(IO.OUT, COLOR, 1)
            .setMaxSize(IO.OUT, PRISMATIC_MODE, 1)
            .setProgressBar(GuiTextures.PROGRESS_BAR_ARROW, ProgressTexture.FillDirection.LEFT_TO_RIGHT);

    public static void init() {
    }
}

package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;

import net.minecraftforge.fluids.FluidStack;

import java.util.List;

@SuppressWarnings("unused")
public class SculkVatMachine extends WorkableElectricMultiblockMachine {

    public SculkVatMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static RecipeModifier recipeModifier() {
        return (metaMachine, gtRecipe) -> {
            if (metaMachine instanceof SculkVatMachine sculkVat) {
                var tanks = sculkVat.getCapabilitiesFlat(IO.OUT, GTRecipeCapabilities.FLUID)
                        .stream()
                        .filter(NotifiableFluidTank.class::isInstance)
                        .map(NotifiableFluidTank.class::cast)
                        .toList();
                double modifier = getSculkVatMultiplier(tanks);
                return ModifierFunction.builder()
                        .outputModifier(new ContentModifier(modifier, 0.0))
                        .build();
            } else {
                return ModifierFunction.IDENTITY;
            }
        };
    }

    private static double getSculkVatMultiplier(List<NotifiableFluidTank> tanks) {
        if (tanks.size() != 1) {
            throw new IllegalStateException("Sculk Vat must have exactly 1x fluid output hatch");
        }
        var fluidExportHatchTank = tanks.get(0);
        int capacity = fluidExportHatchTank.getTankCapacity(0);
        var contents = fluidExportHatchTank.getContents();
        int stored = 0;
        if (!contents.isEmpty()) {
            stored = ((FluidStack) contents.get(0)).getAmount();
        }
        double x = (double) stored / capacity;
        return Math.pow(1.0 / Math.exp(7.0 * Math.pow((x - 0.5), 2.0)), 2.0);
    }
}

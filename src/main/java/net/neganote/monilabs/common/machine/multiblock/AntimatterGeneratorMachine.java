package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;

import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("unused")
public class AntimatterGeneratorMachine extends WorkableElectricMultiblockMachine {

    public AntimatterGeneratorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    public static RecipeModifier recipeModifier() {
        return (machine, recipe) -> {
            if (machine instanceof AntimatterGeneratorMachine antiGenerator) {
                var parallels = ParallelLogic.getParallelAmount(machine, recipe, 1000000000);
                if (parallels == 1) {
                    return ModifierFunction.IDENTITY;
                } else {
                    return ModifierFunction.builder()
                            .modifyAllContents(ContentModifier.multiplier(parallels))
                            .eutMultiplier(parallels)
                            .parallels(parallels)
                            .build();
                }
            } else {
                return ModifierFunction.IDENTITY;
            }
        };
    }

    @Override
    public boolean onWorking() {
        var inputHatches = getCapabilitiesFlat(IO.IN, GTRecipeCapabilities.FLUID).stream()
                .filter(NotifiableFluidTank.class::isInstance)
                .map(NotifiableFluidTank.class::cast)
                .toList();

        for (var inputHatch : inputHatches) {
            for (int i = 0; i < inputHatch.getSize(); i++) {
                inputHatch.setFluidInTank(i, FluidStack.EMPTY);
            }
        }

        return super.onWorking();
    }
}

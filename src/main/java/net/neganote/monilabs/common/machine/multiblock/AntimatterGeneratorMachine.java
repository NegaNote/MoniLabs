package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;

import net.minecraftforge.fluids.FluidStack;

@SuppressWarnings("unused")
public class AntimatterGeneratorMachine extends WorkableElectricMultiblockMachine {

    public AntimatterGeneratorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
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

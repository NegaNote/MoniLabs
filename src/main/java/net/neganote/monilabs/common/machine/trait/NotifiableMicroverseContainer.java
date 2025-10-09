package net.neganote.monilabs.common.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotifiableMicroverseContainer extends NotifiableRecipeHandlerTrait<Microverse> {

    public NotifiableMicroverseContainer(MetaMachine machine) {
        super(machine);
    }

    public Microverse getHeldMicroverse() {
        if (!(getMachine() instanceof MicroverseProjectorMachine projector)) {
            throw new IllegalStateException();
        }
        return projector.getMicroverse();
    }

    @Override
    public IO getHandlerIO() {
        return IO.IN;
    }

    @Override
    public List<Microverse> handleRecipeInner(IO io, GTRecipe recipe, List<Microverse> left, boolean simulate) {
        Microverse recipeMicroverse = left.get(0);
        if (getHeldMicroverse() == recipeMicroverse) {
            return null;
        }
        return left;
    }

    @Override
    public @NotNull List<Object> getContents() {
        return List.of(getHeldMicroverse());
    }

    @Override
    public double getTotalContentAmount() {
        return 1;
    }

    @Override
    public RecipeCapability<Microverse> getCapability() {
        return MoniRecipeCapabilities.MICROVERSE;
    }
}

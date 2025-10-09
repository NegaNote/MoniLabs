package net.neganote.monilabs.common.machine.trait;

import com.gregtechceu.gtceu.api.machine.feature.IRecipeLogicMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;

public class DummyRecipeLogic extends RecipeLogic {

    public DummyRecipeLogic(IRecipeLogicMachine machine) {
        super(machine);
    }

    @Override
    public void serverTick() {
        // Do nothing! This is so we can register recipes normally but actually do the
        // handling of them in a generic tick subscription in the machine itself.
    }
}

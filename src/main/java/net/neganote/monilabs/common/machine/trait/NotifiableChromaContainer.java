package net.neganote.monilabs.common.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import net.neganote.monilabs.capability.recipe.ChromaIngredient;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.Color;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class NotifiableChromaContainer extends NotifiableRecipeHandlerTrait<ChromaIngredient> {

    public NotifiableChromaContainer(MetaMachine machine) {
        super(machine);
    }

    public Color getHeldColor() {
        if (!(getMachine() instanceof PrismaticCrucibleMachine prismac)) {
            throw new IllegalStateException();
        }
        return prismac.getColor();
    }

    @Override
    public IO getHandlerIO() {
        return IO.IN;
    }

    @Override
    public List<ChromaIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<ChromaIngredient> left,
                                                    boolean simulate) {
        ChromaIngredient recipeColor = left.get(0);
        List<Object> contents = getContents();
        if (contents.stream().anyMatch(recipeColor::equals)) {
            return null;
        } else {
            return left;
        }
    }

    @Override
    public @NotNull List<Object> getContents() {
        return Color.getColorsWithCategories(getHeldColor())
                .stream()
                .map(ChromaIngredient::new)
                .map(Object.class::cast)
                .toList();
    }

    @Override
    public int getSize() {
        return 15;
    }

    @Override
    public double getTotalContentAmount() {
        return getContents().size();
    }

    @Override
    public RecipeCapability<ChromaIngredient> getCapability() {
        return MoniRecipeCapabilities.CHROMA;
    }
}

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
        List<Color> colors = Color.getColorsWithCategories(recipeColor.color());
        if (colors.stream().anyMatch(getHeldColor()::equals)) {
            return null;
        } else {
            return left;
        }
    }

    @Override
    public @NotNull List<Object> getContents() {
        return List.of(new ChromaIngredient(getHeldColor()));
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public double getTotalContentAmount() {
        return 1;
    }

    @Override
    public RecipeCapability<ChromaIngredient> getCapability() {
        return MoniRecipeCapabilities.CHROMA;
    }
}

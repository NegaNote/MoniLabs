package net.neganote.monilabs.common.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.MapIngredientTypeManager;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.neganote.monilabs.capability.recipe.ChromaIngredient;
import net.neganote.monilabs.capability.recipe.ChromaRecipeCapability;
import net.neganote.monilabs.capability.recipe.MapColorIngredient;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.Color;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public class NotifiableChromaContainer extends NotifiableRecipeHandlerTrait<ChromaIngredient> {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            NotifiableChromaContainer.class,
            NotifiableRecipeHandlerTrait.MANAGED_FIELD_HOLDER);

    @Persisted
    private Color heldColor;

    public NotifiableChromaContainer(MetaMachine machine) {
        super(machine);
        if (machine instanceof PrismaticCrucibleMachine prismaticCrucibleMachine) {
            this.heldColor = prismaticCrucibleMachine.getColorState();
        } else {
            // should never be called outside a PrismaC, but oh well
            this.heldColor = Color.RED;
        }
    }

    @Override
    public IO getHandlerIO() {
        return IO.IN;
    }

    public void setColor(Color newColor) {
        this.heldColor = newColor;
        notifyListeners();
    }

    @Override
    public List<ChromaIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<ChromaIngredient> left,
                                                    boolean simulate) {
        ChromaIngredient recipeColor = (ChromaIngredient) recipe.getInputContents(MoniRecipeCapabilities.CHROMA).get(0)
                .getContent();
        List<Color> colors = MapIngredientTypeManager.getFrom(recipeColor, ChromaRecipeCapability.CAP).stream()
                .map(MapColorIngredient.class::cast).filter(Objects::nonNull).map(ing -> ing.color).toList();
        if (colors.stream().anyMatch(heldColor::equals)) {
            return null;
        } else {
            return left;
        }
    }

    @Override
    public @NotNull List<Object> getContents() {
        return List.of(new ChromaIngredient(heldColor));
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

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

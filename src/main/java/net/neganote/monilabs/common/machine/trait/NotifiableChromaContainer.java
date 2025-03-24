package net.neganote.monilabs.common.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.neganote.monilabs.capability.recipe.ChromaIngredient;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

import org.jetbrains.annotations.Nullable;

import java.util.List;

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
    public List<ChromaIngredient> handleRecipe(IO io, GTRecipe recipe, List<?> left, @Nullable String slotName,
                                               boolean simulate) {
        return super.handleRecipe(io, recipe, left, slotName, simulate);
    }

    @Override
    public List<ChromaIngredient> handleRecipeInner(IO io, GTRecipe recipe, List<ChromaIngredient> left,
                                                    @Nullable String slotName, boolean simulate) {
        List<Color> colors = recipe.getInputContents(MoniRecipeCapabilities.CHROMA)
                .stream().map(c -> ((ChromaIngredient) c.getContent()).color())
                .toList();
        int key = this.heldColor.key;
        for (Color color : colors) {
            if (color.key > Color.ACTUAL_COLOR_COUNT) {
                if (key % 4 == 0) {
                    if (color == Color.PRIMARY)
                        return null;
                    if (color == Color.BASIC)
                        return null;
                } else if ((key + 2) % 4 == 0) {
                    if (color == Color.SECONDARY)
                        return null;
                    if (color == Color.BASIC)
                        return null;
                } else {
                    if (color == Color.TERTIARY)
                        return null;
                }
                if (color == Color.ANY)
                    return null;
            } else {
                if (this.heldColor == color) {
                    return null;
                }
            }
        }
        return left;
    }

    @Override
    public List<Object> getContents() {
        return List.of(heldColor);
    }

    @Override
    public int getSize() {
        return Color.COLOR_COUNT;
    }

    @Override
    public double getTotalContentAmount() {
        return 0;
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

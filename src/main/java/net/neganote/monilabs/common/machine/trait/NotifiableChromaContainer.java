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
                if (color.isTypeNotColor()) {
                    switch (color) {
                        case NOT_RED -> {
                            return this.heldColor == Color.RED ? left : null;
                        }
                        case NOT_ORANGE -> {
                            return this.heldColor == Color.ORANGE ? left : null;
                        }
                        case NOT_YELLOW -> {
                            return this.heldColor == Color.YELLOW ? left : null;
                        }
                        case NOT_LIME -> {
                            return this.heldColor == Color.LIME ? left : null;
                        }
                        case NOT_GREEN -> {
                            return this.heldColor == Color.GREEN ? left : null;
                        }
                        case NOT_TEAL -> {
                            return this.heldColor == Color.TEAL ? left : null;
                        }
                        case NOT_CYAN -> {
                            return this.heldColor == Color.CYAN ? left : null;
                        }
                        case NOT_AZURE -> {
                            return this.heldColor == Color.AZURE ? left : null;
                        }
                        case NOT_BLUE -> {
                            return this.heldColor == Color.BLUE ? left : null;
                        }
                        case NOT_INDIGO -> {
                            return this.heldColor == Color.INDIGO ? left : null;
                        }
                        case NOT_MAGENTA -> {
                            return this.heldColor == Color.MAGENTA ? left : null;
                        }
                        case NOT_PINK -> {
                            return this.heldColor == Color.PINK ? left : null;
                        }
                    }
                }
            }
            if (this.heldColor == color) {
                return null;
            }
        }
        return left;
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

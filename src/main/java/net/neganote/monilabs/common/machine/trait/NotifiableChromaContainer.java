package net.neganote.monilabs.common.machine.trait;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableRecipeHandlerTrait;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import lombok.Setter;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class NotifiableChromaContainer extends NotifiableRecipeHandlerTrait<Color> {
    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(NotifiableChromaContainer.class,
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
        // I assume this is where you'd notify the machine holding this, but idk how to do that
        // For now it's probably okay because controller is the container so we alredy knew the color changed
    }

    @Override
    public List<Color> handleRecipe(IO io, GTRecipe recipe, List<?> left, @Nullable String slotName, boolean simulate) {
        return super.handleRecipe(io, recipe, left, slotName, simulate);
    }

    @Override
    public List<Color> handleRecipeInner(IO io, GTRecipe recipe, List<Color> left, @Nullable String slotName, boolean simulate) {
        List<Color> colors = recipe.getInputContents(MoniRecipeCapabilities.CHROMA)
                .stream().map(c -> (Color) c.getContent())
                .toList();
        if (left.contains(heldColor) && colors.contains(heldColor)) {
            return null;
        } else {
            return left;
        }
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
    public RecipeCapability<Color> getCapability() {
        return MoniRecipeCapabilities.CHROMA;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

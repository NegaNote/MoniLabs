package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class PrismaticCrucibleMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PrismaticCrucibleMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    private int colorKey;
    public PrismaticCrucibleMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        colorKey = Color.RED.key;
    }

    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (recipe.data.contains("input_states")) {
            int inputStatesCount = recipe.data.getInt("input_states");
            if (inputStatesCount == 1) {                            // Any state
                if (recipe.data.getInt("input_states_0") != colorKey) return false;

            } else if (inputStatesCount == Color.COLOR_COUNT) {     // One state
                return super.beforeWorking(recipe);

            } else {                                                // State list
                boolean noMatch = IntStream.range(0, inputStatesCount)
                        .map(i -> recipe.data.getInt("output_states_" + i))
                        .noneMatch(s -> s == colorKey);
                if (noMatch) {
                    return false;
                }
            }
        } //If input_states is undefined, assume any state is accepted to avoid edge cases.
        return super.beforeWorking(recipe);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        GTRecipe recipe = recipeLogic.getLastRecipe();
        if (recipe == null) {
            return;
        }

        int newKey = 0;

        int outputStatesCount = recipe.data.getInt("output_states");
        if (recipe.data.contains("output_states")) {
            if (outputStatesCount == 1) {                                   // Deterministic
                newKey = recipe.data.getInt("output_states_0");

            } else if (outputStatesCount == Color.COLOR_COUNT) {            // Full random
                newKey = Color.getRandomColor();

            } else {                                                        // Random Among List
                int[] outputStates = IntStream.range(0, outputStatesCount)
                        .map(i -> recipe.data.getInt("output_states_" + i))
                        .toArray();
                newKey = Color.getRandomColorFromKeys(outputStates);
            }

            if (recipe.data.contains("color_change_relative") && recipe.data.getBoolean("color_change_relative")) {
                newKey = (colorKey + newKey) % Color.COLOR_COUNT;
            }
        } else {
            newKey = Color.getRandomColor();
        }
        changeColorState(Color.getColorFromKey(newKey));
    }

    private void changeColorState(Color newColor) {
        colorKey = newColor.key;
    }

    public Color getColorState() {
        return Color.getColorFromKey(colorKey);
    }

    public enum Color {
        RED(0, "monilabs.prismatic.color_name.red"),
        ORANGE(1, "monilabs.prismatic.color_name.orange"),
        YELLOW(2, "monilabs.prismatic.color_name.yellow"),
        LIME(3, "monilabs.prismatic.color_name.lime"),
        GREEN(4, "monilabs.prismatic.color_name.green"),
        TEAL(5, "monilabs.prismatic.color_name.turquoise"),
        CYAN(6, "monilabs.prismatic.color_name.cyan"),
        AZURE(7, "monilabs.prismatic.color_name.azure"),
        BLUE(8, "monilabs.prismatic.color_name.blue"),
        INDIGO(9, "monilabs.prismatic.color_name.indigo"),
        MAGENTA(10, "monilabs.prismatic.color_name.magenta"),
        PINK(11, "monilabs.prismatic.color_name.pink");

        public static final Color[] COLORS = Color.values();

        public final String nameKey;
        public final int key;

        public static final int COLOR_COUNT = Color.values().length;

        Color(int key, String nameKey) {
            this.key = key;
            this.nameKey = nameKey;
        }

        public static Color getColorFromKey(int pKey) {
            return COLORS[pKey];
        }
        public static int getRandomColor() {
            return (int) Math.floor(Math.random() * Color.COLOR_COUNT);
        }

        public static int getRandomColorFromKeys(int[] keys) {
            return keys[(int) Math.floor(Math.random() * (double) keys.length)];
        }
    }
}

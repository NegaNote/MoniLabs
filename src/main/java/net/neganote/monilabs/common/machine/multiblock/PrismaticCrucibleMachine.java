package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.trait.NotifiableChromaContainer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.IntStream;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class PrismaticCrucibleMachine extends WorkableElectricMultiblockMachine {
    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(PrismaticCrucibleMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    private Color color;

    @Persisted
    private NotifiableChromaContainer notifiableChromaContainer;

    public PrismaticCrucibleMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.color = Color.RED;
        this.notifiableChromaContainer = new NotifiableChromaContainer(this);
    }

    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        changeColorState(Color.RED);
    }

//    @Override
//    public boolean beforeWorking(@Nullable GTRecipe recipe) {
//        if (recipe == null) return false;
//        if (recipe.data.contains("input_states")) {
//            int inputStatesCount = recipe.data.getInt("input_states");
//            if (inputStatesCount == 1) {                            // Any state
//                if (recipe.data.getInt("input_states_0") != color.key) return false;
//
//            } else if (inputStatesCount == Color.COLOR_COUNT) {     // One state
//                return super.beforeWorking(recipe);
//
//            } else {                                                // State list
//                boolean noMatch = IntStream.range(0, inputStatesCount)
//                        .map(i -> recipe.data.getInt("input_states_" + i))
//                        .noneMatch(s -> s == color.key);
//                if (noMatch) {
//                    return false;
//                }
//            }
//        } //If input_states is undefined, assume any state is accepted to avoid edge cases.
//        return super.beforeWorking(recipe);
//    }

    @Override
    public int getOutputSignal(@Nullable Direction side) {
        if (!isFormed()) {
            return 0;
        } else {
            return color.key + 1;
        }
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        GTRecipe recipe = recipeLogic.getLastRecipe();
        if (recipe == null) {
            return;
        }

        int newKey;

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
                newKey = (color.key + newKey) % Color.COLOR_COUNT;
            }
        } else {
            newKey = Color.getRandomColor();
        }
        changeColorState(Color.getColorFromKey(newKey));
    }

    private void changeColorState(Color newColor) {
        color = newColor;
        updateSignal();
    }

    public Color getColorState() {
        return color;
    }

    public enum Color {
        RED(0, "monilabs.prismatic.color_name.red", 1.0f, 0f, 0f),
        ORANGE(1, "monilabs.prismatic.color_name.orange", 1.0f, 0.5f, 0f),
        YELLOW(2, "monilabs.prismatic.color_name.yellow", 1.0f, 1.0f, 0f),
        LIME(3, "monilabs.prismatic.color_name.lime", 0.5f, 1.0f, 0f),
        GREEN(4, "monilabs.prismatic.color_name.green", 0f, 1.0f, 0f),
        TEAL(5, "monilabs.prismatic.color_name.turquoise", 0f, 1.0f, 0.5f),
        CYAN(6, "monilabs.prismatic.color_name.cyan", 0f, 1.0f, 1.0f),
        AZURE(7, "monilabs.prismatic.color_name.azure", 0f, 0.5f, 1.0f),
        BLUE(8, "monilabs.prismatic.color_name.blue", 0f, 0f, 1.0f),
        INDIGO(9, "monilabs.prismatic.color_name.indigo", 0.5f, 0f, 1.0f),
        MAGENTA(10, "monilabs.prismatic.color_name.magenta", 1.0f, 0f, 1.0f),
        PINK(11, "monilabs.prismatic.color_name.pink", 1.0f, 0f, 0.5f);

        public static final Color[] COLORS = Color.values();

        public final String nameKey;
        public final int key;
        public final float r;
        public final float g;
        public final float b;

        public static final int COLOR_COUNT = Color.values().length;

        Color(int key, String nameKey, float r, float g, float b) {
            this.key = key;
            this.nameKey = nameKey;
            this.r = r;
            this.g = g;
            this.b = b;
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

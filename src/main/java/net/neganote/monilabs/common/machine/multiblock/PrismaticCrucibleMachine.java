package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

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
        if (!recipe.data.contains("required_color") || recipe.data.getInt("required_color") != colorKey) {
            return false;
        }
        return super.beforeWorking(recipe);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        GTRecipe recipe = recipeLogic.getLastRecipe();
        if (recipe == null) {
            return;
        }

        ColorChangeMode mode = ColorChangeMode.getModeFromKey(recipe.data.getInt("mode_switch_type"));
        switch (mode) {
            case DETERMINISTIC -> changeColorState(Color.getColorFromKey(recipe.data.getInt("result_color")));
            case RANDOM_WITH_LIST -> {
                int[] newPossibleColors = recipe.data.getIntArray("possible_new_colors");
                changeColorState(Color.getRandomColorFromKeys(newPossibleColors));
            }
            case FULL_RANDOM -> changeColorState(Color.getRandomColor());
        }
    }

    private void changeColorState(Color newColor) {
        colorKey = newColor.key;
    }

    public Color getColorState() {
        return Color.getColorFromKey(colorKey);
    }

    public enum ColorChangeMode {
        DETERMINISTIC(0, "monilabs.prismatic.mode_name.deterministic"),
        RANDOM_WITH_LIST(1, "monilabs.prismatic.mode_name.random"),
        FULL_RANDOM(2, "monilabs.prismatic.mode_name.random");

        public final int key;
        public final String nameKey;

        ColorChangeMode(int key, String nameKey) {
            this.key = key;
            this.nameKey = nameKey;
        }

        public static ColorChangeMode getModeFromKey(int key) {
            return switch (key) {
                case 1:
                    yield ColorChangeMode.RANDOM_WITH_LIST;
                case 2:
                    yield ColorChangeMode.FULL_RANDOM;
                case 0:
                default:
                    yield ColorChangeMode.DETERMINISTIC;
            };
        }
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

        public final String nameKey;
        public final int key;

        Color(int key, String nameKey) {
            this.key = key;
            this.nameKey = nameKey;
        }

        public static Color getColorFromKey(int modulus) {
            return switch (modulus) {
                case 1:
                    yield Color.ORANGE;
                case 2:
                    yield Color.YELLOW;
                case 3:
                    yield Color.LIME;
                case 4:
                    yield Color.GREEN;
                case 5:
                    yield Color.TEAL;
                case 6:
                    yield Color.CYAN;
                case 7:
                    yield Color.AZURE;
                case 8:
                    yield Color.BLUE;
                case 9:
                    yield Color.INDIGO;
                case 10:
                    yield Color.MAGENTA;
                case 11:
                    yield Color.PINK;
                case 0:
                default:
                    yield Color.RED;
            };
        }
        public static Color getRandomColor() {
            return getColorFromKey((int) Math.floor(Math.random() * 12.0));
        }

        public static Color getRandomColorFromKeys(int[] keys) {
            return getColorFromKey(keys[(int) Math.floor(Math.random() * (double) keys.length)]);
        }
    }
}

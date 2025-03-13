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

        public static final ColorChangeMode[] MODES = ColorChangeMode.values();

        public final int key;
        public final String nameKey;

        ColorChangeMode(int key, String nameKey) {
            this.key = key;
            this.nameKey = nameKey;
        }

        public static ColorChangeMode getModeFromKey(int pKey) {
            return MODES[pKey];
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

        public static final Color[] COLORS = Color.values();

        public final String nameKey;
        public final int key;

        Color(int key, String nameKey) {
            this.key = key;
            this.nameKey = nameKey;
        }

        public static Color getColorFromKey(int pKey) {
            return COLORS[pKey];
        }
        public static Color getRandomColor() {
            return getColorFromKey((int) Math.floor(Math.random() * 12.0));
        }

        public static Color getRandomColorFromKeys(int[] keys) {
            return getColorFromKey(keys[(int) Math.floor(Math.random() * (double) keys.length)]);
        }
    }
}

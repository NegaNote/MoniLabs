package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import net.minecraft.core.Direction;
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
    public void onStructureInvalid() {
        super.onStructureInvalid();
        changeColorState(Color.RED);
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
    public int getOutputSignal(@Nullable Direction side) {
        if (!isFormed()) {
            return 0;
        } else {
            return colorKey + 1;
        }
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
        updateSignal();
    }

    public Color getColorState() {
        return Color.getColorFromKey(colorKey);
    }

    public enum ColorChangeMode {
        DETERMINISTIC(0),
        RANDOM_WITH_LIST(1),
        FULL_RANDOM(2);

        public static final ColorChangeMode[] MODES = ColorChangeMode.values();

        public final int key;

        ColorChangeMode(int key) {
            this.key = key;
        }

        public static ColorChangeMode getModeFromKey(int pKey) {
            return MODES[pKey];
        }
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
        public static Color getRandomColor() {
            return getColorFromKey((int) Math.floor(Math.random() * 12.0));
        }

        public static Color getRandomColorFromKeys(int[] keys) {
            return getColorFromKey(keys[(int) Math.floor(Math.random() * (double) keys.length)]);
        }
    }
}

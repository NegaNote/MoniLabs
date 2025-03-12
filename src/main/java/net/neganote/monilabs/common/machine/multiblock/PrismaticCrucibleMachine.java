package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class PrismaticCrucibleMachine extends WorkableElectricMultiblockMachine {
    private Color color;
    private PrismaticMode mode;
    public PrismaticCrucibleMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        color = Color.RED;
        mode = PrismaticMode.DETERMINISTIC;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        color = Color.RED;
        mode = PrismaticMode.DETERMINISTIC;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (!recipe.data.contains("required_color") || recipe.data.getInt("required_color") != color.modulus) {
            return false;
        }
        return super.beforeWorking(recipe);
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        GTRecipe recipe = recipeLogic.getLastRecipe();

    }

    private void changeColorState(Color newColor) {
        color = newColor;
    }

    public Color getColorState() {
        return color;
    }

    private void changeMode(PrismaticMode newMode) {
        mode = newMode;
    }

    public PrismaticMode getCurrentMode() {
        return mode;
    }

    public enum PrismaticMode {
        DETERMINISTIC(1, "monilabs.prismatic.mode_name.deterministic"),
        RANDOM_WITH_LIST(2, "monilabs.prismatic.mode_name.random"),
        FULL_RANDOM(3, "monilabs.prismatic.mode_name.random");

        public final int key;
        public final String nameKey;

        PrismaticMode(int key, String nameKey) {
            this.key = key;
            this.nameKey = nameKey;
        }

        public static PrismaticMode getModeFromKey(int key) {
            return switch (key) {
                case 1:
                    yield PrismaticMode.RANDOM_WITH_LIST;
                case 2:
                    yield PrismaticMode.FULL_RANDOM;
                case 0:
                default:
                    yield PrismaticMode.DETERMINISTIC;
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
        public final int modulus;

        Color(int modulus, String nameKey) {
            this.modulus = modulus;
            this.nameKey = nameKey;
        }

        public static Color getColorFromModulus(int modulus) {
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
            return getColorFromModulus((int) Math.floor(Math.random() * 16.0));
        }
    }
}

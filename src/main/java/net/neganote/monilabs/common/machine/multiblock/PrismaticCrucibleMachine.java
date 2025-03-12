package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import net.minecraft.network.chat.Component;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class PrismaticCrucibleMachine extends WorkableElectricMultiblockMachine {
    private ColorState color;
    public PrismaticCrucibleMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        textList.add(Component.translatable("monilabs.prismatic.current_mode", Component.translatable(color.nameKey)));
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        color = ColorState.RED;
    }

    private void changeColorState(ColorState newColor) {
        color = newColor;
    }

    public ColorState getColorState() {
        return color;
    }

    public static ColorState getRandomColor() {
        int rand = (int) Math.floor(Math.random() * 16.0);
        return switch (rand) {
            case 1:
                yield ColorState.ORANGE;
            case 2:
                yield ColorState.YELLOW;
            case 3:
                yield ColorState.LIME;
            case 4:
                yield ColorState.GREEN;
            case 5:
                yield ColorState.TEAL;
            case 6:
                yield ColorState.CYAN;
            case 7:
                yield ColorState.AZURE;
            case 8:
                yield ColorState.BLUE;
            case 9:
                yield ColorState.INDIGO;
            case 10:
                yield ColorState.MAGENTA;
            case 11:
                yield ColorState.PINK;
            case 0:
            default:
                yield ColorState.RED;
        };
    }

    public enum WorkingMode {
        DETERMINISTIC,
        RANDOM_WITH_LIST,
        FULL_RANDOM
    }

    public enum ColorState {
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

        ColorState(int modulus, String nameKey) {
            this.modulus = modulus;
            this.nameKey = nameKey;
        }
    }
}

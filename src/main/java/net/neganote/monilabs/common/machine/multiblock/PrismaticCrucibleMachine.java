package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.part.PrismaticCoreBlock;
import net.neganote.monilabs.common.machine.trait.NotifiableChromaContainer;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.stream.IntStream;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class PrismaticCrucibleMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PrismaticCrucibleMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    private float[] renderOffset;

    @Persisted
    private Color color;

    @Persisted
    private final NotifiableChromaContainer notifiableChromaContainer;

    public PrismaticCrucibleMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.color = Color.RED;
        this.notifiableChromaContainer = new NotifiableChromaContainer(this);
        this.renderOffset = new float[] {};
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
    public void onStructureFormed() {
        super.onStructureFormed();

        for (Long longPos : Objects.requireNonNull(getActiveBlocks())) {
            if (Objects.requireNonNull(getLevel()).getBlockState(BlockPos.of(longPos))
                    .getBlock() instanceof PrismaticCoreBlock) {
                BlockPos controllerPos = getPos();
                BlockPos corePos = BlockPos.of(longPos);

                float xDiff = (float) (corePos.getX() - controllerPos.getX()) + 0.5f;
                float yDiff = (float) (corePos.getY() - controllerPos.getY()) + 0.5f;
                float zDiff = (float) (corePos.getZ() - controllerPos.getZ()) + 0.5f;
                this.renderOffset = new float[] { xDiff, yDiff, zDiff };
                break;
            }
        }
    }

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
        this.notifiableChromaContainer.setColor(newColor);
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
        PINK(11, "monilabs.prismatic.color_name.pink", 1.0f, 0f, 0.5f),
        PRIMARY(12, "", 0f, 0f, 0f),
        SECONDARY(13, "", 0f, 0f, 0f),
        BASIC(14, "", 0f, 0f, 0f),
        TERTIARY(15, "", 0f, 0f, 0f),
        ANY(16, "", 0f, 0f, 0f);

        public static final Color[] COLORS = Color.values();

        public static final Color[] ACTUAL_COLORS = new Color[] { RED, ORANGE, YELLOW, LIME, GREEN, TEAL, CYAN, AZURE,
                BLUE, INDIGO, MAGENTA, PINK };

        public final String nameKey;
        public final int key;
        public final float r;
        public final float g;
        public final float b;

        public static final int COLOR_COUNT = COLORS.length;

        public static final int ACTUAL_COLOR_COUNT = ACTUAL_COLORS.length;

        Color(int key, String nameKey, float r, float g, float b) {
            this.key = key;
            this.nameKey = nameKey;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public boolean isPrimary() {
            return this == RED || this == GREEN || this == BLUE;
        }

        public boolean isSecondary() {
            return this == YELLOW || this == CYAN || this == MAGENTA;
        }

        public boolean isBasic() {
            return isPrimary() || isSecondary();
        }

        public boolean isTertiary() {
            return !isBasic() && isRealColor();
        }

        public static Color getColorFromKey(int pKey) {
            return COLORS[pKey];
        }

        public static int getRandomColor() {
            return (int) Math.floor(Math.random() * Color.ACTUAL_COLOR_COUNT);
        }

        public static int getRandomColorFromKeys(int[] keys) {
            return keys[(int) Math.floor(Math.random() * (double) keys.length)];
        }

        public boolean isRealColor() {
            return this == RED || this == ORANGE || this == YELLOW || this == LIME || this == GREEN || this == TEAL ||
                    this == CYAN || this == AZURE || this == BLUE || this == INDIGO || this == MAGENTA || this == PINK;
        }
    }
}

package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.block.PrismaticActiveBlock;
import net.neganote.monilabs.common.machine.trait.NotifiableChromaContainer;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class PrismaticCrucibleMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PrismaticCrucibleMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    private final Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    @Persisted
    @DescSynced
    private Color color;

    @Getter
    @DescSynced
    @Persisted
    private BlockPos focusPos;

    private BlockPos structMin;

    private BlockPos structMax;

    @Persisted
    private final NotifiableChromaContainer notifiableChromaContainer;

    public PrismaticCrucibleMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.color = Color.RED;
        this.notifiableChromaContainer = new NotifiableChromaContainer(this);
        structMin = getPos();
        structMax = getPos();
        saveOffsets();
        focusPos = null;
    }

    private void saveStructBoundingBox() {
        var cache = getMultiblockState().getCache();
        if (structMin.equals(getPos()) && structMax.equals(getPos())) {
            for (BlockPos pos : cache) {
                if (structMin == null || structMax == null) {
                    structMin = pos;
                    structMax = pos;
                } else {
                    structMin = new BlockPos(Math.min(pos.getX(), structMin.getX()),
                            Math.min(pos.getY(), structMin.getY()),
                            Math.min(pos.getZ(), structMin.getZ()));
                    structMax = new BlockPos(Math.max(pos.getX(), structMax.getX()),
                            Math.max(pos.getY(), structMax.getY()),
                            Math.max(pos.getZ(), structMax.getZ()));
                }
            }
        }
    }

    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureInvalid() {
        changeColorState(Color.RED);
        fluidBlockOffsets.clear();
        super.onStructureInvalid();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
        structMin = getPos();
        structMax = getPos();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        for (BlockPos pos : getMultiblockState().getCache()) {
            if (Objects.requireNonNull(getLevel()).getBlockState(pos).getBlock() == MoniBlocks.PRISMATIC_FOCUS.get()) {
                focusPos = pos;
                break;
            }
        }

        saveStructBoundingBox();

        saveOffsets();
        updateActiveBlocks(true);
    }

    // Not currently used now, but would reset the machine's color
    // if there were multiple recipe types and the mode was switched
    @Override
    public void setActiveRecipeType(int activeRecipeType) {
        super.setActiveRecipeType(activeRecipeType);
        // Make this check because this method is also used to set the recipe
        // mode on world load
        if (isFormed()) {
            changeColorState(Color.RED);
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

        if (recipe.data.contains("output_states")) {
            int outputStatesCount = recipe.data.getInt("output_states");
            if (outputStatesCount == 1) {                                   // Deterministic
                newKey = recipe.data.getInt("output_states_0");

            } else if (outputStatesCount == Color.ACTUAL_COLOR_COUNT) {            // Full random
                newKey = Color.getRandomColor();

            } else {                                                        // Random Among List
                int[] outputStates = IntStream.range(0, outputStatesCount)
                        .map(i -> recipe.data.getInt("output_states_" + i))
                        .toArray();
                newKey = Color.getRandomColorFromKeys(outputStates);
            }

            if (recipe.data.contains("color_change_relative") && recipe.data.getBoolean("color_change_relative")) {
                newKey = Mth.positiveModulo(color.key + newKey, Color.ACTUAL_COLOR_COUNT);
            }
        } else {
            newKey = Color.getRandomColor();
        }
        changeColorState(Color.getColorFromKey(newKey));
    }

    private void changeColorState(Color newColor) {
        color = newColor;
        this.notifiableChromaContainer.setColor(newColor);
        getCoverContainer().getCovers().forEach((CoverBehavior::onChanged));
    }

    @Override
    public void updateActiveBlocks(boolean active) {
        if (activeBlocks != null) {
            for (long pos : activeBlocks) {
                var blockPos = BlockPos.of(pos);
                var blockState = Objects.requireNonNull(getLevel()).getBlockState(blockPos);
                if (blockState.getBlock() instanceof PrismaticActiveBlock block) {
                    var newState = block.changeActive(blockState, active || isFormed());
                    newState = block.changeColor(newState, color.key);
                    if (newState != blockState) {
                        getLevel().setBlockAndUpdate(blockPos, newState);
                    }
                }
            }
        } else if (!isFormed()) {
            for (int x = structMin.getX(); x <= structMax.getX(); x++) {
                for (int y = structMin.getY(); y <= structMax.getY(); y++) {
                    for (int z = structMin.getZ(); z <= structMax.getZ(); z++) {
                        var blockPos = new BlockPos(x, y, z);
                        var blockState = Objects.requireNonNull(getLevel()).getBlockState(blockPos);
                        if (blockState.getBlock() instanceof PrismaticActiveBlock block) {
                            var newState = block.changeActive(blockState, false);
                            if (newState != blockState) {
                                getLevel().setBlockAndUpdate(blockPos, newState);
                            }
                        }
                    }
                }
            }
        }
    }

    // Stolen from LargeChemicalBathMachine
    protected void saveOffsets() {
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction back = getFrontFacing().getOpposite();
        BlockPos pos = getPos();
        BlockPos center = pos.relative(up);
        Direction clockWise;
        Direction counterClockWise;
        if (up == Direction.UP || up == Direction.DOWN) {
            clockWise = getFrontFacing().getClockWise();
            counterClockWise = getFrontFacing().getCounterClockWise();
        } else {
            clockWise = Direction.UP;
            counterClockWise = Direction.DOWN;
        }
        center = center.relative(back);
        center = center.relative(back);
        for (int i = 2; i < 5; i++) {
            center = center.relative(back);
            fluidBlockOffsets.add(center.subtract(pos));
            fluidBlockOffsets.add(center.relative(clockWise).subtract(pos));
            fluidBlockOffsets.add(center.relative(counterClockWise).subtract(pos));
        }
    }

    @Override
    public boolean isBatchEnabled() {
        return false;
    }

    public Color getColorState() {
        return color;
    }

    public enum Color {

        RED(0, "monilabs.prismatic.color_name.red", 1.0f, 0f, 0f, 0xFFFF0000),
        ORANGE(1, "monilabs.prismatic.color_name.orange", 1.0f, 0.5f, 0f, 0xFFFF8000),
        YELLOW(2, "monilabs.prismatic.color_name.yellow", 1.0f, 1.0f, 0f, 0xFFFFFF00),
        LIME(3, "monilabs.prismatic.color_name.lime", 0.5f, 1.0f, 0f, 0xFF80FF00),
        GREEN(4, "monilabs.prismatic.color_name.green", 0f, 1.0f, 0f, 0xFF00FF00),
        TEAL(5, "monilabs.prismatic.color_name.teal", 0f, 1.0f, 0.5f, 0xFF00FF80),
        CYAN(6, "monilabs.prismatic.color_name.cyan", 0f, 1.0f, 1.0f, 0xFF00FFFF),
        AZURE(7, "monilabs.prismatic.color_name.azure", 0f, 0.5f, 1.0f, 0xFF0080FF),
        BLUE(8, "monilabs.prismatic.color_name.blue", 0f, 0f, 1.0f, 0xFF0000FF),
        INDIGO(9, "monilabs.prismatic.color_name.indigo", 0.5f, 0f, 1.0f, 0xFF8000FF),
        MAGENTA(10, "monilabs.prismatic.color_name.magenta", 1.0f, 0f, 1.0f, 0xFFFF00FF),
        PINK(11, "monilabs.prismatic.color_name.pink", 1.0f, 0f, 0.5f, 0xFFFF0080),
        PRIMARY(12, "", 0f, 0f, 0f, 0),
        SECONDARY(13, "", 0f, 0f, 0f, 0),
        BASIC(14, "", 0f, 0f, 0f, 0),
        TERTIARY(15, "", 0f, 0f, 0f, 0),
        ANY(16, "", 0f, 0f, 0f, 0),
        NOT_RED(17, "", 0f, 0f, 0f, 0),
        NOT_ORANGE(18, "", 0f, 0f, 0f, 0),
        NOT_YELLOW(19, "", 0f, 0f, 0f, 0),
        NOT_LIME(20, "", 0f, 0f, 0f, 0),
        NOT_GREEN(21, "", 0f, 0f, 0f, 0),
        NOT_TEAL(22, "", 0f, 0f, 0f, 0),
        NOT_CYAN(23, "", 0f, 0f, 0f, 0),
        NOT_AZURE(24, "", 0f, 0f, 0f, 0),
        NOT_BLUE(25, "", 0f, 0f, 0f, 0),
        NOT_INDIGO(26, "", 0f, 0f, 0f, 0),
        NOT_MAGENTA(27, "", 0f, 0f, 0f, 0),
        NOT_PINK(28, "", 0f, 0f, 0f, 0);

        public static final Color[] COLORS = Color.values();

        public static final Color[] ACTUAL_COLORS = Arrays.copyOfRange(COLORS, 0, 12);

        public static final Color[] NOT_COLORS = Arrays.copyOfRange(COLORS, 17, 29);

        public static final Color[] PRIMARY_COLORS = new Color[] { RED, GREEN, BLUE };

        public static final Color[] SECONDARY_COLORS = new Color[] { YELLOW, CYAN, MAGENTA };

        public static final Color[] BASIC_COLORS = new Color[] { RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA };

        public static final Color[] TERTIARY_COLORS = new Color[] { ORANGE, LIME, TEAL, AZURE, INDIGO, PINK };

        public final String nameKey;
        public final int key;
        public final float r;
        public final float g;
        public final float b;

        public final int integerColor;

        public static final int COLOR_COUNT = COLORS.length;

        public static final int ACTUAL_COLOR_COUNT = ACTUAL_COLORS.length;

        Color(int key, String nameKey, float r, float g, float b, int integerColor) {
            this.key = key;
            this.nameKey = nameKey;
            this.r = r;
            this.g = g;
            this.b = b;
            this.integerColor = integerColor;
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

        @Override
        public String toString() {
            return "Color{" +
                    "nameKey='" + nameKey + '\'' +
                    '}';
        }

        public static int getRandomColor() {
            return (int) Math.floor(Math.random() * Color.ACTUAL_COLOR_COUNT);
        }

        public static int getRandomColorFromKeys(int[] keys) {
            return keys[(int) Math.floor(Math.random() * (double) keys.length)];
        }

        public boolean isRealColor() {
            return Stream.of(ACTUAL_COLORS).anyMatch(c -> c == this);
        }

        public boolean isTypeNotColor() {
            return Stream.of(NOT_COLORS).anyMatch(c -> c == this);
        }
    }
}

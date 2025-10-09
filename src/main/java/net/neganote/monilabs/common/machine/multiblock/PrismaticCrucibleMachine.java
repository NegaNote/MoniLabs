package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
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

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class PrismaticCrucibleMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            PrismaticCrucibleMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    @DescSynced
    private Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    @Getter
    @Persisted
    @DescSynced
    private Color color;

    @Getter
    @DescSynced
    @Persisted
    private BlockPos focusPos;

    private BlockPos structMin;

    private BlockPos structMax;

    private boolean updatingBlocksFromOnFormed = false;

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

        // The prismac can occasionally decide to run this method before it has synced its color,
        // so this boolean is a way around that
        //
        updatingBlocksFromOnFormed = true;
        updateActiveBlocks(true);
        updatingBlocksFromOnFormed = false;
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
        updateActiveBlocks(true);
    }

    @Override
    public void updateActiveBlocks(boolean active) {
        if (activeBlocks != null) {
            for (long pos : activeBlocks) {
                var blockPos = BlockPos.of(pos);
                var blockState = Objects.requireNonNull(getLevel()).getBlockState(blockPos);
                if (blockState.hasProperty(GTBlockStateProperties.ACTIVE)) {
                    var newState = blockState.setValue(GTBlockStateProperties.ACTIVE, isFormed());
                    // Prevents color from desyncing
                    if (!updatingBlocksFromOnFormed) {
                        newState = newState.setValue(PrismaticActiveBlock.COLOR, color.key);
                    } else {
                        // Yes this is janky, but it ensures that the prismac doesn't randomly decide it wants to
                        // reset its color on world load for no reason
                        color = Color.getColorFromKey(blockState.getValue(PrismaticActiveBlock.COLOR));
                    }
                    if (!Objects.equals(blockState, newState)) {
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
                            var newState = blockState.setValue(GTBlockStateProperties.ACTIVE, false);
                            if (!Objects.equals(blockState, newState)) {
                                getLevel().setBlockAndUpdate(blockPos, newState);
                            }
                        }
                    }
                }
            }
        }
    }

    // Stolen from LargeChemicalBathMachine
    public void saveOffsets() {
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
}

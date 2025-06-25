package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IRedstoneSignalMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.block.PrismaticActiveBlock;
import net.neganote.monilabs.common.machine.part.ChromaSensorHatchPartMachine;
import net.neganote.monilabs.common.machine.part.SculkExperienceDrainingHatchPartMachine;
import net.neganote.monilabs.common.machine.part.SculkExperienceSensorHatchPartMachine;
import net.neganote.monilabs.common.machine.trait.NotifiableChromaContainer;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class MicroverseProjectorMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler microverseHandler;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MicroverseProjectorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Getter
    @Persisted
    private final Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    // Currently running recipe. Needs to be cached separately for a specific behavior
    @Persisted
    @DescSynced
    private GTRecipe activeRecipe;
    // Used for microverse projector tier
    private final int tier;

    // Microverse type currently active
    @Persisted
    @DescSynced
    @Setter
    private Microverse microverse;

    // Current microverse integrity/"health"
    @Persisted
    @DescSynced
    private int microverseIntegrity;

    @Persisted
    @Getter
    private int timer = 0;

    // Constant for max health. Takes 500s (8m20s) to decay at a rate of 1/tick
    private final int MICROVERSE_MAX_INTEGRITY = 10000;
    private final int FLUX_REPAIR_AMOUNT = 100;


    public MicroverseProjectorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.tier = tier;
        this.microverseHandler = new ConditionalSubscriptionHandler(this, this::microverseTick, this::isFormed);
        updateMicroverse(0, false);
    }

    @Override
    @NotNull
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureInvalid() {
        updateMicroverse(0, false);
        super.onStructureInvalid();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        // TODO: Cache flux input bus for performance
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (microverseIntegrity == 0) return false;
        if (!recipe.data.contains("required_microverse") || recipe.data.getInt("required_microverse") != microverse.ordinal()) {
            return false;
        }
        if (super.beforeWorking(recipe)) {
            activeRecipe = recipeLogic.getLastRecipe();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onWorking(){
        if (!super.onWorking()) {
            activeRecipe = null;
            return false;
        } // Wondering when to calculate super.onWorking

        if (activeRecipe.data.contains("damage_rate")) {
            int decayRate = activeRecipe.data.getInt("damage_rate");
            microverseIntegrity -= decayRate;
            if (microverseIntegrity <= 0) {
                // TODO: Return the first input of the recipe in the output hatch
                // TODO OPTIONAL: Make this behavior configurable for evil deeds >:3
                if (microverse == Microverse.SHATTERED) {
                    microverseIntegrity = MICROVERSE_MAX_INTEGRITY >> 1; // start at half integrity
                    microverse = Microverse.CORRUPTED;
                } else {
                    microverseIntegrity = 0;
                    microverse = Microverse.NONE;
                }
                activeRecipe = null;
                return false;
            }
            if (microverseIntegrity >= MICROVERSE_MAX_INTEGRITY) microverseIntegrity = MICROVERSE_MAX_INTEGRITY;
        }
        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        if (activeRecipe.data.contains("updated_microverse")) {
            int updatedMicroverse = activeRecipe.data.getInt("updatedMicroverse");
            updateMicroverse(updatedMicroverse & 7, updatedMicroverse > 7);
        }
        activeRecipe = null;
    }

    public void microverseTick() {
        if (timer == 0 && microverse.isRepairable) {
            if (microverse.isHungry) {
                // TODO: eat flux and return amount as "fluxCount"
                int fluxCount = 1;

                int missingHealth = MICROVERSE_MAX_INTEGRITY - microverseIntegrity;
                if (fluxCount * 100 > missingHealth) {
                    microverseIntegrity = MICROVERSE_MAX_INTEGRITY;
                    int rollbackCount = (fluxCount * 100 - missingHealth) / 100; // number of excess flux (a half-useful flux is not excess)
                    if (activeRecipe != null && recipeLogic.getProgress() > 1) {
                        recipeLogic.setProgress(Math.max(1, recipeLogic.getProgress() - (20 * rollbackCount)));
                    }
                } else {
                    microverseIntegrity += fluxCount * 100;
                }
            } else {
                int missingHealth = MICROVERSE_MAX_INTEGRITY - microverseIntegrity;
                int missingFlux = missingHealth / 100;
                // TODO: determine available flux as "fluxCount", cap it at missingFlux
                // TODO: Also consume all flux, capped at missingFlux
                int fluxCount = 1;

                microverseIntegrity += fluxCount * 100;
            }
        }
        timer = (timer + 1) % 20;
    }

    private void updateMicroverse(int pKey, boolean keepIntegrity) {
        microverse = Microverse.getMicroverseFromKey(pKey);
        if (microverse == Microverse.NONE) {
            microverseIntegrity = 0;
        } else {
            microverseIntegrity = ( keepIntegrity ? microverseIntegrity : MICROVERSE_MAX_INTEGRITY );
        }
    }

    public enum Microverse {

        NONE(0, false, false),
        NORMAL(0, true, false),
        HOSTILE(2, false, false),
        SHATTERED(0, false, false),
        CORRUPTED(1, true, true);

        public static final Microverse[] MICROVERSES = Microverse.values();

        public final int decayRate;
        public final boolean isRepairable;

        public final boolean isHungry;

        Microverse(int decayRate, boolean isRepairable, boolean isHungry) {
            this.decayRate = decayRate;
            this.isRepairable = isRepairable;
            this.isHungry = isHungry;
        }

        public static MicroverseProjectorMachine.Microverse getMicroverseFromKey(int pKey) {
            return MICROVERSES[pKey];
        }

        /*
        @Override
        public String toString() {
            return "Color{" +
                    "nameKey='" + nameKey + '\'' +
                    '}';
        }
         */
    }
}

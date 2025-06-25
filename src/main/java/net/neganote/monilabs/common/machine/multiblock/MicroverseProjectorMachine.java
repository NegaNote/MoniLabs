package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IRedstoneSignalMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import lombok.Setter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.block.PrismaticActiveBlock;
import net.neganote.monilabs.common.machine.part.ChromaSensorHatchPartMachine;
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

    // Constant for max health. Takes 500s (8m20s) to decay at a rate of 1/tick
    private final int MICROVERSE_MAX_INTEGRITY = 10000;


    public MicroverseProjectorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.tier = tier;
        this.microverseHandler = null; // TODO later
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
                // Return the first input of the recipe in the output hatch
                // OPTIONAL: Make this behavior configurable for evil deeds >:3
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

    private void updateMicroverse(int pKey, boolean keepIntegrity) {
        microverse = Microverse.getMicroverseFromKey(pKey);
        if (microverse == Microverse.NONE) {
            microverseIntegrity = 0;
        } else {
            microverseIntegrity = ( keepIntegrity ? microverseIntegrity : MICROVERSE_MAX_INTEGRITY );
        }
    }

    public enum Microverse {

        NONE(0, false),
        NORMAL(0, true),
        HOSTILE(0, false),
        SHATTERED(0, false),
        CORRUPTED(0, true);

        public static final Microverse[] MICROVERSES = Microverse.values();

        public final int decayRate;
        public final boolean isRepairable;

        Microverse(int decayRate, boolean isRepairable) {
            this.decayRate = decayRate;
            this.isRepairable = isRepairable;
        }

        public static MicroverseProjectorMachine.Microverse getMicroverseFromKey(int pKey) {
            return MICROVERSES[pKey];
        }

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

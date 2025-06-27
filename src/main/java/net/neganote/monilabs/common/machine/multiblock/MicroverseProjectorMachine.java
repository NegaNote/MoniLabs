package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import net.neganote.monilabs.config.MoniConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@SuppressWarnings("unused")
public class MicroverseProjectorMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler microverseHandler;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MicroverseProjectorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private final Item quantumFluxItem;

    @Getter
    @Persisted
    private final Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    // Currently running recipe. Needs to be cached separately for a specific behavior
    @Persisted
    @DescSynced
    private GTRecipe activeRecipe;
    // Used for microverse projector tier
    @Getter
    private final int tier;

    // Microverse type currently active
    @Persisted
    @DescSynced
    @Setter
    @Getter
    private Microverse microverse;

    // Current microverse integrity/"health"
    @Persisted
    @DescSynced
    private int microverseIntegrity;

    @Persisted
    @Getter
    private int timer = 0;

    // Constant for max health. Takes 500s (8m20s) to decay at a rate of 1/tick
    private final int MICROVERSE_MAX_INTEGRITY = 100_000;
    private final int FLUX_REPAIR_AMOUNT = 1000;

    public MicroverseProjectorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.tier = tier;
        this.microverseHandler = new ConditionalSubscriptionHandler(this, this::microverseTick, this::isFormed);
        updateMicroverse(0, false);
        this.quantumFluxItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.of("kubejs:quantum_flux", ':'));
        assert this.quantumFluxItem != null;
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
        microverseHandler.updateSubscription();
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        microverseHandler.updateSubscription();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (microverseIntegrity == 0) return false;
        if (!recipe.data.contains("required_microverse") ||
                recipe.data.getInt("required_microverse") != microverse.ordinal()) {
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
    public boolean onWorking() {
        if (!super.onWorking()) {
            activeRecipe = null;
            return false;
        } // Wondering when to calculate super.onWorking

        if (activeRecipe.data.contains("damage_rate")) {
            int decayRate = activeRecipe.data.getInt("damage_rate");
            microverseIntegrity -= decayRate;
            if (microverseIntegrity <= 0) {
                if (MoniConfig.INSTANCE.values.microminerReturnedOnZeroIntegrity) {
                    var contents = (Ingredient) activeRecipe.getInputContents(ItemRecipeCapability.CAP).get(0)
                            .getContent();
                    List<Ingredient> left = List.of(contents);

                    var outputBuses = getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP).stream()
                            .filter(NotifiableItemStackHandler.class::isInstance)
                            .map(NotifiableItemStackHandler.class::cast)
                            .toList();
                    for (var outputBus : outputBuses) {
                        left = outputBus.handleRecipe(IO.OUT, null, left, false);
                        if (left == null) {
                            break;
                        }
                    }
                }

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
            int updatedMicroverse = activeRecipe.data.getInt("updated_microverse");
            updateMicroverse(updatedMicroverse, activeRecipe.data.getBoolean("keep_integrity"));
        }
        activeRecipe = null;
    }

    public void microverseTick() {
        if (timer == 0 && microverse.isRepairable) {
            var itemHandlers = getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
            if (microverse.isHungry) {
                int fluxCount = 0;

                for (var itemHandler : itemHandlers) {
                    for (int i = 0; i < itemHandler.getSlots(); i++) {
                        var stack = itemHandler.getStackInSlot(i);
                        if (stack.getItem() == quantumFluxItem) {
                            fluxCount += stack.getCount();
                            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
                        }
                    }
                }

                int missingHealth = MICROVERSE_MAX_INTEGRITY - microverseIntegrity;
                if (fluxCount * 100 > missingHealth) {
                    microverseIntegrity = MICROVERSE_MAX_INTEGRITY;
                    int rollbackCount = (fluxCount * 100 - missingHealth) / 100; // number of excess flux (a half-useful
                                                                                 // flux is not excess)
                    if (activeRecipe != null && recipeLogic.getProgress() > 1) {
                        recipeLogic.setProgress(Math.max(1, recipeLogic.getProgress() - (20 * rollbackCount)));
                    }
                } else {
                    microverseIntegrity += fluxCount * 100;
                }
            } else {
                int missingHealth = MICROVERSE_MAX_INTEGRITY - microverseIntegrity;
                int missingFlux = missingHealth / 100;
                int acc = missingFlux;
                List<Ingredient> fluxList = new ObjectArrayList<>();
                while (acc >= 64) {
                    fluxList.add(Ingredient.of(new ItemStack(quantumFluxItem, 64)));
                    acc -= 64;
                }
                if (acc != 0) {
                    fluxList.add(Ingredient.of(new ItemStack(quantumFluxItem, acc)));
                }
                for (var itemHandler : itemHandlers) {
                    fluxList = itemHandler.handleRecipe(IO.IN, null, fluxList, false);
                    if (fluxList == null) {
                        break;
                    }
                }

                int fluxCount = missingFlux;
                if (fluxList != null) {
                    for (Ingredient ingredient : fluxList) {
                        var items = ingredient.getItems();
                        for (ItemStack item : items) {
                            fluxCount -= item.getCount();
                        }
                    }
                }

                microverseIntegrity += fluxCount * 100;
            }
        }
        timer = (timer + 1) % 20;
        if (microverse.decayRate != 0) {
            microverseIntegrity -= microverse.decayRate;
            if (microverseIntegrity <= 0) {
                updateMicroverse(0, false);
            }
        }
    }

    private void updateMicroverse(int pKey, boolean keepIntegrity) {
        microverse = Microverse.getMicroverseFromKey(pKey);
        if (microverse == Microverse.NONE) {
            microverseIntegrity = 0;
        } else {
            microverseIntegrity = (keepIntegrity ? microverseIntegrity : MICROVERSE_MAX_INTEGRITY);
        }
    }

    public enum Microverse {

        NONE(0, false, false),
        NORMAL(0, true, false),
        HOSTILE(20, false, false),
        SHATTERED(0, false, false),
        CORRUPTED(10, true, true);

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
         * @Override
         * public String toString() {
         * return "Color{" +
         * "nameKey='" + nameKey + '\'' +
         * '}';
         * }
         */
    }
}

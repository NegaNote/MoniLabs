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
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.config.MoniConfig;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
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
    private final int projectorTier;

    // Microverse type currently active
    @Persisted
    @DescSynced
    @Setter
    @Getter
    @RequireRerender
    private Microverse microverse;

    // Current microverse integrity/"health"
    @Persisted
    @DescSynced
    @Getter
    private int microverseIntegrity;

    @Persisted
    @Getter
    private int timer = 0;

    private List<NotifiableItemStackHandler> inputBuses = null;
    private List<NotifiableItemStackHandler> outputBuses = null;

    // Constant for max health. Takes 500s (8m20s) to decay at a rate of 1/tick
    public static final int MICROVERSE_MAX_INTEGRITY = 100_000;
    public static final int FLUX_REPAIR_AMOUNT = 1000;

    public MicroverseProjectorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.projectorTier = tier;
        this.microverseHandler = new ConditionalSubscriptionHandler(this, this::microverseTick, this::isFormed);
        updateMicroverse(0, false);
        this.quantumFluxItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.bySeparator("kubejs:quantum_flux", ':'));
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
        inputBuses = null;
        outputBuses = null;
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
    }

    @Override
    public void onStructureFormed() {
        if (inputBuses == null) {
            inputBuses = getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
        }
        if (outputBuses == null && MoniConfig.INSTANCE.values.microminerReturnedOnZeroIntegrity) {
            outputBuses = getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
        }
        super.onStructureFormed();
        microverseHandler.updateSubscription();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;
        if (microverseIntegrity == 0 && microverse != Microverse.NONE) return false;
        if (recipe.data.contains("required_microverse") &&
                recipe.data.getInt("required_microverse") != microverse.ordinal()) {
            return false;
        }
        if (recipe.data.contains("projector_tier") && recipe.data.getLong("projector_tier") > projectorTier) {
            return false;
        }
        if (super.beforeWorking(recipe)) {
            activeRecipe = recipe;
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

        if (activeRecipe != null && activeRecipe.data.contains("damage_rate")) {
            int decayRate = activeRecipe.data.getInt("damage_rate");
            microverseIntegrity = Math.max(microverseIntegrity - decayRate * activeRecipe.parallels, 0);
            if (microverseIntegrity == 0 && microverse != Microverse.NONE) {
                if (MoniConfig.INSTANCE.values.microminerReturnedOnZeroIntegrity) {
                    var contents = (Ingredient) activeRecipe.getInputContents(ItemRecipeCapability.CAP).get(0)
                            .getContent();
                    List<Ingredient> left = List.of(contents);
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
            if (microverse.isHungry) {
                int fluxCount = 0;

                for (var itemHandler : inputBuses) {
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
                    int rollbackCount = (fluxCount * 100 - missingHealth) / 100; // number of excess flux --
                                                                                 // a half-useful flux is not excess
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
                for (var itemHandler : inputBuses) {
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
            int decayRate = microverse.decayRate;
            if (activeRecipe != null) {
                decayRate *= activeRecipe.parallels;
            }
            microverseIntegrity -= decayRate;
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

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("microverse.monilabs.current_microverse",
                    Component.translatable(microverse.langKey)));
            if (microverse != Microverse.NONE) {
                textList.add(Component.translatable("microverse.monilabs.integrity",
                        (float) microverseIntegrity / FLUX_REPAIR_AMOUNT));
            }
        }
    }
}

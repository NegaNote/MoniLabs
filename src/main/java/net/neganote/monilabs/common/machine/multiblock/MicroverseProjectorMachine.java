package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.ingredient.SizedIngredient;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.data.recipe.builder.GTRecipeBuilder;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.annotation.UpdateListener;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.item.MoniItems;
import net.neganote.monilabs.common.machine.trait.NotifiableMicroverseContainer;
import net.neganote.monilabs.config.MoniConfig;
import net.neganote.monilabs.data.tags.MoniTags;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class MicroverseProjectorMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler microverseHandler;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            MicroverseProjectorMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private final Item quantumFluxItem;

    private final Item universeDataItem;

    @Getter
    @Persisted
    private final Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    // Used for microverse projector tier
    @Getter
    private final int projectorTier;

    // Microverse type currently active
    @Persisted
    @DescSynced
    @Setter
    @Getter
    @RequireRerender
    @UpdateListener(methodName = "onMicroverseChange")
    private Microverse microverse;

    // Current microverse integrity/"health"
    @Persisted
    @DescSynced
    @Getter
    private int microverseIntegrity;

    @Persisted
    @DescSynced
    @Getter
    private int stabilizationProgress;

    private List<NotifiableItemStackHandler> inputBuses = null;
    private List<NotifiableItemStackHandler> outputBuses = null;

    // Constant for max health. Takes 500s (8m20s) to decay at a rate of 1/tick
    public static final int MICROVERSE_MAX_INTEGRITY = 100_000;
    public static final int FLUX_REPAIR_AMOUNT = 1000;

    private final GTRecipe quantumFluxRecipe;
    private final GTRecipe causalityCollapserRecipe;
    private final GTRecipe causalityShardRecipe;
    private final GTRecipe universeDataRecipe;

    private ItemStack targetItem;

    @Persisted
    private final NotifiableMicroverseContainer microverseContainer;

    public MicroverseProjectorMachine(IMachineBlockEntity holder, int tier, Object... args) {
        super(holder, args);
        this.projectorTier = tier;
        this.microverseHandler = new ConditionalSubscriptionHandler(this, this::microverseTick, this::isFormed);
        this.quantumFluxItem = ForgeRegistries.ITEMS.getValue(ResourceLocation.bySeparator("kubejs:quantum_flux", ':'));
        assert this.quantumFluxItem != null;
        this.quantumFluxRecipe = GTRecipeBuilder.ofRaw().inputItems(this.quantumFluxItem).buildRawRecipe();
        this.microverseContainer = new NotifiableMicroverseContainer(this);

        updateMicroverse(0, false);

        // Insanity Mode setup
        this.universeDataItem = ForgeRegistries.ITEMS
                .getValue(ResourceLocation.bySeparator("kubejs:universe_creation_data", ':'));
        assert this.universeDataItem != null;
        this.causalityCollapserRecipe = GTRecipeBuilder.ofRaw().inputItems(MoniItems.CAUSALITY_COLLAPSER)
                .buildRawRecipe();
        this.causalityShardRecipe = GTRecipeBuilder.ofRaw().outputItems(MoniItems.SHARD_OF_CAUSALITY).buildRawRecipe();
        this.universeDataRecipe = GTRecipeBuilder.ofRaw().inputItems(this.universeDataItem).buildRawRecipe();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        microverseHandler.updateSubscription();
        inputBuses = null;
        outputBuses = null;
        if (microverse == Microverse.DEGENERATE) {
            failMicroverse("loss of structural integrity in the Microverse Projector");
        }
        if (microverse.decayRate != 0) {
            updateMicroverse(0, false);
        }
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
        if (microverseIntegrity == 0 && microverse != Microverse.NONE) return false;
        // Override projector tier check if Degenerate
        if (microverse != Microverse.DEGENERATE) {
            if (recipe.data.contains("projector_tier") && recipe.data.getLong("projector_tier") > projectorTier) {
                RecipeLogic.putFailureReason(this, recipe,
                        Component.translatable("monilabs.failure_reason.insufficient_projector_tier"));
                return false;
            }
        }
        return super.beforeWorking(recipe);
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) {
            return false;
        }

        if (outputBuses == null || outputBuses.isEmpty()) {
            outputBuses = getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
        }

        var activeRecipe = recipeLogic.getLastRecipe();

        // Shards Of Causality logic
        if (activeRecipe != null && microverse == Microverse.DEGENERATE) {
            if (stabilizationProgress >= 0) {
                List<ItemStack> outputs = RecipeHelper.getOutputItems(activeRecipe);
                for (ItemStack output : outputs) {
                    if (output.is(targetItem.getItem())) {
                        advanceStabilization();
                        abortMission(activeRecipe);
                        return false;
                    }
                }
            }
            // Fallback if not actively stabilizing or if wrong recipe is running
            abortMission(activeRecipe);
            failMicroverse("dangerous interference with Microverse's Causality Flux");
            return false;
        }

        if (activeRecipe != null && (activeRecipe.data.contains("damage_rate"))) {
            int decayRate = activeRecipe.data.getInt("damage_rate");
            decayRate *= activeRecipe.parallels;

            var originalDuration = activeRecipe.data.getInt("duration");

            var durationDifference = originalDuration / activeRecipe.duration;
            decayRate *= durationDifference;

            microverseIntegrity = Mth.clamp(microverseIntegrity - decayRate, 0, MICROVERSE_MAX_INTEGRITY);

            boolean microverseChanged = doIntegrityTypeTransitions();
            if (microverseChanged) {
                abortMission(activeRecipe);
                return false;
            }
        }
        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();
        var activeRecipe = recipeLogic.getLastRecipe();
        if (activeRecipe != null && activeRecipe.data.contains("updated_microverse")) {
            int updatedMicroverse = activeRecipe.data.getInt("updated_microverse");
            updateMicroverse(updatedMicroverse, activeRecipe.data.getBoolean("keep_integrity"));
        }
    }

    public void microverseTick() {
        if (inputBuses == null || inputBuses.isEmpty()) {
            inputBuses = getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
        }
        if (MoniConfig.INSTANCE.values.doComplexMicroverses && microverse != Microverse.NONE) {
            // Attempt to consume a Causality Collapser
            var collapsersAvailable = ParallelLogic.getMaxByInput(this, causalityCollapserRecipe, 1,
                    Collections.emptyList());

            if (collapsersAvailable > 0) {
                List<Ingredient> inputList = ObjectArrayList
                        .of(SizedIngredient.create(new ItemStack(MoniItems.CAUSALITY_COLLAPSER, 1)));

                for (var bus : inputBuses) {
                    inputList = bus.handleRecipe(IO.IN, causalityCollapserRecipe, inputList, false);
                    if (inputList == null) break;
                }

                if (microverse == Microverse.DEGENERATE) {
                    if (stabilizationProgress == -1) {
                        // Initiate Stabilization sequence
                        advanceStabilization();
                    } else {
                        failMicroverse("uncontrolled collapse of the Causality Flux");
                    }
                } else {
                    updateMicroverse(0, false);
                }
            }

            // If not stabilizing, attempt to consume a data to clear the 'verse
            if (microverse == Microverse.DEGENERATE && stabilizationProgress == -1) {
                int dataAvailable = ParallelLogic.getMaxByInput(this, universeDataRecipe, 1,
                        Collections.emptyList());

                if (dataAvailable > 0) {
                    List<Ingredient> inputList = ObjectArrayList
                            .of(SizedIngredient.create(new ItemStack(this.universeDataItem, 1)));

                    for (var bus : inputBuses) {
                        inputList = bus.handleRecipe(IO.IN, universeDataRecipe, inputList, false);
                        if (inputList == null) break;
                    }

                    updateMicroverse(0, false);
                }
            }
        }

        if (microverse.isRepairable) {
            var missingHealth = MICROVERSE_MAX_INTEGRITY - microverseIntegrity;
            var fluxToFullHeal = missingHealth / FLUX_REPAIR_AMOUNT;
            var fluxAvailable = ParallelLogic.getMaxByInput(this, quantumFluxRecipe, Integer.MAX_VALUE,
                    Collections.emptyList());

            var fluxToConsume = microverse.isHungry ? fluxAvailable : Math.min(fluxToFullHeal, fluxAvailable);

            if (fluxToConsume > 0) {
                List<Ingredient> fluxList = ObjectArrayList
                        .of(SizedIngredient.create(new ItemStack(quantumFluxItem, fluxToConsume)));

                var scaledRecipe = quantumFluxRecipe.copy(new ContentModifier(fluxToConsume, 0.0));

                for (var bus : inputBuses) {
                    fluxList = bus.handleRecipe(IO.IN, scaledRecipe, fluxList, false);
                    if (fluxList == null) break;
                }

                var usedToHeal = Math.min(fluxToFullHeal, fluxToConsume);
                microverseIntegrity += usedToHeal * FLUX_REPAIR_AMOUNT;

                if (microverse.isHungry && fluxToConsume > usedToHeal) {
                    microverseIntegrity = MICROVERSE_MAX_INTEGRITY;
                    if (microverse == Microverse.CORRUPTED) {
                        updateMicroverse(7, 50);
                        abortMission(recipeLogic.getLastRecipe());
                    } else {
                        int rollbackCount = fluxToConsume - usedToHeal;
                        if (recipeLogic.getLastRecipe() != null && recipeLogic.getProgress() > 1) {
                            recipeLogic.setProgress(Math.max(1, recipeLogic.getProgress() - (20 * rollbackCount)));
                        }
                    }
                }
            }
        }
        if (microverse.decayRate != 0) {
            int decayRate = microverse.decayRate;
            microverseIntegrity -= decayRate;
        }

        // Perform type transitions based on integrity.
        // Reset recipe logic if type transition leads to a change in microverse.
        boolean microverseChanged = doIntegrityTypeTransitions();
        if (microverseChanged) {
            abortMission(recipeLogic.getLastRecipe());
        }
    }

    private void abortMission(@Nullable GTRecipe activeRecipe) {
        if (activeRecipe != null && MoniConfig.INSTANCE.values.microminerReturnedOnZeroIntegrity) {
            var contents = (Ingredient) activeRecipe.getInputContents(ItemRecipeCapability.CAP).get(0)
                    .getContent();
            List<Ingredient> left = List.of(contents);
            for (var outputBus : outputBuses) {
                left = outputBus.handleRecipe(IO.OUT, activeRecipe, left, false);
                if (left == null) {
                    break;
                }
            }
        }
        recipeLogic.resetRecipeLogic();
        recipeLogic.markLastRecipeDirty();
    }

    private void advanceStabilization() {
        if (outputBuses == null || outputBuses.isEmpty()) {
            outputBuses = getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
        }
        stabilizationProgress++;

        if (Math.random() <= Math.pow(stabilizationProgress / 5.0, 2)) {
            // Success, you gain one (1) shard of causality.
            List<Ingredient> outputList = ObjectArrayList
                    .of(SizedIngredient.create(new ItemStack(MoniItems.SHARD_OF_CAUSALITY, 1)));

            for (var bus : outputBuses) {
                outputList = bus.handleRecipe(IO.OUT, causalityShardRecipe, outputList, false);
                if (outputList == null) break;
            }

            updateMicroverse(1, 1);
        } else {
            // Restore 10% of the Microverse (5 seconds)
            microverseIntegrity = Mth.clamp(microverseIntegrity + 10_000, 0, MICROVERSE_MAX_INTEGRITY);

            // Compute next target
            Optional<Item> potentialItem = ForgeRegistries.ITEMS.tags()
                    .getTag(MoniTags.DEGENERATE_MICROVERSE_REQUESTABLE)
                    .getRandomElement(RandomSource.create());
            if (potentialItem.isPresent()) {
                targetItem = new ItemStack(potentialItem.get(), 1);

                // Output target item
                List<Ingredient> outputList = ObjectArrayList
                        .of(SizedIngredient.create(targetItem));

                GTRecipe outputItemRecipe = GTRecipeBuilder.ofRaw().outputItems(targetItem).buildRawRecipe();

                for (var bus : outputBuses) {
                    outputList = bus.handleRecipe(IO.OUT, outputItemRecipe, outputList, false);
                    if (outputList == null) break;
                }
            } else {
                // Empty tag, can't stabilize
                failMicroverse("empty tag " + MoniTags.DEGENERATE_MICROVERSE_REQUESTABLE.toString() +
                        ". Please report the issue to the dev team");
            }
        }
    }

    private void failMicroverse(String cause) {
        updateMicroverse(0, false);

        BlockPos pos = this.getPos();
        Level level = this.self().getLevel();

        var state = this.getMultiblockState();
        if (state != null && this.isFormed()) {
            for (BlockPos structurePos : state.getCache()) {
                var blockState = level.getBlockState(structurePos);

                if (blockState.is(MoniBlocks.MICROVERSE_CASING.get())) {
                    level.removeBlock(structurePos, false);
                }
            }
        }

        // Commented out for the testers' sanity.
        // throw new IllegalMicroverseException("Failed to contain degenerate Microverse at "
        // + this.getPos().toString()
        // + ", caused by " + cause + ".");

        level.removeBlock(pos, false);
    }



    private boolean doIntegrityTypeTransitions() {
        microverseIntegrity = Mth.clamp(microverseIntegrity, 0, MICROVERSE_MAX_INTEGRITY);
        if (microverseIntegrity <= 0) {
            switch (microverse) {
                case NONE -> {
                    return false;
                }

                case NORMAL -> {
                    if (MoniConfig.INSTANCE.values.doComplexMicroverses) {
                        updateMicroverse(3, (int) ((Math.random() + 3) * MICROVERSE_MAX_INTEGRITY) >> 2);
                        // Shattered, between 3/4 and full integrity
                    } else {
                        updateMicroverse(0, false);
                        // None
                    }
                    return true;
                }

                case SHATTERED, NECROSED -> {
                    updateMicroverse(4, MICROVERSE_MAX_INTEGRITY >> 1);
                    // Corrupted, half integrity
                    return true;
                }

                case CORRUPTED -> {
                    if (MoniConfig.INSTANCE.values.doComplexMicroverses) {
                        updateMicroverse(8, false);
                        // Degenerate, full countdown
                    } else {
                        updateMicroverse(0, false);
                        // None
                    }
                    return true;
                }

                case HOSTILE -> {
                    if (MoniConfig.INSTANCE.values.doComplexMicroverses) {
                        updateMicroverse(5, false);
                        // Abyssal, full integrity
                    } else {
                        updateMicroverse(0, false);
                        // None
                    }
                    return true;
                }

                case ABYSSAL, SUPERCHARGED -> {
                    updateMicroverse(8, false);
                    // Degenerate, full countdown
                    return true;
                }

                case DEGENERATE -> {
                    failMicroverse("rift in the Microverse's Causality Flux entering critical state");
                }
            }
        } else if (microverseIntegrity >= MICROVERSE_MAX_INTEGRITY) {
            // COMPLEX MICROVERSES ONLY
            if (MoniConfig.INSTANCE.values.doComplexMicroverses) {
                switch (microverse) {
                    case HOSTILE -> {
                        updateMicroverse(1, MICROVERSE_MAX_INTEGRITY / 10);
                        // Normal, 10%
                        return true;
                    }

                    case CORRUPTED -> {
                        updateMicroverse(7, 50);
                        // Supercharged, ~0%
                        return true;
                    }

                    case ABYSSAL -> {
                        updateMicroverse(2, MICROVERSE_MAX_INTEGRITY / 10);
                        // Hostile, 10%
                        return true;
                    }

                    case SUPERCHARGED -> {
                        updateMicroverse(0, false);
                        explode(4 * tier);
                        // Boom :3
                        return true;
                    }

                    default -> {
                        return false;
                    }
                }
            }
        }
        return false;
    }

    private void updateMicroverse(int pKey, boolean keepIntegrity) {
        this.updateMicroverse(pKey, MICROVERSE_MAX_INTEGRITY, keepIntegrity);
    }

    private void updateMicroverse(int pKey, int integrity) {
        this.updateMicroverse(pKey, integrity, false);
    }

    private void updateMicroverse(int pKey, int integrity, boolean keepIntegrity) {
        microverse = Microverse.getMicroverseFromKey(pKey);
        if (microverse == Microverse.NONE) {
            microverseIntegrity = 0;
        } else {
            microverseIntegrity = (keepIntegrity ? microverseIntegrity : integrity);
        }
        if (pKey != 8) {
            stabilizationProgress = -1;
            targetItem = null;
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
                textList.add(Component.translatable("microverse.monilabs.debug.0",
                        (double) stabilizationProgress));
                if (targetItem != null) {
                    textList.add(Component.translatable("microverse.monilabs.debug.1",
                            targetItem.getItem().toString()));
                }
            }
        }
    }

    public void onMicroverseChange(Microverse oldMicroverse, Microverse newMicroverse) {
        scheduleRenderUpdate();
    }

    public void explode(float explosionPower) {
        BlockPos pos = this.getPos();
        MetaMachine machine = this.self();
        Level level = machine.getLevel();
        level.removeBlock(pos, false);
        level.explode((Entity) null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5,
                explosionPower, ConfigHolder.INSTANCE.machines.doesExplosionDamagesTerrain ?
                        Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
    }
}

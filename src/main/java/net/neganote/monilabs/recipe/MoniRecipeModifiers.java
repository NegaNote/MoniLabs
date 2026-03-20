package net.neganote.monilabs.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic.*;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;
import com.gregtechceu.gtceu.utils.GTUtil;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;
import net.neganote.monilabs.common.machine.multiblock.VirtualParticleSynthesizerMachine;
import net.neganote.monilabs.config.MoniConfig;

import java.util.*;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("unused")
@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class MoniRecipeModifiers {

    public static ModifierFunction sculkVatRecipeModifier(MetaMachine machine, GTRecipe recipe) {
        if (machine instanceof SculkVatMachine sculkVat) {
            var tanks = sculkVat.getCapabilitiesFlat(IO.OUT, GTRecipeCapabilities.FLUID)
                    .stream()
                    .filter(NotifiableFluidTank.class::isInstance)
                    .map(NotifiableFluidTank.class::cast)
                    .toList();
            var stored = tanks.get(0).getFluidInTank(0).getAmount();
            var capacity = tanks.get(0).getTankCapacity(0);
            double x = (double) stored / capacity;
            double expMod = Math.log(MoniConfig.INSTANCE.values.sculkVatEfficiencyMultiplier) * 2.0;
            double modifier = Math.pow(1.0 / Math.exp(expMod * Math.pow((x - 0.5), 2.0)), 2.0);
            return ModifierFunction.builder()
                    .outputModifier(new ContentModifier(modifier, 0.0))
                    .build();
        } else {
            return RecipeModifier.nullWrongType(SculkVatMachine.class, machine);
        }
    }

    public static RecipeModifier omnicSynthRecipeModifier() {
        return (metaMachine, gtRecipe) -> {
            if (metaMachine instanceof OmnicSynthesizerMachine omnic) {
                Item item = RecipeHelper.getInputItems(gtRecipe).get(0).getItem();
                double multiplier;
                if (!omnic.recipeModifierCalculated) {
                    boolean found = true;
                    int index = omnic.diversityList.indexOf(item);
                    if (index < 0) {
                        found = false;
                        index = omnic.diversityList.size();
                    }

                    omnic.diversityPoints += (int) Math
                            .floor(Math.pow(index, MoniConfig.INSTANCE.values.omnicSynthesizerExponent));
                    multiplier = (double) omnic.diversityPoints / 100;
                    omnic.recipeModifierAmount = multiplier;
                    omnic.recipeModifierCalculated = true;
                    omnic.diversityPoints = omnic.diversityPoints % 100;

                    if (found) {
                        omnic.diversityList.remove(index);
                    }
                    omnic.diversityList.add(0, item);
                } else {
                    multiplier = omnic.recipeModifierAmount;
                }

                return ModifierFunction.builder()
                        .outputModifier(ContentModifier.multiplier(multiplier))
                        .build();
            }
            return RecipeModifier.nullWrongType(OmnicSynthesizerMachine.class, metaMachine);
        };
    }

    public static RecipeModifier MICROVERSE_OC = MoniRecipeModifiers::microverseOC;

    public static ModifierFunction microverseOC(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof MicroverseProjectorMachine projector)) {
            return RecipeModifier.nullWrongType(MicroverseProjectorMachine.class, machine);
        }
        if (RecipeHelper.getRecipeEUtTier(recipe) > projector.getTier()) {
            return ModifierFunction.cancel(Component.translatable("gtceu.recipe_modifier.insufficient_voltage"));
        }
        int projectorTier = projector.getProjectorTier();
        int recipeTier;
        if (recipe.data.contains("projector_tier")) {
            recipeTier = recipe.data.getByte("projector_tier");
        } else {
            recipeTier = 1;
        }
        int maxOCs = projector.getTier() - RecipeHelper.getRecipeEUtTier(recipe);
        OverclockingLogic logic = (p, v) -> microverseProjectorTierOC(p, v, projectorTier, recipeTier);
        return logic.getModifier(machine, recipe, projector.getOverclockVoltage());
    }

    // Heavily modeled after/copied from EBF OC logic but without subtick
    public static OCResult microverseProjectorTierOC(OCParams params, long maxVoltage, int projectorTier,
                                                     int recipeTier) {
        double duration = params.duration();
        double eut = params.eut();
        int ocAmount = params.ocAmount();

        double durationMultiplier = 1;

        int perfectOCAmount = projectorTier - recipeTier;

        int ocLevel = 0;
        while (ocAmount-- > 0) {
            boolean perfect = perfectOCAmount-- > 0;
            double potentialEUt = eut * OverclockingLogic.STD_VOLTAGE_FACTOR;
            if (potentialEUt > maxVoltage) break;
            double dFactor = perfect ? OverclockingLogic.PERFECT_DURATION_FACTOR :
                    OverclockingLogic.STD_DURATION_FACTOR;

            double potentialDuration = duration * dFactor;
            if (potentialDuration < 1) break;

            duration = potentialDuration;
            durationMultiplier *= dFactor;

            // Only set EUt after checking duration - no need to OC if duration would be too low
            eut = potentialEUt;
            ocLevel++;
        }

        return new OCResult(Math.pow(OverclockingLogic.STD_VOLTAGE_FACTOR, ocLevel), durationMultiplier, ocLevel, 1);
    }

    public static RecipeModifier MICROVERSE_PARALLEL_HATCH = MoniRecipeModifiers::hatchParallelMicroverse;

    // Identical to GTRecipeModifiers.hatchParallel, with the addition of the ability to blacklist parallels
    // on a per-recipe basis.
    public static ModifierFunction hatchParallelMicroverse(MetaMachine machine,
                                                           GTRecipe recipe) {
        if (recipe.data.contains("blacklistParallel") && recipe.data.getBoolean("blacklistParallel")) {
            return ModifierFunction.IDENTITY;
        }
        if (machine instanceof IMultiController controller && controller.isFormed()) {
            int parallels = controller.getParallelHatch()
                    .map(hatch -> ParallelLogic.getParallelAmount(machine, recipe, hatch.getCurrentParallel()))
                    .orElse(1);

            if (parallels == 1) return ModifierFunction.IDENTITY;
            return ModifierFunction.builder()
                    .modifyAllContents(ContentModifier.multiplier(parallels))
                    .eutMultiplier(parallels)
                    .parallels(parallels)
                    .build();
        }
        return ModifierFunction.IDENTITY;
    }

    public static RecipeModifier OC_AS_PARALLELS = MoniRecipeModifiers::greenhouseOCasParallels;

    public static ModifierFunction greenhouseOCasParallels(MetaMachine machine, GTRecipe recipe) {
        var workableMachine = (WorkableElectricMultiblockMachine) machine;

        OCParams params = new OCParams(RecipeHelper.getRealEUt(recipe).getTotalEU(), recipe.duration,
                GTUtil.getOCTierByVoltage(workableMachine.getOverclockVoltage()) -
                        GTUtil.getTierByVoltage(RecipeHelper.getRealEUt(recipe).getTotalEU()),
                Integer.MAX_VALUE);

        var ocResult = OverclockingLogic.NON_PERFECT_OVERCLOCK_SUBTICK.runOverclockingLogic(params,
                workableMachine.getOverclockVoltage());

        int parallels = ParallelLogic.getParallelAmount(machine, recipe,
                Mth.smallestEncompassingPowerOfTwo(ocResult.ocLevel()) * ocResult.parallels());

        if (parallels == 1) return ModifierFunction.IDENTITY;
        return ModifierFunction.builder()
                .modifyAllContents(ContentModifier.multiplier(parallels))
                .eutMultiplier(Math.pow(Mth.smallestEncompassingPowerOfTwo(parallels), 2))
                .parallels(parallels)
                .build();
    }

    public static ModifierFunction VirtualParticleSynthesisModifier(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof VirtualParticleSynthesizerMachine vps)) {
            return RecipeModifier.nullWrongType(VirtualParticleSynthesizerMachine.class, machine);
        }
        long seed; // Defined for the compiler in case you need the seed again later.
        long recipeHash; // Defined for the compiler.

        // Hash the recipe through its ID and the world seed.
        if (vps.getLevel() instanceof ServerLevel serverLevel) {
            seed = serverLevel.getSeed();

            // This order leads to less correlation between similar recipe IDs.
            String seededRecipeID = recipe.getId().toString().concat(String.valueOf(seed));
            recipeHash = hashString(seededRecipeID, 7L);
        } else {
            return ModifierFunction.IDENTITY;
        }

        // Init variables
        int noise = vps.getQuantumNoise();
        int targetNoise, diff;
        boolean recipeDirty = false;

        List<Content> recipeInputs;
        final Map<RecipeCapability<?>, List<Content>> newInputs = new HashMap<>(recipe.inputs);

        List<Content> recipeOutputs;
        final Map<RecipeCapability<?>, List<Content>> newOutputs = new HashMap<>(recipe.outputs);

        double nomnom = 1;

        if (recipe.data.contains("quantum_rule_input")) {
            String inputModifier = recipe.data.getString("quantum_rule_input");

            long inputHash = hashString("input", recipeHash) ^ seed;

            switch (inputModifier) {
                case "quantum_entanglement": {
                    // Target noise is the last 4 bits of the hash
                    targetNoise = (int) (inputHash & 0xF);

                    // Calculate modulo distance between random target and destination
                    diff = Mth.abs(noise - targetNoise);
                    diff = (diff <= 8 ? diff : 16 - diff);

                    boolean success = Math.random() >= diff / 8.0;

                    recipeInputs = new ArrayList<>(recipe.getInputContents(ItemRecipeCapability.CAP));

                    try {
                        // Try halving one of the two inputs
                        recipeInputs.set((success ? 0 : 1),
                                recipeInputs.get((success ? 0 : 1))
                                        .copy(ItemRecipeCapability.CAP, ContentModifier.multiplier(0.5)));
                    } catch (IndexOutOfBoundsException E) {
                        // Do nothing if the output doesn't exist lol
                    }

                    // Replace the Item Input list with a version of the list
                    newInputs.put(ItemRecipeCapability.CAP, recipeInputs);
                    recipeDirty = true;
                    break;
                }
                case "quantum_polarization": {
                    // Target noise is the last 4 bits of the hash
                    targetNoise = (int) (inputHash & 0xF);

                    // Calculate modulo distance between random target and destination
                    diff = Mth.abs(noise - targetNoise);
                    diff = (diff <= 8 ? diff : 16 - diff);

                    recipeInputs = new ArrayList<>(recipe.getInputContents(FluidRecipeCapability.CAP));

                    try {
                        recipeInputs.set(0,
                                recipeInputs.get(0)
                                        .copy(FluidRecipeCapability.CAP, ContentModifier.multiplier(0.5 + (0.5 - diff / 16.0))));
                    } catch (IndexOutOfBoundsException E) {
                        // Do nothing if the output doesn't exist lol
                    }
                    try {
                        recipeInputs.set(1,
                                recipeInputs.get(1)
                                        .copy(FluidRecipeCapability.CAP, ContentModifier.multiplier(0.5 + (diff / 16.0))));
                    } catch (IndexOutOfBoundsException E) {
                        // Do nothing if the output doesn't exist lol
                    }

                    // Replace the Item Input list with a version of the list
                    newInputs.put(FluidRecipeCapability.CAP, recipeInputs);
                    recipeDirty = true;
                    break;
                }
                case "quantum_fields" : {
                    recipeInputs = new ArrayList<>(recipe.getInputContents(ItemRecipeCapability.CAP));

                    int averageItems = 0;
                    for (int i = 0; i < recipeInputs.size(); i++) {
                        // Creates a new hash based on inputHash and ingredient index.
                        // Then, take a group of 4 bits based on current noise to serve as a multiplier.
                        int multiplier = (int) ((hashString(
                                String.valueOf(i) + "Needs 14 chars" + "-".repeat(i), inputHash) >>> (4 * noise)) &
                                0xF);
                        averageItems += multiplier;

                        // Modifies the current ingredient using the multiplier (ranging from 1/16x to 1x)
                        recipeInputs.set(i,
                                recipeInputs.get(i)
                                        .copy(ItemRecipeCapability.CAP,
                                                ContentModifier.multiplier((multiplier + 1) / 16.0)));
                    }

                    nomnom = ((int) ((averageItems / (double) recipeInputs.size()) + 1) / 16.0);

                    // Replace the Item Input list with a version of the list
                    newInputs.put(ItemRecipeCapability.CAP, recipeInputs);
                    recipeDirty = true;
                    break;
                }
                case "quantum_waves" : {
                    recipeInputs = new ArrayList<>(recipe.getInputContents(FluidRecipeCapability.CAP));

                    int averageFluids = 0;
                    for (int i = 0; i < recipeInputs.size(); i++) {
                        // Creates a new hash based on inputHash and ingredient index.
                        // Then, take a group of 4 bits based on current noise to serve as a multiplier.
                        int multiplier = (int) ((hashString(
                                String.valueOf(i) + "Needs 14 chars" + "-".repeat(i), inputHash) >>> (4 * noise)) &
                                0xF);
                        averageFluids += multiplier;

                        // Modifies the current ingredient using the multiplier (ranging from 1/16x to 1x)
                        recipeInputs.set(i,
                                recipeInputs.get(i)
                                        .copy(FluidRecipeCapability.CAP,
                                                ContentModifier.multiplier((multiplier + 1) / 16.0)));
                    }

                    nomnom = ((int) ((averageFluids / (double) recipeInputs.size()) + 1) / 16.0);

                    // Replace the Item Input list with a version of the list
                    newInputs.put(FluidRecipeCapability.CAP, recipeInputs);
                    recipeDirty = true;
                    break;
                }
            }
        }

        // Calculate modifier on recipe outputs
        if (recipe.data.contains("quantum_rule_output")) {
            String outputModifier = recipe.data.getString("quantum_rule_output");

            long outputHash = hashString("input", recipeHash) ^ seed;

            switch (outputModifier) {
                case "quantum_entanglement": {
                    // Target noise is the last 4 bits of the hash
                    targetNoise = (int) (outputHash & 0xF);

                    // Calculate modulo distance between random target and destination
                    diff = Mth.abs(noise - targetNoise);
                    diff = (diff <= 8 ? diff : 16 - diff);

                    recipeOutputs = new ArrayList<>(recipe.getOutputContents(ItemRecipeCapability.CAP));
                    // Modify Item Outputs accordingly
                    try {
                        // Try to remove failed item output
                        recipeOutputs.remove((Math.random() >= diff / 8.0 ? 1 : 0));
                    } catch (IndexOutOfBoundsException E) {
                        // Do nothing if there's nothing to remove lol
                    }

                    // Replace the Item Output list with a version of the list
                    newOutputs.put(ItemRecipeCapability.CAP, recipeOutputs);
                    recipeDirty = true;
                    break;
                }
                case "quantum_polarization": {
                    // Target noise is the last 4 bits of the hash
                    targetNoise = (int) (outputHash & 0xF);

                    // Calculate modulo distance between random target and destination
                    diff = Mth.abs(noise - targetNoise);
                    diff = (diff <= 8 ? diff : 16 - diff);

                    recipeOutputs = new ArrayList<>(recipe.getOutputContents(FluidRecipeCapability.CAP));
                    try {
                        recipeOutputs.set(0,
                                recipeOutputs.get(0)
                                        .copy(FluidRecipeCapability.CAP, ContentModifier.multiplier(1 - diff / 8.0)));
                    } catch (IndexOutOfBoundsException E) {
                        // Do nothing if the output doesn't exist lol
                    }
                    try {
                        recipeOutputs.set(1,
                                recipeOutputs.get(1)
                                        .copy(FluidRecipeCapability.CAP, ContentModifier.multiplier(diff / 8.0)));
                    } catch (IndexOutOfBoundsException E) {
                        // Do nothing if the output doesn't exist lol
                    }

                    // Replace the Fluid Output list with an updated version
                    newOutputs.put(FluidRecipeCapability.CAP, recipeOutputs);
                    recipeDirty = true;
                    break;
                }
                case "quantum_fields": {
                    recipeOutputs = new ArrayList<>(recipe.getOutputContents(ItemRecipeCapability.CAP));

                    // Calculate success for each item
                    for (int i = 0; i < recipeOutputs.size(); i++) {
                        int bitMap = hashToBitMap((int) (outputHash >>> 16 * i & 0xFFFF));

                        boolean success = (bitMap >>> noise & 1) != 0;

                        recipeOutputs.set(i,
                                recipeOutputs.get(i)
                                        .copy(ItemRecipeCapability.CAP, ContentModifier.multiplier((success ? 1 : 0))));
                    }

                    // Replace the Item Output list with an updated version
                    newOutputs.put(ItemRecipeCapability.CAP, recipeOutputs);
                    recipeDirty = true;
                    break;
                }
                case "quantum_waves": {
                    recipeOutputs = new ArrayList<>(recipe.getOutputContents(FluidRecipeCapability.CAP));

                    // Calculate success for each fluid
                    for (int i = 0; i < recipeOutputs.size(); i++) {
                        int bitMap = hashToBitMap((int) (outputHash >>> 16 * i & 0xFFFF));

                        boolean success = (bitMap >>> noise & 1) != 0;

                        recipeOutputs.set(i,
                                recipeOutputs.get(i)
                                        .copy(FluidRecipeCapability.CAP,
                                                ContentModifier.multiplier((success ? 1 : 0))));
                    }

                    // Replace the Fluid Output list with an updated version
                    newOutputs.put(FluidRecipeCapability.CAP, recipeOutputs);
                    recipeDirty = true;
                    break;
                }
            }
        }

        final double outputMultiplier = nomnom;
        if (recipeDirty) {
            return (inputRecipe) -> new GTRecipe(recipe.recipeType, recipe.id, newInputs,
                    ContentModifier.multiplier(outputMultiplier).applyContents(newOutputs),
                    new HashMap<>(recipe.tickInputs), new HashMap<>(recipe.tickOutputs),
                    new HashMap<>(recipe.inputChanceLogics), new HashMap<>(recipe.outputChanceLogics),
                    new HashMap<>(recipe.tickInputChanceLogics), new HashMap<>(recipe.tickOutputChanceLogics),
                    new ArrayList<>(recipe.conditions), new ArrayList<>(recipe.ingredientActions), recipe.data,
                    recipe.duration, recipe.recipeCategory, recipe.groupColor);
        } else {
            return ModifierFunction.IDENTITY;
        }
    }

    /*
     * Hashes the given string with the 31x + i method, but allows defining a starting value.
     */
    private static long hashString(String string, long startValue) {
        for (int i = 0; i < string.length(); i++) {
            startValue = startValue * 31L + string.charAt(i);
        }
        return startValue;
    }

    /*
     * Takes any 16-bit int and computes a 16-bit int with a Hamming Weight of exactly 4 from it.
     * 
     * @param x The input int (only least 16 bits are used)
     * 
     * @return int with HW 4
     */
    private static int hashToBitMap(int x) {
        int bitMap = 0;
        for (int j = 0; j < 4; j++) {
            int offset = x & 0xF;
            x = x >>> 4;
            while (true) {
                if ((bitMap >>> offset & 1) == 0) {
                    bitMap = bitMap | 1 << offset;
                    break;
                }
                offset = Math.floorMod(offset + 5, 16);
            }
        }
        return bitMap;
    }
}

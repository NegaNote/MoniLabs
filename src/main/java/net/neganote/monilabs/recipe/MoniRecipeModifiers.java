package net.neganote.monilabs.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic;
import com.gregtechceu.gtceu.api.recipe.OverclockingLogic.*;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.ParallelLogic;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;

import net.minecraft.util.valueproviders.FloatProvider;
import net.minecraft.util.valueproviders.UniformFloat;
import net.minecraft.world.item.Item;
import net.minecraftforge.fluids.FluidStack;
import net.neganote.monilabs.common.machine.multiblock.AntimatterGeneratorMachine;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;
import net.neganote.monilabs.config.MoniConfig;

import java.util.List;

@SuppressWarnings("unused")
public class MoniRecipeModifiers {

    public static RecipeModifier sculkVatRecipeModifier() {
        return (metaMachine, gtRecipe) -> {
            if (metaMachine instanceof WorkableElectricMultiblockMachine sculkVat) {
                var tanks = sculkVat.getCapabilitiesFlat(IO.OUT, GTRecipeCapabilities.FLUID)
                        .stream()
                        .filter(NotifiableFluidTank.class::isInstance)
                        .map(NotifiableFluidTank.class::cast)
                        .toList();
                double modifier = getSculkVatMultiplier(tanks);
                return ModifierFunction.builder()
                        .outputModifier(new ContentModifier(modifier, 0.0))
                        .build();
            } else {
                return ModifierFunction.IDENTITY;
            }
        };
    }

    private static double getSculkVatMultiplier(List<NotifiableFluidTank> tanks) {
        if (tanks.size() != 1) {
            throw new IllegalStateException("Sculk Vat must have exactly 1x fluid output hatch");
        }
        var fluidExportHatchTank = tanks.get(0);
        int capacity = fluidExportHatchTank.getTankCapacity(0);
        var contents = fluidExportHatchTank.getContents();
        int stored = 0;
        if (!contents.isEmpty()) {
            stored = ((FluidStack) contents.get(0)).getAmount();
        }
        double x = (double) stored / capacity;
        return Math.pow(1.0 / Math.exp(7.0 * Math.pow((x - 0.5), 2.0)), 2.0) * Math.exp(7.0 / 2.0);
    }

    public static RecipeModifier omnicSynthRecipeModifier() {
        return (metaMachine, gtRecipe) -> {
            if (metaMachine instanceof OmnicSynthesizerMachine omnic) {
                Item item = RecipeHelper.getInputItems(gtRecipe).get(0).getItem();
                double multiplier = 0.0;
                if (omnic.diversityList.contains(item)) {
                    int index = omnic.diversityList.indexOf(item);
                    omnic.diversityPoints += (int) Math
                            .floor(Math.pow(index, MoniConfig.INSTANCE.values.omnicSynthesizerExponent));
                    multiplier = (double) omnic.diversityPoints / 100;
                    omnic.diversityPoints = omnic.diversityPoints % 100;
                    Item temp = omnic.diversityList.remove(index);
                    omnic.diversityList.add(0, temp);
                } else {
                    omnic.diversityList.add(0, item);
                }
                return ModifierFunction.builder()
                        .outputModifier(ContentModifier.multiplier(multiplier))
                        .build();
            }
            return ModifierFunction.NULL;
        };
    }

    public static RecipeModifier antiMatterGeneratorRecipeModifier() {
        return (machine, recipe) -> {
            if (machine instanceof AntimatterGeneratorMachine antiGenerator) {
                var parallels = ParallelLogic.getParallelAmount(machine, recipe, 1000000000);
                if (parallels == 1) {
                    return ModifierFunction.IDENTITY;
                } else {
                    return ModifierFunction.builder()
                            .modifyAllContents(ContentModifier.multiplier(parallels))
                            .eutMultiplier(parallels)
                            .parallels(parallels)
                            .build();
                }
            } else {
                return ModifierFunction.IDENTITY;
            }
        };
    }

    public static RecipeModifier antiMatterManipulatorRecipeModifier() {
        return (machine, recipe) -> {
            float minInclusive = MoniConfig.INSTANCE.values.antimatterRandomMinInclusive;
            float maxExclusive = MoniConfig.INSTANCE.values.antimatterRandomMaxExclusive;
            FloatProvider rand = UniformFloat.of(minInclusive, maxExclusive);
            boolean shouldBeRandom = recipe.data.contains("antimatterRandom") &&
                    recipe.data.getBoolean("antimatterRandom");
            return ModifierFunction.builder()
                    .inputModifier(new ContentModifier(shouldBeRandom ? rand.sample(GTValues.RNG) : 1.0, 0.0))
                    .outputModifier(new ContentModifier(shouldBeRandom ? rand.sample(GTValues.RNG) : 1.0, 0.0))
                    .build();
        };
    }

    public static RecipeModifier MICROVERSE_OC = (machine, recipe) -> {
        if (!(machine instanceof MicroverseProjectorMachine projector)) {
            return RecipeModifier.nullWrongType(MicroverseProjectorMachine.class, machine);
        }
        int projectorTier = projector.getProjectorTier();
        if (!recipe.data.contains("projector_tier")) {
            return ModifierFunction.NULL;
        }
        int recipeTier = recipe.data.getByte("projector_tier");
        int maxOCs = projector.getTier() - RecipeHelper.getRecipeEUtTier(recipe);
        // MoniLabs.LOGGER.info("projectorTier: {}, recipeTier: {}, maxOCs: {}", projectorTier, recipeTier, maxOCs);
        OverclockingLogic logic = (p, v) -> microverseTierOC(projectorTier, recipeTier, maxOCs);
        return logic.getModifier(machine, recipe, projector.getOverclockVoltage());
    };

    public static OCResult microverseTierOC(int projectorTier, int recipeTier, int maxOCs) {
        int perfectOCAmount = Math.min(projectorTier - recipeTier, maxOCs);
        double durationMultiplier = Math.pow(OverclockingLogic.PERFECT_DURATION_FACTOR, perfectOCAmount);
        if (maxOCs > perfectOCAmount) {
            int normalOCAmount = maxOCs - perfectOCAmount;
            durationMultiplier *= Math.pow(OverclockingLogic.STD_DURATION_FACTOR, normalOCAmount);
            // MoniLabs.LOGGER.info("durationMultiplier: {}", durationMultiplier);
            return new OCResult(Math.pow(4, perfectOCAmount) * Math.pow(2, normalOCAmount), durationMultiplier, maxOCs,
                    1);
        } else {
            // MoniLabs.LOGGER.info("durationMultiplier: {}", durationMultiplier);
            return new OCResult(Math.pow(4, perfectOCAmount), durationMultiplier, perfectOCAmount, 1);
        }
    }
}

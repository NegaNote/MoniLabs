package net.neganote.monilabs.recipe;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
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
        double expMod = Math.log(MoniConfig.INSTANCE.values.sculkVatEfficiencyMultiplier) * 2.0;
        return Math.pow(1.0 / Math.exp(expMod * Math.pow((x - 0.5), 2.0)), 2.0);
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

    public static RecipeModifier MICROVERSE_OC = MoniRecipeModifiers::microverseOC;

    public static ModifierFunction microverseOC(MetaMachine machine, GTRecipe recipe) {
        if (!(machine instanceof MicroverseProjectorMachine projector)) {
            return RecipeModifier.nullWrongType(MicroverseProjectorMachine.class, machine);
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
}

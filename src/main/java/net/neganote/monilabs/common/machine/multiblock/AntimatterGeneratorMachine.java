package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.IEnergyContainer;
import com.gregtechceu.gtceu.api.capability.recipe.EURecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.transfer.fluid.IFluidHandlerModifiable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.config.MoniConfig;

import java.util.List;

@SuppressWarnings("unused")
public class AntimatterGeneratorMachine extends WorkableElectricMultiblockMachine {

    protected ConditionalSubscriptionHandler generationSubscription;
    protected Fluid antimatterFuelFluid;
    protected Fluid annihilatableMatterFluid;

    public AntimatterGeneratorMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.generationSubscription = new ConditionalSubscriptionHandler(this, this::generateEnergyTick,
                this::isFormed);
        this.antimatterFuelFluid = ForgeRegistries.FLUIDS
                .getValue(ResourceLocation.of(MoniConfig.INSTANCE.values.antimatterFuelID, ':'));
        assert this.antimatterFuelFluid != null : "antimatterFuelID is not a valid fluid ID";
        this.annihilatableMatterFluid = ForgeRegistries.FLUIDS
                .getValue(ResourceLocation.of(MoniConfig.INSTANCE.values.annihilatableMatterID, ':'));
        assert this.annihilatableMatterFluid != null : "annihilatableMatterID is not a valid fluid ID";
    }

    private void generateEnergyTick() {
        if (isWorkingEnabled()) {
            List<IFluidHandlerModifiable> hatches = getCapabilitiesFlat(IO.IN, FluidRecipeCapability.CAP)
                    .stream()
                    .filter(IFluidHandlerModifiable.class::isInstance)
                    .map(IFluidHandlerModifiable.class::cast)
                    .toList();

            assert hatches.size() == 2 : "There must be exactly 2 1x input hatches in the Antimatter Generator";
            assert hatches.get(0).getTanks() == 1 &&
                    hatches.get(1).getTanks() == 1 : "The hatches in the Antimatter Generator must be 1x";

            int tankWithFuel = -1;
            if (hatches.get(0).getFluidInTank(0).getFluid() == antimatterFuelFluid) {
                tankWithFuel = 0;
            } else if (hatches.get(1).getFluidInTank(0).getFluid() == antimatterFuelFluid) {
                tankWithFuel = 1;
            }
            if (tankWithFuel == -1) {
                voidFluids(hatches);
                return;
            }

            int tankWithMatter = tankWithFuel == 0 ? 1 : 0;
            if (hatches.get(tankWithMatter).getFluidInTank(0).getFluid() != annihilatableMatterFluid) {
                voidFluids(hatches);
                return;
            }

            int reactive = Math.min(hatches.get(0).getFluidInTank(0).getAmount(),
                    hatches.get(1).getFluidInTank(0).getAmount());

            double bonus = Math.log10(reactive);

            assert getCapabilitiesFlat(IO.OUT, EURecipeCapability.CAP).size() ==
                    1 : "There must be exactly 1 dynamo or laser source hatch on the Antimatter Generator";

            getCapabilitiesFlat(IO.OUT, EURecipeCapability.CAP).stream()
                    .filter(IEnergyContainer.class::isInstance)
                    .map(IEnergyContainer.class::cast)
                    .forEach(container -> container.addEnergy(
                            (long) (reactive * MoniConfig.INSTANCE.values.euPerAntimatterMillibucket * bonus)));
            voidFluids(hatches);
        }
    }

    private void voidFluids(List<IFluidHandlerModifiable> hatches) {
        for (var hatch : hatches) {
            hatch.setFluidInTank(0, FluidStack.EMPTY);
        }
    }
}

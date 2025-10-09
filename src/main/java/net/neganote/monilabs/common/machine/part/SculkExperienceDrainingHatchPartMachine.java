package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.common.machine.multiblock.part.FluidHatchPartMachine;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.config.MoniConfig;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class SculkExperienceDrainingHatchPartMachine extends FluidHatchPartMachine {

    public SculkExperienceDrainingHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.ZPM, IO.IN, FluidType.BUCKET_VOLUME, 1);
    }

    @Override
    protected @NotNull NotifiableFluidTank createTank(int initialCapacity, int slots, Object @NotNull... args) {
        return super.createTank(initialCapacity, slots).setFilter(fluidStack -> fluidStack.getFluid().isSame(
                Objects.requireNonNull(ForgeRegistries.FLUIDS
                        .getValue(ResourceLocation.bySeparator(MoniConfig.INSTANCE.values.sculkVatExperienceFluidID,
                                ':')))));
    }

    @Override
    public boolean swapIO() {
        return false;
    }
}

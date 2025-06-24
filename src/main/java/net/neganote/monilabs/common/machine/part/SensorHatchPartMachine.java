package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;

public class SensorHatchPartMachine extends TieredPartMachine {

    public SensorHatchPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
    }

    @Override
    public boolean canConnectRedstone(@NotNull Direction side) {
        return side == getFrontFacing();
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        updateSignal();
        super.removedFromController(controller);
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        updateSignal();
    }
}

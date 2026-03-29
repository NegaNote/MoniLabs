package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.VirtualParticleSynthesizerMachine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class QuantumNoiseSensorHatchPartMachine extends SensorHatchPartMachine {

    public QuantumNoiseSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.EV);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var controller = (VirtualParticleSynthesizerMachine) getController();

            if (controller == null) {
                return 0;
            }

            return controller.getQuantumNoise();
        } else {
            return 0;
        }
    }

    @Override
    public void updateSignal() {
        super.updateSignal();
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        super.removedFromController(controller);
    }
}

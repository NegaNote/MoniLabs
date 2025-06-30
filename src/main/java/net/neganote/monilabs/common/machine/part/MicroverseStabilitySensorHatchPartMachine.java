package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import org.jetbrains.annotations.Nullable;

public class MicroverseStabilitySensorHatchPartMachine extends SensorHatchPartMachine {

    public MicroverseStabilitySensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.HV);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing() || direction == getFrontFacing().getOpposite()) {
            var controllers = getControllers().stream().filter(MicroverseProjectorMachine.class::isInstance)
                    .map(MicroverseProjectorMachine.class::cast)
                    .toList();
            if (controllers.isEmpty()) {
                return 0;
            } else {
                var controller = controllers.get(0);
                int value = (int) (16 * controller.getMicroverseIntegrity() /
                        ((float) MicroverseProjectorMachine.MICROVERSE_MAX_INTEGRITY));
                return value == 16 ? 15 : value;
            }
        } else {
            return 0;
        }
    }
}

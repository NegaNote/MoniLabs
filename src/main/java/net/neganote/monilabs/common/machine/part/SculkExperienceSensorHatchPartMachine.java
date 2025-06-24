package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidType;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import org.jetbrains.annotations.Nullable;

public class SculkExperienceSensorHatchPartMachine extends SensorHatchPartMachine {

    public SculkExperienceSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.ZPM);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing() || direction == getFrontFacing().getOpposite()) {
            var controllers = getControllers().stream().filter(SculkVatMachine.class::isInstance)
                    .map(SculkVatMachine.class::cast)
                    .toList();
            if (controllers.isEmpty()) {
                return 0;
            } else {
                var controller = controllers.get(0);
                int value = (int) (16 * controller.getXpBuffer() / ((float) (FluidType.BUCKET_VOLUME << GTValues.ZPM)));
                return value == 16 ? 15 : value;
            }
        } else {
            return 0;
        }
    }
}

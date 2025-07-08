package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import java.util.Objects;

public class ChromaSensorHatchPartMachine extends SensorHatchPartMachine {

    public ChromaSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.UHV);
    }

    @Override
    public int getOutputSignal(Direction direction) {
        if (direction == getFrontFacing() || direction == getFrontFacing().getOpposite()) {
            var controllers = getControllers().stream().filter(PrismaticCrucibleMachine.class::isInstance)
                    .map(PrismaticCrucibleMachine.class::cast)
                    .toList();
            if (controllers.isEmpty()) {
                setRenderColor(RenderColor.NONE);
                return 0;
            } else {
                var controller = controllers.get(0);
                int signal = controller.isFormed() ? controller.getColorState().key + 1 : 0;
                setRenderColor(RenderColor.values()[signal]);
                return signal;
            }
        } else {
            return 0;
        }
    }

    void setRenderColor(RenderColor newRenderColor) {
        var oldRenderState = getRenderState();
        var newRenderState = oldRenderState.setValue(RenderColor.COLOR_PROPERTY, newRenderColor);
        if (!Objects.equals(oldRenderState, newRenderState)) {
            setRenderState(newRenderState);
        }
    }
}

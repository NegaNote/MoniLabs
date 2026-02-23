package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MicroverseStabilitySensorHatchPartMachine extends SensorHatchPartMachine {

    public MicroverseStabilitySensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.HV);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var controller = (MicroverseProjectorMachine) getController();
            if (controller == null) {
                return 0;
            }
            return Mth.clamp((int) (16 * controller.getMicroverseIntegrity() /
                    ((float) MicroverseProjectorMachine.MICROVERSE_MAX_INTEGRITY)), 0, 15);
        } else {
            return 0;
        }
    }

    @Override
    public void updateSignal() {
        super.updateSignal();
        var controller = (MicroverseProjectorMachine) getController();
        if (controller == null) {
            setRenderFillLevel(FillLevel.EMPTY_TO_QUARTER);
            return;
        }

        int value = Mth.clamp((int) (16 * controller.getMicroverseIntegrity() /
                ((float) MicroverseProjectorMachine.MICROVERSE_MAX_INTEGRITY)), 0, 15);

        var fillLevel = FillLevel.values()[value / 4];

        setRenderFillLevel(fillLevel);
    }

    private void setRenderFillLevel(FillLevel newFillLevel) {
        var oldRenderState = getRenderState();
        var newRenderState = oldRenderState.setValue(FillLevel.FILL_PROPERTY, newFillLevel);
        if (!Objects.equals(oldRenderState, newRenderState)) {
            setRenderState(newRenderState);
        }
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof MicroverseProjectorMachine projector) {
            int value = (int) (16 * projector.getMicroverseIntegrity() /
                    ((float) MicroverseProjectorMachine.MICROVERSE_MAX_INTEGRITY));
            int signal = value == 16 ? 15 : value;
            setRenderFillLevel(FillLevel.values()[signal / 4]);
        }
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        super.removedFromController(controller);
        setRenderFillLevel(FillLevel.EMPTY_TO_QUARTER);
    }
}

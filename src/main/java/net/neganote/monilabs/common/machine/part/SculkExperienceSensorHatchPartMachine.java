package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidType;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SculkExperienceSensorHatchPartMachine extends SensorHatchPartMachine {

    public SculkExperienceSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.ZPM);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
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

    private void setRenderFillLevel(FillLevel newFillLevel) {
        var oldRenderState = getRenderState();
        var newRenderState = oldRenderState.setValue(FillLevel.FILL_PROPERTY, newFillLevel);
        if (!Objects.equals(oldRenderState, newRenderState)) {
            setRenderState(newRenderState);
        }
    }

    @Override
    public void updateSignal() {
        super.updateSignal();
        var controllers = getControllers().stream().filter(SculkVatMachine.class::isInstance)
                .map(SculkVatMachine.class::cast)
                .toList();
        if (controllers.isEmpty()) {
            setRenderFillLevel(FillLevel.EMPTY_TO_QUARTER);
        } else {
            var controller = controllers.get(0);
            int value = (int) (16 * controller.getXpBuffer() / ((float) (FluidType.BUCKET_VOLUME << GTValues.ZPM)));
            int signal = value == 16 ? 15 : value;

            var fillLevel = FillLevel.values()[signal / 4];

            setRenderFillLevel(fillLevel);
        }
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof SculkVatMachine sculkVat) {
            int value = (int) (16 * sculkVat.getXpBuffer() / ((float) (FluidType.BUCKET_VOLUME << GTValues.ZPM)));
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

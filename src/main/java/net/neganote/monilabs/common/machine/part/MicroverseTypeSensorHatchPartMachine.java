package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MicroverseTypeSensorHatchPartMachine extends SensorHatchPartMachine {

    public MicroverseTypeSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.HV);
    }

    public static Object2IntMap<Microverse> SIGNAL_MAP;

    static {
        SIGNAL_MAP = new Object2IntArrayMap<>();
        SIGNAL_MAP.put(Microverse.NONE, 0);
        SIGNAL_MAP.put(Microverse.NORMAL, 1);
        SIGNAL_MAP.put(Microverse.SHATTERED, 2);
        SIGNAL_MAP.put(Microverse.CORRUPTED, 3);
        SIGNAL_MAP.put(Microverse.HOSTILE, 4);
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var controller = (MicroverseProjectorMachine) getController();
            if (controller == null) {
                return 0;
            }
            return SIGNAL_MAP.getInt(controller.getMicroverse());
        }
        return 0;
    }

    @Override
    public void updateSignal() {
        super.updateSignal();

        var controller = (MicroverseProjectorMachine) getController();

        setRenderMicroverse(controller == null ? Microverse.NONE : controller.getMicroverse());
    }

    private void setRenderMicroverse(Microverse newMicroverse) {
        var oldRenderState = getRenderState();
        var newRenderState = oldRenderState.setValue(Microverse.MICROVERSE_TYPE, newMicroverse);
        if (!Objects.equals(oldRenderState, newRenderState)) {
            setRenderState(newRenderState);
        }
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof MicroverseProjectorMachine projector) {
            setRenderMicroverse(projector.getMicroverse());
        }
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        super.removedFromController(controller);
        setRenderMicroverse(Microverse.NONE);
    }
}

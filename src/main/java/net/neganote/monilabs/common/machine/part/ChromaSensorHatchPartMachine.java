package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.Color;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ChromaSensorHatchPartMachine extends SensorHatchPartMachine {

    public ChromaSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder, GTValues.UHV);
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        if (controller instanceof PrismaticCrucibleMachine pcm) {
            setRenderColor(RenderColor.values()[pcm.getColorState().key + 1]);
        }
    }

    public @Nullable Color getPrismacColor() {
        var controller = getController();
        if (controller == null) {
            return null;
        }
        return ((PrismaticCrucibleMachine) controller).getColorState();
    }

    @Override
    public int getOutputSignal(Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var prismacColor = getPrismacColor();
            return prismacColor == null ? 0 : prismacColor.key + 1;
        } else {
            return 0;
        }
    }

    @Override
    public void updateSignal() {
        super.updateSignal();
        var controllers = getControllers().stream().filter(PrismaticCrucibleMachine.class::isInstance)
                .map(PrismaticCrucibleMachine.class::cast)
                .toList();
        if (controllers.isEmpty()) {
            setRenderColor(RenderColor.NONE);
        } else {
            var controller = controllers.get(0);
            int signal = controller.isFormed() ? controller.getColorState().key + 1 : 0;
            setRenderColor(RenderColor.values()[signal]);
        }
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        super.removedFromController(controller);
        setRenderColor(RenderColor.NONE);
    }

    void setRenderColor(RenderColor newRenderColor) {
        var oldRenderState = getRenderState();
        var newRenderState = oldRenderState.setValue(RenderColor.COLOR_PROPERTY, newRenderColor);
        if (!Objects.equals(oldRenderState, newRenderState)) {
            setRenderState(newRenderState);
        }
    }
}

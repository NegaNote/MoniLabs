package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import org.jetbrains.annotations.NotNull;

public class ChromaSensorHatchPartMachine extends TieredPartMachine {

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
                return 0;
            } else {
                var controller = controllers.get(0);
                return controller.isFormed() ? controller.getColorState().key + 1 : 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public boolean canConnectRedstone(@NotNull Direction side) {
        return side == getFrontFacing();
    }
}

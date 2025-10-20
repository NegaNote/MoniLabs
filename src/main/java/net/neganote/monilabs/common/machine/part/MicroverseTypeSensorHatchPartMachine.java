package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import org.jetbrains.annotations.Nullable;

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
            var controllers = getControllers().stream().filter(MicroverseProjectorMachine.class::isInstance)
                    .map(MicroverseProjectorMachine.class::cast)
                    .toList();
            if (controllers.isEmpty()) {
                return 0;
            } else {
                var controller = controllers.get(0);
                return SIGNAL_MAP.getInt(controller.getMicroverse());
            }
        }
        return 0;
    }
}

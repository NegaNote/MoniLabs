package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;

import net.minecraft.core.Direction;

import org.jetbrains.annotations.NotNull;

public class SensorHatchPartMachine extends TieredPartMachine {

    private final ConditionalSubscriptionHandler signalUpdateHandler;

    public SensorHatchPartMachine(IMachineBlockEntity holder, int tier) {
        super(holder, tier);
        this.signalUpdateHandler = new ConditionalSubscriptionHandler(this, this::updateSignal, () -> true);
    }

    @Override
    public boolean canConnectRedstone(@NotNull Direction side) {
        return side == getFrontFacing();
    }

    @Override
    public void removedFromController(@NotNull IMultiController controller) {
        super.removedFromController(controller);
        signalUpdateHandler.updateSubscription();
    }

    @Override
    public void addedToController(@NotNull IMultiController controller) {
        super.addedToController(controller);
        signalUpdateHandler.updateSubscription();
    }
}

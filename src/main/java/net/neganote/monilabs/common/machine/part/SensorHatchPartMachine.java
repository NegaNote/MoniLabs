package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.part.TieredPartMachine;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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

    public @Nullable IMultiController getController() {
        var controllers = getControllers().stream().filter(PrismaticCrucibleMachine.class::isInstance)
                .map(PrismaticCrucibleMachine.class::cast)
                .toList();
        if (controllers.isEmpty() || !controllers.get(0).isFormed()) {
            return null;
        } else {
            return controllers.get(0);
        }
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return false;
    }

    @Override
    public boolean canShared() {
        return false;
    }
}

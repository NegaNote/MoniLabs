package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableMultiblockMachine;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeEnergySavedData;

import java.util.UUID;

public class CreativeEnergyMultiMachine extends WorkableMultiblockMachine {

    private final ConditionalSubscriptionHandler creativeEnergySubscription;

    public CreativeEnergyMultiMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);

        this.creativeEnergySubscription = new ConditionalSubscriptionHandler(this, this::tickEnableCreativeEnergy,
                this::isSubscriptionActive);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        creativeEnergySubscription.updateSubscription();
    }

    public void enableCreativeEnergy(boolean enabled) {
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID == null) {
            ownerUUID = new UUID(0L, 0L);
        }
        if (getLevel() instanceof ServerLevel serverLevel) {
            CreativeEnergySavedData savedData = CreativeEnergySavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            savedData.setEnabled(ownerUUID, enabled);
        }
    }

    private void tickEnableCreativeEnergy() {
        enableCreativeEnergy(isActive());
    }

    private Boolean isSubscriptionActive() {
        return isFormed();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
        if (!isWorkingAllowed) {
            enableCreativeEnergy(false);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        enableCreativeEnergy(false);
        creativeEnergySubscription.unsubscribe();
    }
}

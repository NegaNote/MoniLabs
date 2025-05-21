package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeDataAccessSavedData;

import java.util.UUID;

public class CreativeDataMultiMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler creativeDataSubscription;

    public CreativeDataMultiMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);

        this.creativeDataSubscription = new ConditionalSubscriptionHandler(this, this::tickEnableCreativeData,
                this::isSubscriptionActive);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        creativeDataSubscription.updateSubscription();
    }

    public void enableCreativeData(boolean enabled) {
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID == null) {
            ownerUUID = new UUID(0L, 0L);
        }
        if (getLevel() instanceof ServerLevel serverLevel) {
            CreativeDataAccessSavedData savedData = CreativeDataAccessSavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            savedData.setEnabled(ownerUUID, enabled);
        }
    }

    private void tickEnableCreativeData() {
        enableCreativeData(isActive() && getRecipeLogic().isWorkingEnabled());
    }

    private Boolean isSubscriptionActive() {
        return isFormed();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
        if (!isWorkingAllowed) {
            enableCreativeData(false);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        enableCreativeData(false);
        creativeDataSubscription.unsubscribe();
    }
}

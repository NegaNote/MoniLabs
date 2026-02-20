package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeDataAccessSavedData;

import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("unused")
public class CreativeDataMultiMachine extends UniqueWorkableElectricMultiblockMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CreativeDataMultiMachine.class,
            UniqueWorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

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

    @Override
    public void onUnload() {
        super.onUnload();
        enableCreativeData(false);
    }

    private void tickEnableCreativeData() {
        enableCreativeData(recipeLogic.isWorking());
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

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

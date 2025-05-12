package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeEnergySavedData;

import java.util.UUID;

public class CreativeEnergyMultiMachine extends WorkableElectricMultiblockMachine {

    public CreativeEnergyMultiMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        MachineOwner owner = getOwner();
        UUID ownerUUID = owner == null ? new UUID(0L, 0L) : owner.getUUID();
        if (getLevel() instanceof ServerLevel serverLevel) {
            CreativeEnergySavedData savedData = CreativeEnergySavedData.getOrCreate(serverLevel);
            savedData.setEnabled(ownerUUID, isWorkingAllowed);
        }
        super.setWorkingEnabled(isWorkingAllowed);
    }
}

package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.common.machine.owner.MachineOwner;

import net.minecraft.server.level.ServerLevel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unused")
public class CreativeEnergyMultiMachine extends UniqueWorkableElectricMultiblockMachine {

    private static final Set<UUID> ACTIVE_OWNERS = new HashSet<>();
    private static final Map<UUID, Boolean> PLAYER_CACHE = new HashMap<>();

    private final ConditionalSubscriptionHandler creativeEnergySubscription;

    public CreativeEnergyMultiMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);

        this.creativeEnergySubscription = new ConditionalSubscriptionHandler(this, this::tickEnableCreativeEnergy,
                this::isSubscriptionActive);
    }

    public static boolean isCreativeEnergyEnabledFor(UUID playerUUID) {
        if (ACTIVE_OWNERS.isEmpty()) return false;
        return PLAYER_CACHE.computeIfAbsent(playerUUID, uuid -> {
            MachineOwner owner = MachineOwner.getOwner(uuid);
            if (owner == null) return false;
            return ACTIVE_OWNERS.stream().anyMatch(owner::isPlayerInTeam);
        });
    }

    public static void clearActiveOwners() {
        ACTIVE_OWNERS.clear();
        PLAYER_CACHE.clear();
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        enableCreativeEnergy(recipeLogic.isWorking());
        creativeEnergySubscription.updateSubscription();
    }

    public void enableCreativeEnergy(boolean enabled) {
        if (!(getLevel() instanceof ServerLevel)) return;
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID == null) {
            ownerUUID = new UUID(0L, 0L);
        }
        boolean changed = enabled ? ACTIVE_OWNERS.add(ownerUUID) : ACTIVE_OWNERS.remove(ownerUUID);
        if (changed) {
            PLAYER_CACHE.clear();
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        enableCreativeEnergy(false);
    }

    private void tickEnableCreativeEnergy() {
        enableCreativeEnergy(recipeLogic.isWorking());
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

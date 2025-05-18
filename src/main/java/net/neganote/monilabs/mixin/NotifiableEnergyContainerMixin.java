package net.neganote.monilabs.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeEnergySavedData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = NotifiableEnergyContainer.class, remap = false)
public class NotifiableEnergyContainerMixin extends MachineTrait {

    public NotifiableEnergyContainerMixin(MetaMachine machine) {
        super(machine);
    }

    @Shadow
    public long getEnergyCapacity() {
        return 0;
    }

    @Override
    public MetaMachine getMachine() {
        return super.getMachine();
    }

    @Inject(method = "getEnergyStored()J", at = @At(value = "HEAD"), cancellable = true)
    private void monilabs$injectBeforeGetEnergyStored(CallbackInfoReturnable<Long> cir) {
        if (getMachine().getLevel() instanceof ServerLevel serverLevel) {
            CreativeEnergySavedData savedData = CreativeEnergySavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            UUID uuid = getMachine().getOwnerUUID();
            if (uuid == null) {
                uuid = new UUID(0, 0);
            }
            if (savedData.isEnabledFor(uuid)) {
                cir.setReturnValue(getEnergyCapacity());
            }
        }
    }

    @Inject(method = "changeEnergy", at = @At(value = "HEAD"), cancellable = true)
    private void monilabs$injectBeforeChangeEnergy(long energyToAdd, CallbackInfoReturnable<Long> cir) {
        if (getMachine().getLevel() instanceof ServerLevel serverLevel) {
            CreativeEnergySavedData savedData = CreativeEnergySavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            UUID uuid = getMachine().getOwnerUUID();
            if (uuid == null) {
                uuid = new UUID(0, 0);
            }
            if (savedData.isEnabledFor(uuid)) {
                cir.setReturnValue(getEnergyCapacity());
            }
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return NotifiableEnergyContainer.MANAGED_FIELD_HOLDER;
    }
}

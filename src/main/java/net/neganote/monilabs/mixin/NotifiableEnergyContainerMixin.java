package net.neganote.monilabs.mixin;

import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.trait.MachineTrait;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeEnergySavedData;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(value = NotifiableEnergyContainer.class, remap = false)
public class NotifiableEnergyContainerMixin extends MachineTrait {

    @Shadow
    protected @Nullable TickableSubscription outputSubs;

    public NotifiableEnergyContainerMixin(MetaMachine machine) {
        super(machine);
    }

    @Shadow
    public long getEnergyCapacity() {
        return 0;
    }

    @Shadow
    public void serverTick() {}

    @Override
    public MetaMachine getMachine() {
        return super.getMachine();
    }

    @Inject(method = "getEnergyStored()J", at = @At(value = "HEAD"), cancellable = true)
    private void monilabs$injectBeforeGetEnergyStored(CallbackInfoReturnable<Long> cir) {
        MetaMachine machine = getMachine();
        if (machine.getLevel() instanceof ServerLevel serverLevel) {
            outputSubs = machine.subscribeServerTick(this.outputSubs, this::serverTick);
            CreativeEnergySavedData savedData = CreativeEnergySavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            UUID uuid = machine.getOwnerUUID();
            if (uuid != null && savedData.isEnabledFor(uuid)) {
                cir.setReturnValue(getEnergyCapacity());
            }
        }
    }

    // This injection is so that it doesn't try and modify the *actual* stored energy, which could easily cheese
    // the power substation and the like to be filled even after the boolean is set back to false.
    @Inject(method = "changeEnergy", at = @At(value = "HEAD"), cancellable = true)
    private void monilabs$injectBeforeChangeEnergy(long energyToAdd, CallbackInfoReturnable<Long> cir) {
        MetaMachine machine = getMachine();
        if (machine.getLevel() instanceof ServerLevel serverLevel) {
            outputSubs = machine.subscribeServerTick(this.outputSubs, this::serverTick);
            CreativeEnergySavedData savedData = CreativeEnergySavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            UUID uuid = machine.getOwnerUUID();
            if (uuid != null && savedData.isEnabledFor(uuid)) {
                cir.setReturnValue(energyToAdd);
            }
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return NotifiableEnergyContainer.MANAGED_FIELD_HOLDER;
    }
}

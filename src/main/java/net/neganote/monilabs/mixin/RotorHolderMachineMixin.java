package net.neganote.monilabs.mixin;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IRotorHolderMachine;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = IRotorHolderMachine.class, remap = false)
public interface RotorHolderMachineMixin {

    @Inject(method = "damageRotor", at = @At(value = "HEAD"), cancellable = true)
    default void monilabs$stopRotorDamage(int damageAmount, CallbackInfo ci) {
        ci.cancel();
    }
}

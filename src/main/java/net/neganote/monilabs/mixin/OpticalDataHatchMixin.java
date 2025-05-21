package net.neganote.monilabs.mixin;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.machine.multiblock.part.OpticalDataHatchMachine;

import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.CreativeDataAccessSavedData;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = OpticalDataHatchMachine.class, remap = false)
public class OpticalDataHatchMixin extends MetaMachine {

    public OpticalDataHatchMixin(IMachineBlockEntity holder) {
        super(holder);
    }

    @Inject(method = "modifyRecipe", at = @At(value = "HEAD"), cancellable = true)
    public void modifyRecipe(GTRecipe recipe, CallbackInfoReturnable<GTRecipe> cir) {
        if (getLevel() instanceof ServerLevel serverLevel) {
            CreativeDataAccessSavedData savedData = CreativeDataAccessSavedData.getOrCreate(serverLevel);
            if (savedData.isEnabledFor(getOwnerUUID())) {
                cir.setReturnValue(recipe);
            }
        }
    }
}

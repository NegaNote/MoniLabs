package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedMicroverseStabilitySensorHatchPartMachine extends MicroverseStabilitySensorHatchPartMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedMicroverseStabilitySensorHatchPartMachine.class,
            MicroverseStabilitySensorHatchPartMachine.MANAGED_FIELD_HOLDER);

    public AdvancedMicroverseStabilitySensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

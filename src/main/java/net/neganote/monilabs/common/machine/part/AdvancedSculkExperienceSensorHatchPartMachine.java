package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedSculkExperienceSensorHatchPartMachine extends SculkExperienceSensorHatchPartMachine {

    public static int DEFAULT_MIN_PERCENT = 33;
    public static int DEFAULT_MAX_PERCENT = 66;

    @Persisted
    @Setter
    @Getter
    public int minPercent, maxPercent;

    @Persisted
    @Setter
    @Getter
    public boolean inverted;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedSculkExperienceSensorHatchPartMachine.class,
            SculkExperienceSensorHatchPartMachine.MANAGED_FIELD_HOLDER);

    public AdvancedSculkExperienceSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
        minPercent = DEFAULT_MIN_PERCENT;
        maxPercent = DEFAULT_MAX_PERCENT;
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction != getFrontFacing().getOpposite()) {
            return 0;
        }

        var controller = (SculkVatMachine) getController();
        if (controller == null) {
            return 0;
        }

        int buffer = controller.getXpBuffer();

        int minXp = (int) (SculkVatMachine.XP_BUFFER_MAX * (minPercent / 100.0));
        int maxXp = (int) (SculkVatMachine.XP_BUFFER_MAX * (maxPercent / 100.0));

        if (inverted) {
            if (buffer >= minXp && buffer <= maxXp) {
                return 0;
            } else {
                return 15;
            }
        } else {
            if (buffer >= minXp && buffer <= maxXp) {
                return 15;
            } else {
                return 0;
            }
        }
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

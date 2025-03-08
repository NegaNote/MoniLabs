package net.neganote.monilabs.common.item;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.item.component.IInteractionItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class PrecisionBreakBehavior implements IInteractionItem {
    public PrecisionBreakBehavior() {
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        if (!level.isClientSide()) {
            BlockPos pos = context.getClickedPos();
            BlockState blockState = level.getBlockState(pos);
            if (!blockState.canHarvestBlock(level, pos, context.getPlayer())) {
                return InteractionResult.PASS;
            }

            var electricItem = GTCapabilityHelper.getElectricItem(context.getItemInHand());

            if (electricItem != null) {
                if (electricItem.discharge(8192L, GTValues.IV, true, false, true) == 8192L) {
                    electricItem.discharge(8192L, GTValues.IV, true, false, false);
                } else {
                    return InteractionResult.PASS;
                }
            }

            level.destroyBlock(pos, true);
        }
        return InteractionResult.SUCCESS;
    }
}

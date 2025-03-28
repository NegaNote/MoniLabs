package net.neganote.monilabs.client;

import com.gregtechceu.gtceu.api.block.ActiveBlock;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neganote.monilabs.common.block.PrismaticActiveBlock;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PrismaticActiveBlockColor implements BlockColor {

    @Override
    public int getColor(@NotNull BlockState pState, @Nullable BlockAndTintGetter pLevel, @Nullable BlockPos pPos,
                        int pTintIndex) {
        int colorKey = pState.getValue(PrismaticActiveBlock.COLOR);
        Color color = Color.getColorFromKey(colorKey);
        if (color.isRealColor() && pState.getValue(ActiveBlock.ACTIVE) && pTintIndex == 1) {
            return color.integerColor;
        } else {
            return 0xFFFFFFFF;
        }
    }
}

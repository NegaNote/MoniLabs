package net.neganote.monilabs.common.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;

import org.jetbrains.annotations.NotNull;

public class PrismaticActiveBlock extends ActiveBlock {

    public static final IntegerProperty COLOR = IntegerProperty.create("prismatic_color", 0, 11);

    public PrismaticActiveBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(COLOR, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(COLOR);
    }
}

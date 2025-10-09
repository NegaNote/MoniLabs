package net.neganote.monilabs.common.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.Vec3;
import net.neganote.monilabs.client.render.effects.MoniTrails;
import net.neganote.monilabs.client.render.effects.ParticleTypes;
import net.neganote.monilabs.client.render.effects.PrismFX;
import net.neganote.monilabs.common.machine.multiblock.Color;
import org.jetbrains.annotations.NotNull;

import stone.mae2.api.client.trails.CloudChamberUtil;

public class PrismaticActiveBlock extends ActiveBlock {

  public static final IntegerProperty COLOR = IntegerProperty
    .create("prismatic_color", 0, 11);

  public PrismaticActiveBlock(Properties properties) {
    super(properties);
    registerDefaultState(defaultBlockState().setValue(COLOR, 0));
  }

  @Override
  public void animateTick(BlockState state, Level level, BlockPos pos,
    RandomSource random) {
    if (state.getValue(GTBlockStateProperties.ACTIVE)) {
      Color color = Color.getColorFromKey(state.getValue(COLOR));
      Vec3 end = CloudChamberUtil.randomPoint(random, MoniTrails.CHROMA_BETA);
      Vec3 start = new Vec3(pos.getX(), pos.getY(), pos.getZ())
        .offsetRandom(random, .5f)
        .add(.5, .5, .5);
      PrismFX.SetColor.setColor(color.r, color.g, color.b);
      CloudChamberUtil
        .drawTrail(level, start, end.add(start), random,
          ParticleTypes.CHROMA_SET);
    }
  }

  @Override
  protected void createBlockStateDefinition(
    StateDefinition.@NotNull Builder<Block, BlockState> builder) {
    super.createBlockStateDefinition(builder);
    builder.add(COLOR);
  }
}

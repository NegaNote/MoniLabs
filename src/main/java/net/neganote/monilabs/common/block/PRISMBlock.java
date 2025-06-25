package net.neganote.monilabs.common.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ModelFile;
import net.neganote.monilabs.client.render.effects.ParticleTypes;

import stone.mae2.api.client.trails.CloudChamberUtil;
import stone.mae2.api.client.trails.Trail;
import stone.mae2.api.client.trails.TrailForming;

public class PRISMBlock extends ActiveBlock implements TrailForming {

  public PRISMBlock(Properties properties) { super(properties); }

  // stolen from AbstractGlassBlock
  public VoxelShape getVisualShape(BlockState pState, BlockGetter pReader,
      BlockPos pPos, CollisionContext pContext) {
      return Shapes.empty();
  }

  // stolen from AbstractGlassBlock
  public float getShadeBrightness(BlockState pState, BlockGetter pLevel,
      BlockPos pPos) {
      return 1.0F;
  }

  // stolen from AbstractGlassBlock
  public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader,
      BlockPos pPos) {
      return true;
  }

  // stolen from HalfTransparentBlock
  @Override
  public boolean skipRendering(BlockState pState,
      BlockState pAdjacentBlockState, Direction pSide) {
      return pAdjacentBlockState.is(this) ? true
          : super.skipRendering(pState, pAdjacentBlockState, pSide);
  }

  @Override
public void animateTick(BlockState pState, Level pLevel, BlockPos pPos,
    RandomSource pRandom) {
    CloudChamberUtil.tryBackgroundRadiation(pLevel, pRandom);
}

  @Override
  public ParticleOptions getTrailParticle(BlockState state, Trail trail) {
      return ParticleTypes.CHROMA_BACKGROUND;
  }

  // this is stupid, but it's what GTm wants because they couldn't be bothered
  // with wildcards I guess
  public static NonNullBiConsumer<DataGenContext<Block, PRISMBlock>, RegistrateBlockstateProvider> createPRISMModel(
      String name, ResourceLocation texturePath) {
    return (ctx, prov) -> {
        ActiveBlock block = ctx.getEntry();
        ResourceLocation texturePath2 = texturePath.withPrefix("block/");

        ModelFile inactive = prov.models().cubeAll(name, texturePath2);
        ModelFile active = prov
            .models()
            .cubeAll(name + "_active",
                texturePath2.withSuffix("_active"));

        prov
            .getVariantBuilder(block)
            .partialState()
            .with(ActiveBlock.ACTIVE, false)
            .modelForState()
            .modelFile(inactive)
            .addModel()
            .partialState()
            .with(ActiveBlock.ACTIVE, true)
            .modelForState()
            .modelFile(active)
            .addModel();
    };
}

}

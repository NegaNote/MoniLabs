package net.neganote.monilabs.common.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.VariantBlockStateBuilder;
import net.neganote.monilabs.common.machine.multiblock.Color;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
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

    public static NonNullBiConsumer<DataGenContext<Block, PrismaticActiveBlock>, RegistrateBlockstateProvider> createPrismaticActiveModel(String name,
                                                                                                                                          ResourceLocation texturePath) {
        return (ctx, prov) -> {
            ActiveBlock block = ctx.getEntry();

            ModelFile inactive = prov
                    .models()
                    .cubeAll(ctx.getName(),
                            texturePath.withPrefix("block/"));

            VariantBlockStateBuilder builder = prov
                    .getVariantBuilder(block)
                    .partialState()
                    .with(GTBlockStateProperties.ACTIVE, false)
                    .modelForState()
                    .modelFile(inactive)
                    .addModel();

            ResourceLocation location = texturePath.withPrefix("block/");
            for (Color color : Color.ACTUAL_COLORS) {
                String colorStr = "_" + color.name().toLowerCase();
                ModelFile file = prov
                        .models()
                        .cubeAll(ctx.getName() + colorStr, location.withSuffix(colorStr));

                builder
                        .partialState()
                        .with(GTBlockStateProperties.ACTIVE, true)
                        .with(COLOR, color.key)
                        .modelForState()
                        .modelFile(file)
                        .addModel();
            }
        };
    }
}

package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.block.ActiveBlock;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

// Despite the name, this only exists for the multi to attach a renderer to it.
public class PrismaticActiveBlock extends ActiveBlock {

    public PrismaticActiveBlock(Properties properties) {
        super(properties);
    }

    public static NonNullBiConsumer<DataGenContext<Block, PrismaticActiveBlock>, RegistrateBlockstateProvider> createActiveModel(ResourceLocation modelPath) {
        return (ctx, prov) -> {
            ActiveBlock block = ctx.getEntry();
            ModelFile inactive = prov.models().getExistingFile(modelPath);
            ModelFile active = prov.models().getExistingFile(modelPath.withSuffix("_active"));
            prov.getVariantBuilder(block)
                    .partialState().with(ActiveBlock.ACTIVE, false).modelForState().modelFile(inactive).addModel()
                    .partialState().with(ActiveBlock.ACTIVE, true).modelForState().modelFile(active).addModel();
        };
    }
}

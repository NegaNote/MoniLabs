package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ModelFile;

// Despite the name, this only exists for the multi to attach a renderer to it.
public class PrismaticCoreBlock extends ActiveBlock {
    public PrismaticCoreBlock(Properties properties) {
        super(properties);
    }

    public static NonNullBiConsumer<DataGenContext<Block, PrismaticCoreBlock>, RegistrateBlockstateProvider> createActiveModel(ResourceLocation modelPath) {
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

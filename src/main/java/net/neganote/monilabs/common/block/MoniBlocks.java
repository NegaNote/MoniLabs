package net.neganote.monilabs.common.block;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.common.data.GTModels;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.item.PrismaticFocusItem;
import net.neganote.monilabs.data.recipe.RecipeTags;
import org.jetbrains.annotations.NotNull;

import stone.mae2.bootstrap.MAE2Tags;

@SuppressWarnings("unused")
public class MoniBlocks {

    public static void init() {}

    public static BlockEntry<PrismaticActiveBlock> registerPrismaticActiveBlock(String name, String internal,
                                                                                NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return REGISTRATE
                .block(internal, PrismaticActiveBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false)
                        .lightLevel((b) -> b.getValue(ActiveBlock.ACTIVE) ? 15 : 0))
                .blockstate(PrismaticActiveBlock.createPrismaticActiveModel("block/" + internal,
                        MoniLabs.id(internal)))
                .tag(RecipeTags.MINEABLE_WITH_WRENCH, BlockTags.MINEABLE_WITH_PICKAXE)
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    public static BlockEntry<PrismaticActiveBlock> CHROMODYNAMIC_CONDUCTION_CASING = registerPrismaticActiveBlock(
            "Chromodynamic Conduction Casing", "chromodynamic_conduction_casing", BlockItem::new);

    private static @NotNull BlockEntry<Block> registerSimpleBlock(String name, String internal,
                                                                  NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return REGISTRATE
                .block(internal, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .blockstate(GTModels.cubeAllModel(internal,
                        MoniLabs.id("block/" + internal)))
                .tag(RecipeTags.MINEABLE_WITH_WRENCH, BlockTags.MINEABLE_WITH_PICKAXE)
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    public static BlockEntry<Block> PRISMATIC_FOCUS = registerSimpleBlock("Prismatic Focus",
            "prismatic_focus", PrismaticFocusItem::new);

    public static BlockEntry<Block> KNOWLEDGE_TRANSMISSION_ARRAY = registerSimpleBlock("Knowledge Transmission Array",
            "knowledge_transmission_array", BlockItem::new);

    public static final BlockEntry<PRISMBlock> PRISM_GLASS = REGISTRATE
        .block("prism_glass", PRISMBlock::new)
        .initialProperties(() -> Blocks.GLASS)
        .addLayer(() -> RenderType::translucent)
        .blockstate(PRISMBlock
            .createPRISMModel("block/prism_glass",
                MoniLabs.id("prism_glass")))
        .tag(RecipeTags.MINEABLE_WITH_WRENCH,
            BlockTags.MINEABLE_WITH_PICKAXE, MAE2Tags.CLOUD_CHAMBERS)
        .item(BlockItem::new)
        // .model((ctx, prov) -> prov
        // .withExistingParent(prov.name(ctx),
        // MoniLabs.id("block/prism")))
        .build()
        .register();
}

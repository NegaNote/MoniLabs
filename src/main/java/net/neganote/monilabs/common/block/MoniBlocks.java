package net.neganote.monilabs.common.block;

import com.gregtechceu.gtceu.api.block.ActiveBlock;
import com.gregtechceu.gtceu.common.data.GTModels;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.data.recipe.RecipeTags;
import net.neganote.monilabs.common.item.PrismaticFocusItem;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

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

    public static BlockEntry<Block> DIMENSIONAL_STABILIZATION_NETHERITE_CASING = REGISTRATE
            .block("dimensional_stabilization_netherite_casing", Block::new)
            .initialProperties(() -> Blocks.IRON_BLOCK)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
            .blockstate(GTModels.cubeAllModel("dimensional_stabilization_netherite_casing",
                    MoniLabs.id("block/dimensional_stabilization_netherite_casing")))
            .tag(RecipeTags.MINEABLE_WITH_WRENCH, BlockTags.MINEABLE_WITH_PICKAXE)
            .lang("Dimensional Stabilization Netherite Casing")
            .item(BlockItem::new)
            .build()
            .register();

    public static BlockEntry<PrismaticActiveBlock> PRISMATIC_FOCUS = registerPrismaticActiveBlock("Prismatic Focus",
            "prismatic_focus", PrismaticFocusItem::new);
}

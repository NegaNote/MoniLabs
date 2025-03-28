package net.neganote.monilabs.data;

import com.gregtechceu.gtceu.common.data.GTModels;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.PrismaticActiveBlockColor;
import net.neganote.monilabs.common.block.PrismaticActiveBlock;
import net.neganote.monilabs.data.recipe.RecipeTags;
import net.neganote.monilabs.item.PrismaticFocusItem;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiConsumer;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

public class MoniBlocks {

    public static void init() {}

    public static BlockEntry<PrismaticActiveBlock> registerPrismaticActiveBlock(String name, String internal) {
        return REGISTRATE
                .block(internal, PrismaticActiveBlock::new)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .blockstate(PrismaticActiveBlock.createPrismaticActiveModel("block/" + internal,
                        MoniLabs.id("block/" + internal)))
                .tag(RecipeTags.MINEABLE_WITH_WRENCH)
                .lang(name)
                .color(() -> PrismaticActiveBlockColor::new)
                .item(BlockItem::new)
                .model((ctx, prov) -> {
                    BlockItem item = ctx.getEntry();
                    prov.blockItem(() -> item);
                })
                .build()
                .register();
    }

    public static BlockEntry<PrismaticActiveBlock> CHROMODYNAMIC_CONDUCTION_CASING =
            registerPrismaticActiveBlock("Chromodynamic Conduction Casing", "casings/chromodynamic_conduction_casing");

    public static BlockEntry<Block> PRISMATIC_CONTAINMENT_LINING = REGISTRATE
            .block("casings/prismatic_containment_lining", Block::new)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
            .blockstate(GTModels.cubeAllModel("block/casings/prismatic_containment_lining",
                    MoniLabs.id("block/casings/prismatic_containment_lining")))
            .tag(RecipeTags.MINEABLE_WITH_WRENCH)
            .lang("Prismatic Containment Lining")
            .item(BlockItem::new)
            .model(NonNullBiConsumer.noop())
            .build()
            .register();

    public static BlockEntry<PrismaticActiveBlock> PRISMATIC_FOCUS =
            registerPrismaticActiveBlock("Prismatic Focus", "prismatic_focus");
}

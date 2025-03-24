package net.neganote.monilabs.data;

import com.gregtechceu.gtceu.common.data.GTModels;

import net.minecraft.world.level.block.Block;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.part.PrismaticFocusBlock;
import net.neganote.monilabs.data.recipe.RecipeTags;
import net.neganote.monilabs.item.PrismaticFocusItem;

import com.tterrag.registrate.util.entry.BlockEntry;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

public class MoniBlocks {

    public static void init() {}

    public static BlockEntry<Block> PRISMATIC_CASING = REGISTRATE
            .block("prismatic_casing", Block::new)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
            .blockstate(GTModels.cubeAllModel("prismatic_casing", MoniLabs.id("block/casings/prismatic_casing")))
            .tag(RecipeTags.MINEABLE_WITH_WRENCH)
            .lang("Prismatic Casing")
            .item()
            .build()
            .register();

    public static BlockEntry<PrismaticFocusBlock> PRISMATIC_FOCUS = REGISTRATE
            .block("prismatic_focus", PrismaticFocusBlock::new)
            .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
            .blockstate(PrismaticFocusBlock.createActiveModel(MoniLabs.id("block/prismatic_focus")))
            .tag(RecipeTags.MINEABLE_WITH_WRENCH)
            .lang("Prismatic Focus")
            .item(PrismaticFocusItem::new)
            .build()
            .register();
}

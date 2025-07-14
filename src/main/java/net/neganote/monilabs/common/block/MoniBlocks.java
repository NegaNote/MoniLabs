package net.neganote.monilabs.common.block;

import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.item.PrismaticFocusItem;
import net.neganote.monilabs.data.recipe.RecipeTags;

import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullBiFunction;
import org.jetbrains.annotations.NotNull;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

@SuppressWarnings("unused")
public class MoniBlocks {

    public static void init() {}

    public static BlockEntry<PrismaticActiveBlock> registerPrismaticActiveBlock(String name, String id,
                                                                                String texturePath,
                                                                                NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return REGISTRATE
                .block(id, PrismaticActiveBlock::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false)
                        .lightLevel((b) -> b.getValue(GTBlockStateProperties.ACTIVE) ? 15 : 0))
                .blockstate(PrismaticActiveBlock.createPrismaticActiveModel(MoniLabs.id(texturePath)))
                .tag(RecipeTags.MINEABLE_WITH_WRENCH, BlockTags.MINEABLE_WITH_PICKAXE)
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    public static BlockEntry<PrismaticActiveBlock> CHROMODYNAMIC_CONDUCTION_CASING = registerPrismaticActiveBlock(
            "Chromodynamic Conduction Casing", "chromodynamic_conduction_casing",
            "casing/chromodynamic/chromodynamic_conduction_casing",
            BlockItem::new);

    private static @NotNull BlockEntry<Block> registerSimpleBlock(String name, String id, String texture,
                                                                  NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return REGISTRATE
                .block(id, Block::new)
                .initialProperties(() -> Blocks.IRON_BLOCK)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                        prov.models().cubeAll(ctx.getName(), MoniLabs.id("block/" + texture))))
                .tag(RecipeTags.MINEABLE_WITH_WRENCH, BlockTags.MINEABLE_WITH_PICKAXE)
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    public static BlockEntry<Block> PRISMATIC_FOCUS = registerSimpleBlock("Prismatic Focus",
            "prismatic_focus", "prismatic_focus", PrismaticFocusItem::new);

    public static BlockEntry<Block> KNOWLEDGE_TRANSMISSION_ARRAY = registerSimpleBlock("Knowledge Transmission Array",
            "knowledge_transmission_array", "knowledge_transmission_array", BlockItem::new);

    public static BlockEntry<Block> DIMENSIONAL_STABILIZATION_NETHERITE_CASING = registerSimpleBlock(
            "Dimensional Stabilization Netherite Casing", "dimensional_stabilization_netherite_casing",
            "casing/netherite", BlockItem::new);

    public static BlockEntry<Block> MICROVERSE_CASING = registerSimpleBlock("Microverse Casing", "microverse_casing",
            "casing/microverse", BlockItem::new);

    public static BlockEntry<Block> CRYOLOBUS_CASING = registerSimpleBlock("Cryolobus Casing", "cryolobus_casing",
            "casing/cryolobus", BlockItem::new);
}

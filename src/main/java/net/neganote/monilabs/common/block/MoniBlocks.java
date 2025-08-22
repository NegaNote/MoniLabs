package net.neganote.monilabs.common.block;

import com.gregtechceu.gtceu.api.block.property.GTBlockStateProperties;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.item.PrismaticFocusItem;
import net.neganote.monilabs.data.models.MoniModels;
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
                .blockstate(MoniModels.createPrismaticActiveBlockModel(MoniLabs.id(texturePath)))
                .tag(RecipeTags.MINEABLE_WITH_WRENCH, BlockTags.MINEABLE_WITH_PICKAXE)
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    public static BlockEntry<PrismaticActiveBlock> registerPrismaticActiveUpsideDownBeacon(String name, String id,
                                                                                           String texturePath,
                                                                                           NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return REGISTRATE
                .block(id, PrismaticActiveBlock::new)
                .initialProperties(() -> Blocks.BEACON)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false)
                        .lightLevel((b) -> b.getValue(GTBlockStateProperties.ACTIVE) ? 15 : 0))
                .blockstate(MoniModels.createPrismaticActiveUpsideDownBeaconModel(MoniLabs.id(texturePath)))
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

    private static @NotNull BlockEntry<Block> registerSimpleBeacon(String name, String id, String texture,
                                                                   NonNullBiFunction<Block, Item.Properties, ? extends BlockItem> func) {
        return REGISTRATE
                .block(id, Block::new)
                .initialProperties(() -> Blocks.BEACON)
                .properties(p -> p.isValidSpawn((state, level, pos, ent) -> false))
                .blockstate((ctx, prov) -> prov.simpleBlock(ctx.getEntry(),
                        prov.models()
                                .withExistingParent(ctx.getName(), MoniLabs.id("block/beacon"))
                                .renderType("cutout")
                                .texture("frame", MoniLabs.id("block/" + texture + "/frame"))
                                .texture("core", MoniLabs.id("block/" + texture + "/core"))
                                .texture("particle", MoniLabs.id("block/" + texture + "/frame"))))
                .tag(RecipeTags.MINEABLE_WITH_WRENCH, BlockTags.MINEABLE_WITH_PICKAXE)
                .lang(name)
                .item(func)
                .build()
                .register();
    }

    public static BlockEntry<PrismaticActiveBlock> PRISMATIC_FOCUS = registerPrismaticActiveUpsideDownBeacon(
            "Prismatic Focus",
            "prismatic_focus", "beacon/prismatic_focus", PrismaticFocusItem::new);

    public static BlockEntry<Block> KNOWLEDGE_TRANSMISSION_ARRAY = registerSimpleBeacon("Knowledge Transmission Array",
            "knowledge_transmission_array", "beacon/knowledge_transmission_array", BlockItem::new);

    public static BlockEntry<Block> DIMENSIONAL_STABILIZATION_NETHERITE_CASING = registerSimpleBlock(
            "Dimensional Stabilization Netherite Casing", "dimensional_stabilization_netherite_casing",
            "casing/netherite", BlockItem::new);

    public static BlockEntry<Block> MICROVERSE_CASING = registerSimpleBlock("Microverse Casing", "microverse_casing",
            "casing/microverse", BlockItem::new);

    public static BlockEntry<Block> CRYOLOBUS_CASING = registerSimpleBlock("Cryolobus Casing", "cryolobus_casing",
            "casing/cryolobus", BlockItem::new);

    public static BlockEntry<Block> BIOALLOY_CASING = registerSimpleBlock("Bioalloy Casing", "bioalloy_casing",
            "casing/bioalloy", BlockItem::new);

    public static BlockEntry<Block> BIOALLOY_FUSION_CASING = registerSimpleBlock("Bioalloy Fusion Casing",
            "bioalloy_fusion_casing",
            "casing/bioalloy_fusion", BlockItem::new);
}

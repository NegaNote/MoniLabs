package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;

import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.render.MoniDynamicRenderHelper;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.data.materials.MoniMaterials;
import net.neganote.monilabs.common.machine.multiblock.*;
import net.neganote.monilabs.common.machine.part.*;
import net.neganote.monilabs.data.models.MoniMachineModels;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;
import net.neganote.monilabs.recipe.MoniRecipeModifiers;

import appeng.core.definitions.AEBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.BiConsumer;

@SuppressWarnings("unused")
public class MoniMachines {

    public static @NotNull BiConsumer<IMultiController, List<Component>> currentColorDisplayInfo() {
        return (controller, components) -> {
            if (controller instanceof PrismaticCrucibleMachine prismMachine && controller.isFormed()) {
                components.add(Component.translatable("monilabs.prismatic.current_color",
                        Component.translatable(prismMachine.getColorState().nameKey)));
            }
        };
    }

    public static @NotNull BiConsumer<IMultiController, List<Component>> currentDiversityPointsInfo() {
        return (controller, list) -> {
            if (controller instanceof OmnicSynthesizerMachine omnic) {
                list.add(Component.translatable("monilabs.omnic.current_diversity_points", omnic.diversityPoints));
            }
        };
    }

    public static MachineDefinition CHROMA_SENSOR_HATCH = MoniLabs.REGISTRATE
            .machine("chroma_sensor_hatch", ChromaSensorHatchPartMachine::new)
            .langValue("Chroma Sensor Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("monilabs.tooltip.chroma_sensor_hatch.0"),
                    Component.translatable("monilabs.tooltip.chroma_sensor_hatch.1"),
                    Component.translatable("monilabs.tooltip.chroma_sensor_hatch.2"))
            .modelProperty(RenderColor.COLOR_PROPERTY, RenderColor.NONE)
            .model(MoniMachineModels.createOverlayChromaCasingMachineModel("chroma_sensor", "casing/netherite"))
            .tier(GTValues.UHV)
            .register();

    public static MachineDefinition SCULK_XP_DRAINING_HATCH = MoniLabs.REGISTRATE
            .machine("sculk_xp_draining_hatch", SculkExperienceDrainingHatchPartMachine::new)
            .langValue("Sculk XP Draining Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("tooltip.monilabs.xp_draining_hatch.0"),
                    Component.translatable("tooltip.monilabs.xp_draining_hatch.1"),
                    Component.translatable("tooltip.monilabs.xp_draining_hatch.2"))
            .model(MoniMachineModels.createOverlayCasingMachineModel("exp_hatch_draining", "casing/cryolobus"))
            .tier(GTValues.ZPM)
            .register();

    public static MachineDefinition SCULK_XP_SENSOR_HATCH = MoniLabs.REGISTRATE
            .machine("sculk_xp_sensor_hatch", SculkExperienceSensorHatchPartMachine::new)
            .langValue("Sculk XP Sensor Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("tooltip.monilabs.xp_sensor_hatch.0"),
                    Component.translatable("tooltip.monilabs.xp_sensor_hatch.1"))
            .tier(GTValues.ZPM)
            .modelProperty(FillLevel.FILL_PROPERTY, FillLevel.EMPTY_TO_QUARTER)
            .model(MoniMachineModels.createOverlayFillLevelCasingMachineModel("exp_sensor", "casing/cryolobus"))
            .register();

    public static MachineDefinition MICROVERSE_INTEGRITY_SENSOR_HATCH = MoniLabs.REGISTRATE
            .machine("microverse_stability_sensor_hatch", MicroverseStabilitySensorHatchPartMachine::new)
            .langValue("Microverse Stability Sensor Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("tooltip.monilabs.microverse_stability_hatch.0"),
                    Component.translatable("tooltip.monilabs.microverse_stability_hatch.1"))
            .tier(GTValues.HV)
            .modelProperty(FillLevel.FILL_PROPERTY, FillLevel.EMPTY_TO_QUARTER)
            .model(MoniMachineModels.createOverlayFillLevelCasingMachineModel("stability_hatch", "casing/microverse"))
            .register();

    public static MultiblockMachineDefinition PRISMATIC_CRUCIBLE = MoniLabs.REGISTRATE
            .multiblock("prismatic_crucible", PrismaticCrucibleMachine::new)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.CHROMATIC_PROCESSING, MoniRecipeTypes.CHROMATIC_TRANSCENDENCE)
            .recipeModifiers(GTRecipeModifiers.OC_NON_PERFECT)
            .appearanceBlock(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("LLL#######LLL", "#############", "#############", "#############", "#############",
                            "#############", "#############", "#############", "#############", "#############")
                    .aisle("LLLLL###LLLLL", "#F#########F#", "#F#########F#", "#F#########F#", "#F#########F#",
                            "#F#########F#", "#F#########F#", "#F#########F#", "#F#L#####L#F#", "#LLL#####LLL#")
                    .aisle("LLLLLLLLLLLLL", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                            "##F#######F##", "##F#######F##", "##F#######F##", "##FLL###LLF##", "#LLLL###LLLL#")
                    .aisle("#LLCCCCCCCLL#", "###C#####C###", "###C#####C###", "###C#####C###", "###C#####C###",
                            "###C#####C###", "###C#####C###", "###C#####C###", "#LLCCC#CCCLL#", "#LLLL###LLLL#")
                    .aisle("#LLCLLCLLCLL#", "#####LCL#####", "######C######", "#############", "#############",
                            "#############", "#############", "#####LCL#####", "##LCLCCCLCL##", "##LL#####LL##")
                    .aisle("##LCLLLLLCL##", "####L###L####", "#############", "#############", "#############",
                            "#############", "######F######", "####LLCLL####", "###CCL#LCC###", "#############")
                    .aisle("##LCCLLLCCL##", "####C###C####", "####C###C####", "#############", "#############",
                            "#############", "#####FPF#####", "####CCCCC####", "####C###C####", "#############")
                    .aisle("##LCLLLLLCL##", "####L###L####", "#############", "#############", "#############",
                            "#############", "######F######", "####LLCLL####", "###CCL#LCC###", "#############")
                    .aisle("#LLCLLCLLCLL#", "#####LCL#####", "######C######", "#############", "#############",
                            "#############", "#############", "#####LCL#####", "##LCLCCCLCL##", "##LL#####LL##")
                    .aisle("#LLCCCCCCCLL#", "###C#####C###", "###C#####C###", "###C#####C###", "###C#####C###",
                            "###C#####C###", "###C#####C###", "###C#####C###", "#LLCCC#CCCLL#", "#LLLL###LLLL#")
                    .aisle("LLLLLLMLLLLLL", "##F#######F##", "##F#######F##", "##F#######F##", "##F#######F##",
                            "##F#######F##", "##F#######F##", "##F#######F##", "##FLL###LLF##", "#LLLL###LLLL#")
                    .aisle("LLLLL###LLLLL", "#F#########F#", "#F#########F#", "#F#########F#", "#F#########F#",
                            "#F#########F#", "#F#########F#", "#F#########F#", "#F#L#####L#F#", "#LLL#####LLL#")
                    .aisle("LLL#######LLL", "#############", "#############", "#############", "#############",
                            "#############", "#############", "#############", "#############", "#############")
                    .where("L",
                            Predicates.blocks(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING.get())
                                    .setMinGlobalLimited(197)
                                    .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2)
                                            .setPreviewCount(1))
                                    .or(Predicates.abilities(PartAbility.IMPORT_ITEMS).setPreviewCount(1))
                                    .or(Predicates.abilities(PartAbility.EXPORT_ITEMS).setPreviewCount(1))
                                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                                    .or(Predicates.abilities(PartAbility.EXPORT_FLUIDS).setPreviewCount(1))
                                    .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                    .or(Predicates.blocks(CHROMA_SENSOR_HATCH.getBlock()).setMaxGlobalLimited(1)))
                    .where("C", Predicates.blocks(MoniBlocks.CHROMODYNAMIC_CONDUCTION_CASING.get()))
                    .where("M", Predicates.controller(Predicates.blocks(definition.getBlock())))
                    .where("P", Predicates.blocks(MoniBlocks.PRISMATIC_FOCUS.get()))
                    .where("F", Predicates.frames(GTMaterials.Neutronium))
                    .where("#", Predicates.any())
                    .build())
            .tooltips(
                    Component.translatable("monilabs.tooltip.prismatic.0",
                            Component.translatable("monilabs.tooltip.prismatic.rainbow")
                                    .withStyle(TooltipHelper.RAINBOW_HSL_FAST)),
                    Component.translatable("monilabs.tooltip.prismatic.1"),
                    Component.translatable("monilabs.tooltip.prismatic.2"))
            .additionalDisplay(MoniMachines.currentColorDisplayInfo())
            .modelProperty(RecipeLogic.STATUS_PROPERTY, RecipeLogic.Status.IDLE)
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/netherite"),
                            GTCEu.id("block/multiblock/processing_array"))
                    .andThen(b -> b.addDynamicRenderer(MoniDynamicRenderHelper::createPrismacLaserRender)))
            .hasBER(true)
            .register();

    public static MultiblockMachineDefinition BASIC_MICROVERSE_PROJECTOR = MoniLabs.REGISTRATE
            .multiblock("basic_microverse_projector", (holder) -> new MicroverseProjectorMachine(holder, 1))
            .langValue("Basic Microverse Projector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.MICROVERSE_RECIPES)
            .recipeModifiers(MoniRecipeModifiers.MICROVERSE_OC)
            .appearanceBlock(MoniBlocks.MICROVERSE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCC", "CVC", "CCC")
                    .aisle("CCC", "GDG", "CCC")
                    .aisle("C@C", "CGC", "CCC")
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("D", Predicates.any())
                    .where("C", Predicates.blocks(MoniBlocks.MICROVERSE_CASING.get()).setMinGlobalLimited(10)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.blocks(MICROVERSE_INTEGRITY_SENSOR_HATCH.getBlock()).setMaxGlobalLimited(1)))
                    .where("G", Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where("V", Predicates.blocks(GTBlocks.CASING_GRATE.get()))
                    .build())
            .modelProperty(RecipeLogic.STATUS_PROPERTY, RecipeLogic.Status.IDLE)
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
            .tooltips(Component.translatable("tooltip.monilabs.basic_microverse_projector.description"))
            .hasBER(true)
            .register();

    public static MultiblockMachineDefinition ADVANCED_MICROVERSE_PROJECTOR = MoniLabs.REGISTRATE
            .multiblock("advanced_microverse_projector", (holder) -> new MicroverseProjectorMachine(holder, 2))
            .langValue("Advanced Microverse Projector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.MICROVERSE_RECIPES)
            .recipeModifiers(MoniRecipeModifiers.MICROVERSE_OC)
            .appearanceBlock(MoniBlocks.MICROVERSE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCCCC", "CGGGC", "CGGGC", "CGGGC", "CCCCC")
                    .aisle("CVCVC", "GDDDG", "GDDDG", "GDDDG", "CVCVC")
                    .aisle("CCCCC", "GDDDG", "GD#DG", "GDDDG", "CCCCC")
                    .aisle("CVCVC", "GDDDG", "GDDDG", "GDDDG", "CVCVC")
                    .aisle("CC@CC", "CGGGC", "CGGGC", "CGGGC", "CCCCC")
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("D", Predicates.any())
                    .where("C", Predicates.blocks(MoniBlocks.MICROVERSE_CASING.get()).setMinGlobalLimited(45)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.blocks(MICROVERSE_INTEGRITY_SENSOR_HATCH.getBlock()).setMaxGlobalLimited(1)))
                    .where("G", Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where("V", Predicates.blocks(GTBlocks.CASING_GRATE.get()))
                    .where("#", Predicates.any())
                    .build())
            .modelProperty(RecipeLogic.STATUS_PROPERTY, RecipeLogic.Status.IDLE)
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
            .tooltips(Component.translatable("tooltip.monilabs.advanced_microverse_projector.description"))
            .hasBER(true)
            .register();

    public static MultiblockMachineDefinition ELITE_MICROVERSE_PROJECTOR = MoniLabs.REGISTRATE
            .multiblock("elite_microverse_projector", (holder) -> new MicroverseProjectorMachine(holder, 3))
            .langValue("Elite Microverse Projector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.MICROVERSE_RECIPES)
            .recipeModifiers(MoniRecipeModifiers.MICROVERSE_OC)
            .appearanceBlock(MoniBlocks.MICROVERSE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("#########", "#########", "##CCCCC##", "##CVCVC##", "##CCCCC##", "##CVCVC##", "##CCCCC##",
                            "#########", "#########")
                    .aisle("#########", "##CGGGC##", "#CDDDDDC#", "#CDDDDDC#", "#CDDDDDC#", "#CDDDDDC#", "#CDDDDDC#",
                            "##CGGGC##", "#########")
                    .aisle("##CCCCC##", "#CDDDDDC#", "CDDDDDDDC", "CDDDDDDDC", "CDDDDDDDC", "CDDDDDDDC", "CDDDDDDDC",
                            "#CDDDDDC#", "##CCCCC##")
                    .aisle("##CGGGC##", "#GDDDDDG#", "CDDDDDDDC", "GDD###DDG", "GDD###DDG", "GDD###DDG", "CDDDDDDDC",
                            "#GDDDDDG#", "##CGGGC##")
                    .aisle("##CGGGC##", "#GDDDDDG#", "CDDDDDDDC", "GDD###DDG", "GDD###DDG", "GDD###DDG", "CDDDDDDDC",
                            "#GDDDDDG#", "##CGGGC##")
                    .aisle("##CGGGC##", "#GDDDDDG#", "CDDDDDDDC", "GDD###DDG", "GDD###DDG", "GDD###DDG", "CDDDDDDDC",
                            "#GDDDDDG#", "##CGGGC##")
                    .aisle("##CCCCC##", "#CDDDDDC#", "CDDDDDDDC", "CDDDDDDDC", "CDDDDDDDC", "CDDDDDDDC", "CDDDDDDDC",
                            "#CDDDDDC#", "##CCCCC##")
                    .aisle("#########", "##CGGGC##", "#CDDDDDC#", "#CDDDDDC#", "#CDDDDDC#", "#CDDDDDC#", "#CDDDDDC#",
                            "##CGGGC##", "#########")
                    .aisle("#########", "#########", "##CC@CC##", "##CGGGC##", "##CGGGC##", "##CGGGC##", "##CCCCC##",
                            "#########", "#########")
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("D", Predicates.any())
                    .where("C", Predicates.blocks(MoniBlocks.MICROVERSE_CASING.get()).setMinGlobalLimited(125)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.blocks(MICROVERSE_INTEGRITY_SENSOR_HATCH.getBlock()).setMaxGlobalLimited(1)))
                    .where("G", Predicates.blocks(GTBlocks.CASING_TEMPERED_GLASS.get()))
                    .where("V", Predicates.blocks(GTBlocks.CASING_TITANIUM_PIPE.get()))
                    .where("#", Predicates.any())
                    .build())
            .modelProperty(RecipeLogic.STATUS_PROPERTY, RecipeLogic.Status.IDLE)
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
            .tooltips(Component.translatable("tooltip.monilabs.elite_microverse_projector.description"))
            .hasBER(true)
            .register();

    public static MultiblockMachineDefinition HYPERBOLIC_MICROVERSE_PROJECTOR = MoniLabs.REGISTRATE
            .multiblock("hyperbolic_microverse_projector", (holder) -> new MicroverseProjectorMachine(holder, 4))
            .langValue("Hyperbolic Microverse Projector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.MICROVERSE_RECIPES)
            .recipeModifiers(MoniRecipeModifiers.MICROVERSE_PARALLEL_HATCH, MoniRecipeModifiers.MICROVERSE_OC)
            .appearanceBlock(MoniBlocks.MICROVERSE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("###CCCCC###", "###N###N###", "###N###N###", "###N###N###", "###N###N###", "###N###N###",
                            "###N###N###", "###N###N###", "###N###N###", "###N###N###", "###CCCCC###")
                    .aisle("#CCCCVCCCC#", "###########", "###########", "###########", "###########", "###########",
                            "###########", "###########", "###########", "###########", "#CCCCVCCCC#")
                    .aisle("#CVCCCCCVC#", "###CGGGC###", "###########", "###########", "###########", "###########",
                            "###########", "###########", "###########", "###CGGGC###", "#CVCCCCCVC#")
                    .aisle("CCCCCCCCCCC", "N#CCDDDCC#N", "N##CGGGC##N", "N#########N", "N#########N", "N#########N",
                            "N#########N", "N#########N", "N##CGGGC##N", "N#CCDDDCC#N", "CCCCCCCCCCC")
                    .aisle("CCCCCCCCCCC", "##GDDDDDG##", "###GYDYG###", "####YGY####", "####YGY####", "####YGY####",
                            "####YGY####", "####YGY####", "###GYDYG###", "##GDDDDDG##", "CCCCCCCCCCC")
                    .aisle("CVCCCWCCCVC", "##GDDDDDG##", "###GDDDG###", "####GDG####", "####GDG####", "####GDG####",
                            "####GDG####", "####GDG####", "###GDDDG###", "##GDDDDDG##", "CVCCCWCCCVC")
                    .aisle("CCCCCCCCCCC", "##GDDDDDG##", "###GYDYG###", "####YGY####", "####YGY####", "####YGY####",
                            "####YGY####", "####YGY####", "###GYDYG###", "##GDDDDDG##", "CCCCCCCCCCC")
                    .aisle("CCCCCCCCCCC", "N#CCDDDCC#N", "N##CGGGC##N", "N#########N", "N#########N", "N#########N",
                            "N#########N", "N#########N", "N##CGGGC##N", "N#CCDDDCC#N", "CCCCCCCCCCC")
                    .aisle("#CVCCCCCVC#", "###CGGGC###", "###########", "###########", "###########", "###########",
                            "###########", "###########", "###########", "###CGGGC###", "#CVCCCCCVC#")
                    .aisle("#CCCCVCCCC#", "###########", "###########", "###########", "###########", "###########",
                            "###########", "###########", "###########", "###########", "#CCCCVCCCC#")
                    .aisle("###CC@CC###", "###N###N###", "###N###N###", "###N###N###", "###N###N###", "###N###N###",
                            "###N###N###", "###N###N###", "###N###N###", "###N###N###", "###CCCCC###")
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("D", Predicates.any())
                    .where("C", Predicates.blocks(MoniBlocks.MICROVERSE_CASING.get()).setMinGlobalLimited(195)
                            .or(Predicates.autoAbilities(definition.getRecipeTypes()))
                            .or(Predicates.abilities(PartAbility.PARALLEL_HATCH).setMaxGlobalLimited(1))
                            .or(Predicates.blocks(MICROVERSE_INTEGRITY_SENSOR_HATCH.get()).setMaxGlobalLimited(1)))
                    .where("G", Predicates.blocks(GTBlocks.FUSION_GLASS.get()))
                    .where("N", Predicates.frames(MoniMaterials.SculkBioalloy))
                    .where("V", Predicates.blocks(AEBlocks.QUARTZ_VIBRANT_GLASS.block()))
                    .where("W", Predicates.blocks(ForgeRegistries.BLOCKS
                            .getValue(MoniLabs.kjsResLoc("universal_warp_core"))))
                    .where("Y", Predicates.blocks(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING.get()))
                    .where("#", Predicates.any())
                    .build())
            .modelProperty(RecipeLogic.STATUS_PROPERTY, RecipeLogic.Status.IDLE)
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
            .tooltips(Component.translatable("gtceu.multiblock.parallelizable.tooltip"),
                    Component.translatable("tooltip.monilabs.basic_microverse_projector.description"))
            .hasBER(true)
            .register();

    public static MultiblockMachineDefinition CREATIVE_ENERGY_MULTI = MoniLabs.REGISTRATE
            .multiblock("creative_energy_multi", CreativeEnergyMultiMachine::new)
            .langValue("Transdimensional Energy Singularity")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.CREATIVE_ENERGY_MULTI_RECIPES)
            .appearanceBlock(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "###############")
                    .aisle("#####NNNNN#####", "#####NNNNN#####", "#######F#######", "#######F#######", "#######F#######", "###############", "###############", "###############", "###############", "#######H#######", "#######H#######", "#######H#######", "#####AAHAA#####", "#######H#######", "#####AAHAA#####", "#######H#######", "#######H#######", "#######H#######", "#######R#######")
                    .aisle("###NNNNNNNNN###", "###NNNNNNNNN###", "######AAA######", "######AAA######", "######AAA######", "#######F#######", "#######F#######", "###############", "#######H#######", "#######H#######", "#######H#######", "###############", "####A##P##A####", "###############", "####A##P##A####", "###############", "###############", "###############", "###############")
                    .aisle("##NNNNNNNNNNN##", "##NNNNNNNNNNN##", "#######F#######", "#######F#######", "######AAA######", "######AAA######", "######AAA######", "#####CCCCC#####", "#######H#######", "#####CCCCC#####", "###############", "###############", "###A#######A###", "###############", "###A#######A###", "###############", "###############", "###############", "###############")
                    .aisle("##NNNNNNNNNNN##", "##NNNNAAANNNN##", "###############", "###############", "###############", "###############", "###############", "####CC###CC####", "#######H#######", "####CC###CC####", "###############", "###############", "##A#########A##", "###############", "##A#########A##", "###############", "###############", "###############", "###############")
                    .aisle("#NNNNNNNNNNNNN#", "#NNNNAAAAANNNN#", "#####VAAAV#####", "#####V###V#####", "###############", "###############", "###############", "###CC#####CC###", "###############", "###CC#####CC###", "###############", "###############", "#A###########A#", "###############", "#A###########A#", "###############", "###############", "###############", "###############")
                    .aisle("#NNNNNNNNNNNNN#", "#NNNAAAAAAANNN#", "##A##AAAAA##A##", "##A###AAA###A##", "##AA##VAV##AA##", "###A##V#V##A###", "###A#######A###", "###C#######C###", "###############", "###C#######C###", "###############", "###############", "#A###########A#", "###############", "#A###########A#", "###############", "###############", "###############", "###############")
                    .aisle("#NNNNNNNNNNNNN#", "#NNNAAAAAAANNN#", "#FAF#AAAAA#FAF#", "#FAF##AAA##FAF#", "#FAA##AAA##AAF#", "##FA###A###AF##", "##FA###A###AF##", "###C###A###C###", "##HHH##P##HHH##", "#HHC#######CHH#", "#HH#########HH#", "HH###########HH", "HHP#########PHH", "HH###########HH", "HHP#########PHH", "HH###########HH", "HH###########HH", "HH###########HH", "#R###########R#")
                    .aisle("#NNNNNNNNNNNNN#", "#NNNAAAAAAANNN#", "##A##AAAAA##A##", "##A###AAA###A##", "##AA##VAV##AA##", "###A##V#V##A###", "###A#######A###", "###C#######C###", "###############", "###C#######C###", "###############", "###############", "#A###########A#", "###############", "#A###########A#", "###############", "###############", "###############", "###############")
                    .aisle("#NNNNNNNNNNNNN#", "#NNNNAAAAANNNN#", "#####VAAAV#####", "#####V###V#####", "###############", "###############", "###############", "###CC#####CC###", "###############", "###CC#####CC###", "###############", "###############", "#A###########A#", "###############", "#A###########A#", "###############", "###############", "###############", "###############")
                    .aisle("##NNNNNNNNNNN##", "##NNNNAAANNNN##", "###############", "###############", "###############", "###############", "###############", "####CC###CC####", "#######H#######", "####CC###CC####", "###############", "###############", "##A#########A##", "###############", "##A#########A##", "###############", "###############", "###############", "###############")
                    .aisle("##NNNNNNNNNNN##", "##NNNNNNNNNNN##", "#######F#######", "#######F#######", "######AAA######", "######AAA######", "######AAA######", "#####CCCCC#####", "#######H#######", "#####CCCCC#####", "###############", "###############", "###A#######A###", "###############", "###A#######A###", "###############", "###############", "###############", "###############")
                    .aisle("###NNNNNNNNN###", "###NNNNNNNNN###", "######AAA######", "######AAA######", "######AAA######", "#######F#######", "#######F#######", "###############", "#######H#######", "#######H#######", "#######H#######", "###############", "####A##P##A####", "###############", "####A##P##A####", "###############", "###############", "###############", "###############")
                    .aisle("#####NN@NN#####", "#####NNNNN#####", "#######F#######", "#######F#######", "#######F#######", "###############", "###############", "###############", "###############", "#######H#######", "#######H#######", "#######H#######", "#####AAHAA#####", "#######H#######", "#####AAHAA#####", "#######H#######", "#######H#######", "#######H#######", "#######R#######")
                    .aisle("###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "#######H#######", "###############")
                    // spotless:on
                    .where("N",
                            Predicates.blocks(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING.get())
                                    .setMinGlobalLimited(226)
                                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setPreviewCount(1))
                                    .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1)))
                    .where("A", Predicates.blocks(GCYMBlocks.CASING_ATOMIC.get()))
                    .where("F", Predicates.frames(MoniMaterials.Eltz))
                    .where("V", Predicates.blocks(GCYMBlocks.HEAT_VENT.get()))
                    .where("P", Predicates.blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where("C", Predicates.blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                    .where("H", Predicates.blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where("R", Predicates.blocks(GTBlocks.MACHINE_CASING_UEV.get()))
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("#", Predicates.any())
                    .build())
            .modelProperty(RecipeLogic.STATUS_PROPERTY, RecipeLogic.Status.IDLE)
            .model(GTMachineModels.createWorkableCasingMachineModel(MoniLabs.id("block/casing/netherite"),
                    GTCEu.id("block/multiblock/processing_array"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createCreativeEnergyRender)))
            .register();

    public static MultiblockMachineDefinition CREATIVE_DATA_MULTI = MoniLabs.REGISTRATE
            .multiblock("creative_data_multi", CreativeDataMultiMachine::new)
            .langValue("Omniscience Research Beacon")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.CREATIVE_DATA_MULTI_RECIPES)
            .appearanceBlock(MoniBlocks.BIOALLOY_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#####CbbbC#####", "###############", "###############", "###############")
                    .aisle("####BBBBBBB####", "####BRBBBRB####", "####BRB#BRB####", "####BRB#BRB####", "####BBB#BBB####", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#####CbbbC#####", "###bb#####bb###", "###############", "###############", "###############")
                    .aisle("###HBBBBBBBH###", "###BBABBBABB###", "###FBAB#BABF###", "####BAB#BAB####", "####BBB#BBB####", "######F#F######", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#####CbbbC#####", "###bb#####bb###", "##b#########b##", "###############", "###############", "###############")
                    .aisle("##HHBBBBBBBHH##", "##BBBABBBABBB##", "##F#########F##", "###F#######F###", "###############", "###############", "######F#F######", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "######CbC######", "####bb###bb####", "##bb#######bb##", "#b###########b#", "###############", "###############", "###############")
                    .aisle("##HHBBBBBBBHH##", "##BBBAAAAABBB##", "###############", "###############", "####F#####F####", "####F#####F####", "####F#####F####", "######F#F######", "######F#F######", "###############", "###############", "###############", "###############", "###############", "###############", "####bbCbCbb####", "###b#######b###", "##b#########b##", "#b###########b#", "###############", "###############", "###############")
                    .aisle("BBBHBBBBBBBHBBB", "#BBBBBBABBBBBB#", "#F####BBB####F#", "##F#########F##", "###############", "###############", "###############", "#####F###F#####", "#####F###F#####", "#####FFFFF#####", "#####FF#FF#####", "######F#F######", "######F#F######", "######F#F######", "######CCC######", "####bb###bb####", "##Cb#######bC##", "#C###########C#", "C#############C", "###############", "###############", "###############")
                    .aisle("BBBHBBBBBBBHBBB", "#BBBBBBABBBBBB#", "#####HDGDH#####", "######DGD######", "###F##DGD##F###", "###F##DGD##F###", "####F#DGD#F####", "####F#DGD#F####", "####F#DGD#F####", "#####FDGDF#####", "#####FDGDF#####", "#####FDGDF#####", "#####FDGDF#####", "#####FDGDF#####", "#####CbbbC#####", "###CC#####CC###", "##b#########b##", "#b###########b#", "b#############b", "###############", "###############", "###############")
                    .aisle("BBBHHHHBHHHHBBB", "#BBBBBHAHBBBBB#", "#####HHAHH#####", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "######GAG######", "#####CbAbC#####", "###bb##A##bb###", "##b####A####b##", "#b#####A#####b#", "b######A######b", "#######A#######", "#######A#######", "#######I#######")
                    .aisle("BBBHBBBBBBBHBBB", "#BBBBBBBBBBBBB#", "#####HDGDH#####", "######DGD######", "###F##DGD##F###", "###F##DGD##F###", "####F#DGD#F####", "####F#DGD#F####", "####F#DGD#F####", "#####FDGDF#####", "#####FDGDF#####", "#####FDGDF#####", "#####FDGDF#####", "#####FDGDF#####", "#####CbbbC#####", "###CC#####CC###", "##b#########b##", "#b###########b#", "b#############b", "###############", "###############", "###############")
                    .aisle("BBBHBBBBBBBHBBB", "#BBBBBBBBBBBBB#", "#F####B@B####F#", "##F#########F##", "###############", "###############", "###############", "#####F###F#####", "#####F###F#####", "#####FF#FF#####", "#####FF#FF#####", "######F#F######", "######F#F######", "######F#F######", "######CCC######", "####bb###bb####", "##Cb#######bC##", "#C###########C#", "C#############C", "###############", "###############", "###############")
                    .aisle("##HHBBBBBBBHH##", "##BBBBBBBBBBB##", "###############", "###############", "####F#####F####", "####F#####F####", "####F#F#F#F####", "######F#F######", "######F#F######", "###############", "###############", "###############", "###############", "###############", "###############", "####bbCbCbb####", "###b#######b###", "##b#########b##", "#b###########b#", "###############", "###############", "###############")
                    .aisle("##HHBBBBBBBHH##", "##BBBBBBBBBBB##", "##F#########F##", "###F#######F###", "######F#F######", "######F#F######", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "######CbC######", "####bb###bb####", "##bb#######bb##", "#b###########b#", "###############", "###############", "###############")
                    .aisle("###HHBBBBBHH###", "###BBBBBBBBB###", "###F#######F###", "#####F###F#####", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#####CbbbC#####", "###bb#####bb###", "##b#########b##", "###############", "###############", "###############")
                    .aisle("#####BBBBB#####", "#####BBBBB#####", "#####F###F#####", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#####CbbbC#####", "###bb#####bb###", "###############", "###############", "###############")
                    .aisle("#####BBBBB#####", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "###############", "#####CbbbC#####", "###############", "###############", "###############")
                    // spotless:on
                    .where("#", Predicates.any())
                    .where("@", Predicates.controller(Predicates.blocks(definition.get())))
                    .where("G", Predicates.blocks(ForgeRegistries.BLOCKS.getValue(MoniLabs.kjsResLoc("prism_glass"))))
                    .where("A", Predicates.blocks(GTBlocks.ADVANCED_COMPUTER_CASING.get()))
                    .where("C", Predicates.blocks(GTBlocks.COMPUTER_CASING.get()))
                    .where("H", Predicates.blocks(GTBlocks.COMPUTER_HEAT_VENT.get()))
                    .where("D", Predicates.blocks(MoniBlocks.BIOALLOY_FUSION_CASING.get()))
                    .where("F", Predicates.frames(MoniMaterials.CrystalMatrix))
                    .where("I", Predicates.blocks(MoniBlocks.KNOWLEDGE_TRANSMISSION_ARRAY.get()))
                    .where("R", Predicates.abilities(PartAbility.COMPUTATION_DATA_RECEPTION))
                    .where("b", Predicates.blocks(MoniBlocks.BIOALLOY_CASING.get()))
                    .where("B",
                            Predicates.blocks(MoniBlocks.BIOALLOY_CASING.get()).setMinGlobalLimited(268)
                                    .or(Predicates.abilities(PartAbility.IMPORT_FLUIDS).setMinGlobalLimited(1))
                                    .or(Predicates.abilities(PartAbility.MAINTENANCE).setExactLimit(1))
                                    .or(Predicates.abilities(PartAbility.INPUT_ENERGY).setMaxGlobalLimited(2)
                                            .setPreviewCount(1)))
                    .build())
            .modelProperty(RecipeLogic.STATUS_PROPERTY, RecipeLogic.Status.IDLE)
            .model(GTMachineModels.createWorkableCasingMachineModel(MoniLabs.id("block/casing/bioalloy"),
                    GTCEu.id("block/multiblock/processing_array"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createCreativeDataRender)))
            .register();

    public static void init() {}
}

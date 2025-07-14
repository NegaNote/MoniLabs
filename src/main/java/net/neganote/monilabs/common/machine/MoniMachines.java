package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.api.pattern.Predicates;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;
import com.gregtechceu.gtceu.common.data.GTRecipeModifiers;
import com.gregtechceu.gtceu.common.data.models.GTMachineModels;

import net.minecraft.network.chat.Component;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.render.MoniDynamicRenderHelper;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
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
            .additionalDisplay(MoniMachines.currentColorDisplayInfo())
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
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
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
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
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
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
            .hasBER(true)
            .register();

    public static MultiblockMachineDefinition HYPERBOLIC_MICROVERSE_PROJECTOR = MoniLabs.REGISTRATE
            .multiblock("hyperbolic_microverse_projector", (holder) -> new MicroverseProjectorMachine(holder, 4))
            .langValue("Hyperbolic Microverse Projector")
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.MICROVERSE_RECIPES)
            .recipeModifiers(MoniRecipeModifiers.MICROVERSE_OC)
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
                    .where("N", Predicates.frames(GTMaterials.get("sculk_bioalloy")))
                    .where("V", Predicates.blocks(AEBlocks.QUARTZ_VIBRANT_GLASS.block()))
                    .where("W", Predicates.blocks(ForgeRegistries.BLOCKS
                            .getValue(MoniLabs.kjsResLoc("universal_warp_core"))))
                    .where("Y", Predicates.blocks(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING.get()))
                    .where("#", Predicates.any())
                    .build())
            .model(GTMachineModels
                    .createWorkableCasingMachineModel(MoniLabs.id("block/casing/microverse"),
                            MoniLabs.id("block/machines/projectors"))
                    .andThen(b -> b.addDynamicRenderer(
                            MoniDynamicRenderHelper::createMicroverseProjectorRender)))
            .hasBER(true)
            .register();

    public static void init() {}
}

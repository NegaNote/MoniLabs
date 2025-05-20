package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.data.GCYMBlocks;
import com.gregtechceu.gtceu.common.data.GTBlocks;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.renderer.PrismaticCrucibleRenderer;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.machine.multiblock.CreativeEnergyMultiMachine;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static net.neganote.monilabs.MoniLabs.REGISTRATE;
import static net.neganote.monilabs.common.block.MoniBlocks.PRISMATIC_FOCUS;

@SuppressWarnings("unused")
public class MoniMachines {

    static {
        REGISTRATE.creativeModeTab(() -> MoniLabs.MONI_CREATIVE_TAB);
    }

    public static MultiblockMachineDefinition PRISMATIC_CRUCIBLE = REGISTRATE
            .multiblock("prismatic_crucible", PrismaticCrucibleMachine::new)
            .appearanceBlock(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING)
            .rotationState(RotationState.NON_Y_AXIS)
            .allowExtendedFacing(false)
            .recipeTypes(MoniRecipeTypes.PRISMATIC_CRUCIBLE_RECIPES)

            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("LLL       LLL", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                    .aisle("LLLLL   LLLLL", " F         F ", " F         F ", " F         F ", " F         F ", " F         F ", " F         F ", " F         F ", " F L     L F ", " LLL     LLL ")
                    .aisle("LLLLLLLLLLLLL", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  FLL   LLF  ", " LLLL   LLLL ")
                    .aisle(" LLCCCCCCCLL ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", " LLCCC CCCLL ", " LLLL   LLLL ")
                    .aisle(" LLCLLCLLCLL ", "     LCL     ", "      C      ", "             ", "             ", "             ", "             ", "     LCL     ", "  LCLCCCLCL  ", "  LL     LL  ")
                    .aisle("  LCLLLLLCL  ", "    L   L    ", "             ", "             ", "             ", "             ", "      F      ", "    LLCLL    ", "   CCL LCC   ", "             ")
                    .aisle("  LCCLLLCCL  ", "    C   C    ", "    C   C    ", "             ", "             ", "             ", "     FPF     ", "    CCCCC    ", "    C   C    ", "             ")
                    .aisle("  LCLLLLLCL  ", "    L   L    ", "             ", "             ", "             ", "             ", "      F      ", "    LLCLL    ", "   CCL LCC   ", "             ")
                    .aisle(" LLCLLCLLCLL ", "     LCL     ", "      C      ", "             ", "             ", "             ", "             ", "     LCL     ", "  LCLCCCLCL  ", "  LL     LL  ")
                    .aisle(" LLCCCCCCCLL ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", "   C     C   ", " LLCCC CCCLL ", " LLLL   LLLL ")
                    .aisle("LLLLLLMLLLLLL", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  F       F  ", "  FLL   LLF  ", " LLLL   LLLL ")
                    .aisle("LLLLL   LLLLL", " F         F ", " F         F ", " F         F ", " F         F ", " F         F ", " F         F ", " F         F ", " F L     L F ", " LLL     LLL ")
                    .aisle("LLL       LLL", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ", "             ")
                    // spotless:on
                    .where('L',
                            blocks(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING.get()).setMinGlobalLimited(9)
                                    .or(autoAbilities(definition.getRecipeTypes()))
                                    .or(autoAbilities(true, false, false)))
                    .where('C', blocks(MoniBlocks.CHROMODYNAMIC_CONDUCTION_CASING.get()))
                    .where('M', controller(blocks(definition.getBlock())))
                    .where('P', blocks(PRISMATIC_FOCUS.get()))
                    .where('F', frames(GTMaterials.Neutronium))
                    .where(' ', any())
                    .build())
            .additionalDisplay((controller, components) -> {
                if (controller instanceof PrismaticCrucibleMachine prismMachine && controller.isFormed()) {
                    components.add(Component.translatable("monilabs.prismatic.current_color",
                            Component.translatable(prismMachine.getColorState().nameKey)));
                }
            })
            .tooltipBuilder((stack, list) -> {
                list.add(Component.translatable("monilabs.tooltip.prismatic.0",
                        Component.translatable("monilabs.tooltip.prismatic.rainbow")
                                .withStyle(TooltipHelper.RAINBOW_HSL_FAST)));
                list.add(Component.translatable("monilabs.tooltip.prismatic.1"));
                list.add(Component.translatable("monilabs.tooltip.prismatic.2"));
            })
            .hasTESR(true)
            .renderer(PrismaticCrucibleRenderer::new)
            .register();

    public static MultiblockMachineDefinition OMNIC_SYNTHESIZER = REGISTRATE
            .multiblock("omnic_synthesizer", OmnicSynthesizerMachine::new)
            .recipeTypes(MoniRecipeTypes.OMNIC_SYNTHESIZER_RECIPES)
            .appearanceBlock(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING)
            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("#CCCCC#", "#CCCCC#", "#CGGGC#", "#CGGGC#", "#CGGGC#", "#CGGGC#", "#CGGGC#", "#CCCCC#",
                            "#CCCCC#")
                    .aisle("CCCCCCC", "CHMMMHC", "CH   HC", "CH   HC", "CH   HC", "CH   HC", "CH   HC", "CHMMMHC",
                            "CCCCCCC")
                    .aisle("CCCCCCC", "CMXYXMC", "G XYX G", "G XYX G", "G XYX G", "G XYX G", "G XYX G", "CMXYXMC",
                            "CCCCCCC")
                    .aisle("CCCCCCC", "CMYYYMC", "G YYY G", "G YYY G", "G YYY G", "G YYY G", "G YYY G", "CMYYYMC",
                            "CCCCCCC")
                    .aisle("CCCCCCC", "CMXYXMC", "G XYX G", "G XYX G", "G XYX G", "G XYX G", "G XYX G", "CMXYXMC",
                            "CCCCCCC")
                    .aisle("CCCCCCC", "CHMMMHC", "CH   HC", "CH   HC", "CH   HC", "CH   HC", "CH   HC", "CHMMMHC",
                            "CCCCCCC")
                    .aisle("CCCCCCC", "CCCCCCC", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#CCCCC#", "#CCCCC#", "CCCCCCC",
                            "CCCCCCC")
                    .aisle("CCCCCCC", "#CCCCC#", "#CCCCC#", "#CNNNC#", "###N###", "###N###", "###N###", "#CNNNC#",
                            "CCCCCCC")
                    .aisle("#CCCCC#", "##C@C##", "##CCC##", "#######", "#######", "#######", "#######", "#######",
                            "#CCCCC#")
                    .where("@", controller(blocks(definition.get())))
                    .where("G", blocks(GTBlocks.CLEANROOM_GLASS.get()))
                    .where("H", blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where("M", frames(GTMaterials.get("crystal_matrix")))
                    .where("N", frames(GTMaterials.NaquadahAlloy))
                    .where("X", blocks(GTBlocks.COMPUTER_CASING.get()))
                    .where("Y", blocks(GTBlocks.ADVANCED_COMPUTER_CASING.get()))
                    .where("C", blocks(GCYMBlocks.CASING_ATOMIC.get()).setMinGlobalLimited(220)
                            .or(autoAbilities(definition.getRecipeTypes()))
                            .or(abilities(PartAbility.COMPUTATION_DATA_RECEPTION).setExactLimit(1)))
                    .where(" ", air())
                    .where("#", any())
                    .build())
            .additionalDisplay((controller, list) -> {
                if (controller instanceof OmnicSynthesizerMachine omnic) {
                    list.add(Component.translatable("monilabs.omnic.current_diversity_points", omnic.diversityPoints));
                }
            })
            .workableCasingRenderer(GTCEu.id("block/casings/gcym/atomic_casing"),
                    GTCEu.id("block/multiblock/fusion_reactor"))
            .recipeModifiers(OmnicSynthesizerMachine.recipeModifier())
            .register();

    public static MultiblockMachineDefinition CREATIVE_ENERGY_MULTI = REGISTRATE
            .multiblock("creative_energy_multi", CreativeEnergyMultiMachine::new)
            .langValue("Black Hole Energy Accumulation and Translocation Station")
            .appearanceBlock(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.CREATIVE_ENERGY_RECIPES)
            .pattern(definition -> FactoryBlockPattern.start()
                    // spotless:off
                    .aisle("               ","               ","               ","               ","               ","               ","               ","               ","               ","               ","               ","       H       ","       H       ","       H       ","       H       ","       H       ","       H       ","       H       ","               ")
                    .aisle("     NNNNN     ","     NNNNN     ","       F       ","       F       ","       F       ","               ","               ","               ","               ","       H       ","       H       ","       H       ","     AAHAA     ","       H       ","     AAHAA     ","       H       ","       H       ","       H       ","       R       ")
                    .aisle("   NNNNNNNNN   ","   NNNNNNNNN   ","      AAA      ","      AAA      ","      AAA      ","       F       ","       F       ","               ","       H       ","       H       ","       H       ","               ","    A  P  A    ","               ","    A  P  A    ","               ","               ","               ","               ")
                    .aisle("  NNNNNNNNNNN  ","  NNNNNNNNNNN  ","       F       ","       F       ","      AAA      ","      AAA      ","      AAA      ","     CCCCC     ","       H       ","     CCCCC     ","               ","               ","   A       A   ","               ","   A       A   ","               ","               ","               ","               ")
                    .aisle("  NNNNNNNNNNN  ","  NNNNAAANNNN  ","               ","               ","               ","               ","               ","    CC   CC    ","       H       ","    CC   CC    ","               ","               ","  A         A  ","               ","  A         A  ","               ","               ","               ","               ")
                    .aisle(" NNNNNNNNNNNNN "," NNNNAAAAANNNN ","     VAAAV     ","     V   V     ","               ","               ","               ","   CC     CC   ","               ","   CC     CC   ","               ","               "," A           A ","               "," A           A ","               ","               ","               ","               ")
                    .aisle(" NNNNNNNNNNNNN "," NNNAAAAAAANNN ","  A  AAAAA  A  ","  A   AAA   A  ","  AA  VAV  AA  ","   A  V V  A   ","   A       A   ","   C       C   ","               ","   C       C   ","               ","               "," A           A ","               "," A           A ","               ","               ","               ","               ")
                    .aisle(" NNNNNNNNNNNNN "," NNNAAAAAAANNN "," FAF AAAAA FAF "," FAF  AAA  FAF "," FAA  AAA  AAF ","  FA   A   AF  ","  FA   A   AF  ","   C   A   C   ","  HHH  P  HHH  "," HHC       CHH "," HH         HH ","HH           HH","HHP         PHH","HH           HH","HHP         PHH","HH           HH","HH           HH","HH           HH"," R           R ")
                    .aisle(" NNNNNNNNNNNNN "," NNNAAAAAAANNN ","  A  AAAAA  A  ","  A   AAA   A  ","  AA  VAV  AA  ","   A  V V  A   ","   A       A   ","   C       C   ","               ","   C       C   ","               ","               "," A           A ","               "," A           A ","               ","               ","               ","               ")
                    .aisle(" NNNNNNNNNNNNN "," NNNNAAAAANNNN ","     VAAAV     ","     V   V     ","               ","               ","               ","   CC     CC   ","               ","   CC     CC   ","               ","               "," A           A ","               "," A           A ","               ","               ","               ","               ")
                    .aisle("  NNNNNNNNNNN  ","  NNNNAAANNNN  ","               ","               ","               ","               ","               ","    CC   CC    ","       H       ","    CC   CC    ","               ","               ","  A         A  ","               ","  A         A  ","               ","               ","               ","               ")
                    .aisle("  NNNNNNNNNNN  ","  NNNNNNNNNNN  ","       F       ","       F       ","      AAA      ","      AAA      ","      AAA      ","     CCCCC     ","       H       ","     CCCCC     ","               ","               ","   A       A   ","               ","   A       A   ","               ","               ","               ","               ")
                    .aisle("   NNNNNNNNN   ","   NNNNNNNNN   ","      AAA      ","      AAA      ","      AAA      ","       F       ","       F       ","               ","       H       ","       H       ","       H       ","               ","    A  P  A    ","               ","    A  P  A    ","               ","               ","               ","               ")
                    .aisle("     NN@NN     ","     NNNNN     ","       F       ","       F       ","       F       ","               ","               ","               ","               ","       H       ","       H       ","       H       ","     AAHAA     ","       H       ","     AAHAA     ","       H       ","       H       ","       H       ","       R       ")
                    .aisle("               ","               ","               ","               ","               ","               ","               ","               ","               ","               ","               ","       H       ","       H       ","       H       ","       H       ","       H       ","       H       ","       H       ","               ")
                    // spotless:on
                    .where("N", blocks(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING.get())
                            .or(autoAbilities(definition.getRecipeTypes())))
                    .where("A", blocks(GCYMBlocks.CASING_ATOMIC.get()))
                    .where("F", frames(GTMaterials.NaquadahAlloy))
                    .where("V", blocks(GCYMBlocks.HEAT_VENT.get()))
                    .where("P", blocks(GTBlocks.CASING_POLYTETRAFLUOROETHYLENE_PIPE.get()))
                    .where("C", blocks(GTBlocks.SUPERCONDUCTING_COIL.get()))
                    .where("H", blocks(GTBlocks.HIGH_POWER_CASING.get()))
                    .where("R", blocks(GTBlocks.MACHINE_CASING_UIV.get()))
                    .where("@", controller(blocks(definition.getBlock())))
                    .where(" ", any())
                    .build())
            .workableCasingRenderer(MoniLabs.id("block/dimensional_stabilization_netherite_casing"),
                    GTCEu.id("block/multiblock/processing_array"))
            .register();

    public static void init() {}
}

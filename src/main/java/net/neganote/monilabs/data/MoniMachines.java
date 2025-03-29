package net.neganote.monilabs.data;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.renderer.PrismaticCrucibleRenderer;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static net.neganote.monilabs.MoniLabs.REGISTRATE;
import static net.neganote.monilabs.data.MoniBlocks.PRISMATIC_FOCUS;

@SuppressWarnings("unused")
public class MoniMachines {

    static {
        REGISTRATE.creativeModeTab(() -> MoniLabs.MONI_CREATIVE_TAB);
    }

    public static MultiblockMachineDefinition PRISMATIC_CRUCIBLE = REGISTRATE
            .multiblock("prismatic_crucible", PrismaticCrucibleMachine::new)
            .appearanceBlock(MoniBlocks.DIMENSIONAL_STABILIZATION_NETHERITE_CASING)
            .rotationState(RotationState.NON_Y_AXIS)
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

    public static void init() {}
}

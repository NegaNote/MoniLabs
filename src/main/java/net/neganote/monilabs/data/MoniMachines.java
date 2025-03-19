package net.neganote.monilabs.data;

import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MultiblockMachineDefinition;
import com.gregtechceu.gtceu.api.pattern.FactoryBlockPattern;
import com.gregtechceu.gtceu.client.util.TooltipHelper;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.renderer.PrismaticCrucibleRenderer;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;

import static com.gregtechceu.gtceu.api.pattern.Predicates.*;
import static net.neganote.monilabs.MoniLabs.REGISTRATE;

@SuppressWarnings("unused")
public class MoniMachines {

    static {
        REGISTRATE.creativeModeTab(() -> MoniLabs.MONI_CREATIVE_TAB);
    }

    public static MultiblockMachineDefinition PRISMATIC_CRUCIBLE = REGISTRATE
            .multiblock("prismatic_crucible", PrismaticCrucibleMachine::new)
            .appearanceBlock(MoniBlocks.PRISMATIC_CASING)
            .rotationState(RotationState.NON_Y_AXIS)
            .recipeTypes(MoniRecipeTypes.PRISMATIC_CRUCIBLE_RECIPES)

            .pattern(definition -> FactoryBlockPattern.start()
                    .aisle("CCC", "CCC", "CCC")
                    .aisle("CCC", "C#C", "C#C")
                    .aisle("CCC", "CPC", "CCC")
                    .where('C', blocks(MoniBlocks.PRISMATIC_CASING.get()).setMinGlobalLimited(9)
                            .or(autoAbilities(definition.getRecipeTypes()))
                            .or(autoAbilities(true, false, false)))
                    .where('P', controller(blocks(definition.getBlock())))
                    .where('#', any())
                    .build())
            .additionalDisplay((controller, components) -> {
                if (controller instanceof PrismaticCrucibleMachine prismMachine && controller.isFormed()) {
                    components.add(Component.translatable("monilabs.prismatic.current_color", Component.translatable(prismMachine.getColorState().nameKey)));
                }
            })
            .tooltipBuilder((stack, list) -> {
                list.add(Component.translatable("monilabs.tooltip.prismatic.0",
                        Component.translatable("monilabs.tooltip.prismatic.rainbow").withStyle(TooltipHelper.RAINBOW_HSL_FAST)));
                list.add(Component.translatable("monilabs.tooltip.prismatic.1"));
                list.add(Component.translatable("monilabs.tooltip.prismatic.2"));
            })
            .renderer(PrismaticCrucibleRenderer::new)
            .register();

    public static void init() {}
}

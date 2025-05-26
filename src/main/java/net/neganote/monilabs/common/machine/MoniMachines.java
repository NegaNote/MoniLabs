package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

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

    public static void init() {}
}

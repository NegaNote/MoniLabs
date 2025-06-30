package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.client.renderer.part.ChromaSensorHatchRenderer;
import net.neganote.monilabs.client.renderer.part.MicroverseStabilitySensorHatchRenderer;
import net.neganote.monilabs.client.renderer.part.SculkExperienceDrainingHatchRenderer;
import net.neganote.monilabs.client.renderer.part.SculkExperienceSensorHatchRenderer;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.common.machine.part.ChromaSensorHatchPartMachine;
import net.neganote.monilabs.common.machine.part.MicroverseStabilitySensorHatchPartMachine;
import net.neganote.monilabs.common.machine.part.SculkExperienceDrainingHatchPartMachine;
import net.neganote.monilabs.common.machine.part.SculkExperienceSensorHatchPartMachine;

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
            .tooltips(Component.translatable("monilabs.tooltip.chroma_sensor_hatch.0"),
                    Component.translatable("monilabs.tooltip.chroma_sensor_hatch.1"),
                    Component.translatable("monilabs.tooltip.chroma_sensor_hatch.2"))
            .renderer(() -> new ChromaSensorHatchRenderer("block/overlay/machine/overlay_chroma_sensor"))
            .register();

    public static MachineDefinition SCULK_XP_DRAINING_HATCH = MoniLabs.REGISTRATE
            .machine("sculk_xp_draining_hatch", SculkExperienceDrainingHatchPartMachine::new)
            .langValue("Sculk XP Draining Hatch")
            .tooltips(Component.translatable("tooltip.monilabs.xp_draining_hatch.0"),
                    Component.translatable("tooltip.monilabs.xp_draining_hatch.1"),
                    Component.translatable("tooltip.monilabs.xp_draining_hatch.2"))
            .renderer(() -> new SculkExperienceDrainingHatchRenderer("block/overlay/machine/exp_hatch_draining"))
            .register();

    public static MachineDefinition SCULK_XP_SENSOR_HATCH = MoniLabs.REGISTRATE
            .machine("sculk_xp_sensor_hatch", SculkExperienceSensorHatchPartMachine::new)
            .langValue("Sculk XP Sensor Hatch")
            .tooltips(Component.translatable("tooltip.monilabs.xp_sensor_hatch.0"),
                    Component.translatable("tooltip.monilabs.xp_sensor_hatch.1"))
            .renderer(() -> new SculkExperienceSensorHatchRenderer("block/overlay/machine/exp_sensor"))
            .register();

    public static MachineDefinition MICROVERSE_INTEGRITY_SENSOR_HATCH = MoniLabs.REGISTRATE
            .machine("microverse_stability_sensor_hatch", MicroverseStabilitySensorHatchPartMachine::new)
            .langValue("Microverse Stability Sensor Hatch")
            .tooltips(Component.translatable("tooltip.monilabs.microverse_stability_hatch.0"),
                    Component.translatable("tooltip.monilabs.microverse_stability_hatch.1"))
            .renderer(() -> new MicroverseStabilitySensorHatchRenderer("block/overlay/machine/stability_hatch"))
            .register();

    public static void init() {}
}

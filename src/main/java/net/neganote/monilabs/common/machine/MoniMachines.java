package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IMultiController;

import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.common.machine.part.*;
import net.neganote.monilabs.data.models.MoniMachineModels;

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
            .model(MoniMachineModels.createOverlayChromaMachineModel("chroma_sensor"))
            .tier(GTValues.UHV)
            .register();

    public static MachineDefinition SCULK_XP_DRAINING_HATCH = MoniLabs.REGISTRATE
            .machine("sculk_xp_draining_hatch", SculkExperienceDrainingHatchPartMachine::new)
            .langValue("Sculk XP Draining Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("tooltip.monilabs.xp_draining_hatch.0"),
                    Component.translatable("tooltip.monilabs.xp_draining_hatch.1"),
                    Component.translatable("tooltip.monilabs.xp_draining_hatch.2"))
            .overlayTieredHullModel("sculk_experience_draining_hatch")
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
            .model(MoniMachineModels.createOverlayFillLevelMachineModel("exp_sensor"))
            .register();

    public static MachineDefinition MICROVERSE_INTEGRITY_SENSOR_HATCH = MoniLabs.REGISTRATE
            .machine("microverse_stability_sensor_hatch", MicroverseStabilitySensorHatchPartMachine::new)
            .langValue("Microverse Stability Sensor Hatch")
            .rotationState(RotationState.ALL)
            .tooltips(Component.translatable("tooltip.monilabs.microverse_stability_hatch.0"),
                    Component.translatable("tooltip.monilabs.microverse_stability_hatch.1"))
            .tier(GTValues.HV)
            .modelProperty(FillLevel.FILL_PROPERTY, FillLevel.EMPTY_TO_QUARTER)
            .model(MoniMachineModels.createOverlayFillLevelMachineModel("stability_hatch"))
            .register();

    public static void init() {}
}

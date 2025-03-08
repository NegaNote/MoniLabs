package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.RotationState;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.multiblock.CleanroomType;
import com.gregtechceu.gtceu.api.machine.multiblock.PartAbility;
import com.gregtechceu.gtceu.client.renderer.machine.MaintenanceHatchPartRenderer;
import com.gregtechceu.gtceu.common.machine.multiblock.part.CleaningMaintenanceHatchPartMachine;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabsMod;

import static net.neganote.monilabs.MoniLabsMod.REGISTRATE;

@SuppressWarnings("unused")
public class MoniMachines {

    static {
        REGISTRATE.creativeModeTab(() -> MoniLabsMod.MONI_CREATIVE_TAB);
    }

    public static final MachineDefinition STERILE_CLEANING_MAINTENANCE_HATCH = REGISTRATE
            .machine("sterile_cleaning_maintenance_hatch",
                    holder -> new CleaningMaintenanceHatchPartMachine(holder, CleanroomType.STERILE_CLEANROOM))
            .rotationState(RotationState.ALL)
            .abilities(PartAbility.MAINTENANCE)
            .tooltips(Component.translatable("gtceu.universal.disabled"),
                    Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.0"),
                    Component.translatable("gtceu.machine.maintenance_hatch_cleanroom_auto.tooltip.1"))
            .tooltipBuilder((stack, tooltips) -> {
                tooltips.add(Component.literal("  ").append(Component
                        .translatable(CleanroomType.STERILE_CLEANROOM.getTranslationKey()).withStyle(ChatFormatting.GREEN)));
            })
            .renderer(() -> new MaintenanceHatchPartRenderer(GTValues.UHV, MoniLabsMod.id("block/machine/part/maintenance.sterile_cleaning"))) // Tier can always be changed later
            .register();

    public static void init() {}
}

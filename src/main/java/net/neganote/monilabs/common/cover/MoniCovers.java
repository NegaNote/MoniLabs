package net.neganote.monilabs.common.cover;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.client.renderer.cover.IOCoverRenderer;
import com.gregtechceu.gtceu.common.cover.ConveyorCover;
import com.gregtechceu.gtceu.common.cover.FluidRegulatorCover;
import com.gregtechceu.gtceu.common.cover.PumpCover;
import com.gregtechceu.gtceu.common.cover.RobotArmCover;

import net.neganote.monilabs.MoniLabs;

public class MoniCovers {

    public static CoverDefinition MAX_CONVEYOR_MODULE = new CoverDefinition(MoniLabs.id("max_conveyor_module"),
            (def, coverable, side) -> new ConveyorCover(def, coverable, side, GTValues.MAX, 8192),
            () -> () -> new IOCoverRenderer(
                    GTCEu.id("block/cover/conveyor"),
                    null,
                    GTCEu.id("block/cover/conveyor_emissive"),
                    GTCEu.id("block/cover/conveyor_inverted_emissive")));

    public static CoverDefinition MAX_ROBOT_ARM = new CoverDefinition(MoniLabs.id("max_robot_arm"),
            (def, coverable, side) -> new RobotArmCover(def, coverable, side, GTValues.MAX, 8192),
            () -> () -> new IOCoverRenderer(
                    GTCEu.id("block/cover/arm"),
                    null,
                    GTCEu.id("block/cover/arm_emissive"),
                    GTCEu.id("block/cover/arm_inverted_emissive")));

    public static CoverDefinition MAX_ELECTRIC_PUMP = new CoverDefinition(MoniLabs.id("max_electric_pump"),
            (def, coverable, side) -> new PumpCover(def, coverable, side, GTValues.MAX,
                    1280 * 64 * 64 * 4 / 20),
            () -> () -> IOCoverRenderer.PUMP_LIKE_COVER_RENDERER);

    public static CoverDefinition MAX_FLUID_REGULATOR = new CoverDefinition(MoniLabs.id("max_fluid_regulator"),
            (def, coverable, side) -> new FluidRegulatorCover(def, coverable, side, GTValues.MAX,
                    1280 * 64 * 64 * 4 / 20),
            () -> () -> IOCoverRenderer.PUMP_LIKE_COVER_RENDERER);

    public static void init() {}
}

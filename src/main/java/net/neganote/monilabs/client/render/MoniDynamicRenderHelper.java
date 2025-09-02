package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class MoniDynamicRenderHelper {

    @Contract(" -> new")
    public static @NotNull DynamicRender<?, ?> createPrismacLaserRender() {
        return PrismaticCrucibleRender.INSTANCE;
    }

    public static @NotNull DynamicRender<?, ?> createMicroverseProjectorRender() {
        return MicroverseProjectorRender.INSTANCE;
    }

    public static @NotNull DynamicRender<?, ?> createCreativeEnergyRender() {
        return CreativeEnergyRender.INSTANCE;
    }
}

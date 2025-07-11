package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class MoniDynamicRenderHelper {

    @Contract(" -> new")
    public static @NotNull DynamicRender<?, ?> createPrismacRender() {
        return PrismaticCrucibleRender.INSTANCE;
    }

    public static @NotNull DynamicRender<?, ?> createMicroverseProjectorRender() {
        return MicroverseProjectorRender.INSTANCE;
    }
}

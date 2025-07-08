package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class MoniDynamicRenderHelper {

    @Contract(" -> new")
    public static @NotNull DynamicRender<?, ?> createPrismacRender() {
        return new PrismaticCrucibleRender();
    }

    public static @NotNull DynamicRender<?, ?> createMicroverseProjectorRender(int tier) {
        return new MicroverseProjectorRender(tier);
    }
}

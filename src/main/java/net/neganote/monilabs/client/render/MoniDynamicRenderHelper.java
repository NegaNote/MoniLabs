package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;

import net.minecraft.MethodsReturnNonnullByDefault;

import org.jetbrains.annotations.Contract;

@MethodsReturnNonnullByDefault
public class MoniDynamicRenderHelper {

    @Contract(" -> new")
    public static DynamicRender<?, ?> createPrismacLaserRender() {
        return PrismaticCrucibleRender.INSTANCE;
    }

    public static DynamicRender<?, ?> createMicroverseProjectorRender() {
        return MicroverseProjectorRender.INSTANCE;
    }

    public static DynamicRender<?, ?> createCreativeEnergyRender() {
        return CreativeEnergyRender.INSTANCE;
    }

    public static DynamicRender<?, ?> createCreativeDataRender() {
        return CreativeDataRender.INSTANCE;
    }
}

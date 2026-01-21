package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.block.FluidBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.LightTexture;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.Contract;

import java.util.List;

@MethodsReturnNonnullByDefault
@SideOnly(Side.CLIENT)
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

    public static DynamicRender<?, ?> createSculkVatRender(float faceOffset, List<RelativeDirection> drawFaces) {
        return new SculkVatRender(FluidBlockRenderer.Builder.create()
                .setFaceOffset(faceOffset)
                .setForcedLight(LightTexture.FULL_BRIGHT)
                .getRenderer(), drawFaces);
    }

    public static DynamicRender<?, ?> createHelicalFusionRenderer() {
        return HelicalFusionRenderer.INSTANCE;
    }
}

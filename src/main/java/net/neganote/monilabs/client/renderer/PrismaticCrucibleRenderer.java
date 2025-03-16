package net.neganote.monilabs.client.renderer;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.monilabs.MoniLabs;


public class PrismaticCrucibleRenderer extends WorkableCasingMachineRenderer {
    public static final ResourceLocation BASE_CASING = MoniLabs.id("block/casings/prismatic_casing");
    public static final ResourceLocation FRONT_TEXTURES = GTCEu.id("block/multiblock/processing_array");
    public PrismaticCrucibleRenderer() {
        super(BASE_CASING, FRONT_TEXTURES);
    }

    @Override
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        super.render(blockEntity, partialTicks, stack, buffer, combinedLight, combinedOverlay);

        // TODO: fill this method with actual rendering once assets are available
    }
}

package net.neganote.monilabs.client.renderer;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.block.FluidBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import com.gregtechceu.gtceu.client.util.RenderUtil;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderTypeHelper;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import com.mojang.blaze3d.vertex.PoseStack;

// Parts copied from LargeChemicalBathRenderer
public class PrismaticCrucibleRenderer extends WorkableCasingMachineRenderer {

    public static final ResourceLocation BASE_CASING = MoniLabs.id("block/casings/prismatic_containment_lining");
    public static final ResourceLocation FRONT_TEXTURES = GTCEu.id("block/multiblock/processing_array");

    private final FluidBlockRenderer fluidBlockRenderer;

    public PrismaticCrucibleRenderer() {
        super(BASE_CASING, FRONT_TEXTURES);
        fluidBlockRenderer = FluidBlockRenderer.Builder.create()
                .setFaceOffset(-0.125f)
                .setForcedLight(LightTexture.FULL_BRIGHT)
                .getRenderer();
    }

    @Override
    public boolean hasTESR(BlockEntity blockEntity) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof IMachineBlockEntity machineBlockEntity &&
                machineBlockEntity.getMetaMachine() instanceof PrismaticCrucibleMachine pcm && pcm.isFormed()) {
            MoniLabs.LOGGER.info("Attempting Render...");
            var level = pcm.getLevel();
            var color = pcm.getColorState();
            assert level != null;
            poseStack.pushPose();
            PoseStack.Pose pose = poseStack.last();
            var fluidRenderType = ItemBlockRenderTypes.getRenderLayer(Fluids.LAVA.defaultFluidState());
            var consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));
            consumer = consumer.color(color.r, color.g, color.b, 1.0F);
            var up = RelativeDirection.UP.getRelativeFacing(pcm.getFrontFacing(), pcm.getUpwardsFacing(),
                    pcm.isFlipped());
            if (up.getAxis() != Direction.Axis.Y) up = up.getOpposite();
            MoniLabs.LOGGER.info("Attempting to draw plane...");
            fluidBlockRenderer.drawPlane(up, pcm.getFluidBlockOffsets(), pose.pose(), consumer, Fluids.LAVA,
                    RenderUtil.FluidTextureType.STILL, combinedOverlay, pcm.getPos());
            poseStack.popPose();
            MoniLabs.LOGGER.info("Rendering complete!");
        }
    }

    @SuppressWarnings("unused")
    // private void renderCube(PoseStack poseStack, MultiBufferSource buffer, Direction frontFacing, float tick,
    // PrismaticCrucibleMachine.Color color, int combinedLight, int combinedOverlay,
    // PrismaticCrucibleMachine machine) {
    // var modelManager = Minecraft.getInstance().getModelManager();
    // poseStack.pushPose();
    //
    // BakedModel bakedModel = modelManager.getModel(CUBE_MODEL);
    //
    // float[] renderOffset = machine.getRenderOffset();
    //
    // poseStack.translate(renderOffset[0], renderOffset[1], renderOffset[2]);
    //
    // poseStack.scale(10.0f, 10.0f, 10.0f);
    //
    // PoseStack.Pose pose = poseStack.last();
    //
    // VertexConsumer consumer = buffer.getBuffer(RenderType.solid());
    //
    // @SuppressWarnings("deprecation")
    // List<BakedQuad> quads = bakedModel.getQuads(null, null, GTValues.RNG);
    //
    // for (BakedQuad quad : quads) {
    // consumer.putBulkData(pose, quad, color.r, color.g, color.b, combinedLight, combinedOverlay);
    // }
    // poseStack.popPose();
    // }

    @OnlyIn(Dist.CLIENT)
    public float reBakeCustomQuadsOffset() {
        return 0f;
    }

    @Override
    public boolean isGlobalRenderer(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 64;
    }
}

package net.neganote.monilabs.client.renderer;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.List;
import java.util.function.Consumer;

// Lots of this is mostly copied from CosmicCore tbh, thanks Ghosti
public class PrismaticCrucibleRenderer extends WorkableCasingMachineRenderer {

    public static final ResourceLocation BASE_CASING = MoniLabs.id("block/casings/prismatic_containment_lining");
    public static final ResourceLocation FRONT_TEXTURES = GTCEu.id("block/multiblock/processing_array");

    public static final ResourceLocation CUBE_MODEL = MoniLabs.id("block/prismac/cube");

    public PrismaticCrucibleRenderer() {
        super(BASE_CASING, FRONT_TEXTURES);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        // if (blockEntity instanceof IMachineBlockEntity machineBlockEntity &&
        // machineBlockEntity.getMetaMachine() instanceof PrismaticCrucibleMachine machine && machine.isFormed()) {
        // var level = machine.getLevel();
        // var frontFacing = machine.getFrontFacing();
        // var color = machine.getColorState();
        // assert level != null;
        // float tick = level.getGameTime() + partialTicks;
        // renderCube(poseStack, buffer, frontFacing, tick, color, combinedLight, combinedOverlay, machine);
        // }
    }

    @SuppressWarnings("unused")
    private void renderCube(PoseStack poseStack, MultiBufferSource buffer, Direction frontFacing, float tick,
                            PrismaticCrucibleMachine.Color color, int combinedLight, int combinedOverlay,
                            PrismaticCrucibleMachine machine) {
        var modelManager = Minecraft.getInstance().getModelManager();
        poseStack.pushPose();

        BakedModel bakedModel = modelManager.getModel(CUBE_MODEL);

        float[] renderOffset = machine.getRenderOffset();

        poseStack.translate(renderOffset[0], renderOffset[1], renderOffset[2]);

        poseStack.scale(10.0f, 10.0f, 10.0f);

        PoseStack.Pose pose = poseStack.last();

        VertexConsumer consumer = buffer.getBuffer(RenderType.solid());

        @SuppressWarnings("deprecation")
        List<BakedQuad> quads = bakedModel.getQuads(null, null, GTValues.RNG);

        for (BakedQuad quad : quads) {
            consumer.putBulkData(pose, quad, color.r, color.g, color.b, combinedLight, combinedOverlay);
        }
        poseStack.popPose();
    }

    @Override
    public void onAdditionalModel(Consumer<ResourceLocation> registry) {
        super.onAdditionalModel(registry);
        registry.accept(CUBE_MODEL);
    }

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
        return 256;
    }
}

package net.neganote.monilabs.client.renderer;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.data.ModelData;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.List;
import java.util.function.Consumer;

// @SuppressWarnings("unused")
public class MicroverseProjectorRenderer extends WorkableCasingMachineRenderer {

    public MicroverseProjectorRenderer(ResourceLocation baseCasing, ResourceLocation workableModel) {
        super(baseCasing, workableModel);
    }

    public static final ResourceLocation SPHERE = MoniLabs.id("render/sphere");

    public static final ResourceLocation CUBE = MoniLabs.id("render/cube");

    @Override
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof MetaMachineBlockEntity mmbe &&
                mmbe.getMetaMachine() instanceof MicroverseProjectorMachine projector && projector.isFormed()) {
            var frontFacing = projector.getFrontFacing();
            var upwardsFacing = projector.getUpwardsFacing();

            Direction upwards = RelativeDirection.UP.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

            Direction left = RelativeDirection.LEFT.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

            int tier = projector.getTier();

            renderMicroverse(stack, buffer, upwards, frontFacing, left, combinedLight, combinedOverlay, tier);
        }
    }

    private void renderMicroverse(PoseStack stack, MultiBufferSource buffer, Direction upwards, Direction front,
                                  Direction left, int combinedLight, int combinedOverlay, int tier) {
        switch (tier) {
            case 1:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -1, 1, 0,
                        1.008f, 1.008f, 1.008f);
                break;
            case 2:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -2, 2, 0,
                        3.008f, 3.008f, 3.008f);
        }
    }

    private void renderCuboid(PoseStack stack, MultiBufferSource buffer, Direction upwards, Direction front,
                              Direction left, int combinedLight, int combinedOverlay, int offsetFront, int offsetUp,
                              int offsetLeft, float scaleFactorFB, float scaleFactorUD,
                              float scaleFactorLR) {
        // Setup
        stack.pushPose();
        var modelManager = Minecraft.getInstance().getModelManager();
        BakedModel cube = modelManager.getModel(CUBE);

        var upwardsNormal = upwards.getNormal();
        var frontNormal = front.getNormal();
        var leftNormal = left.getNormal();

        // Calculate offset
        Vec3i movement = upwardsNormal.multiply(offsetUp);
        // MoniLabs.LOGGER.info("movement after upwards offset: {}", movement);
        movement = movement.offset(frontNormal.multiply(offsetFront));
        // MoniLabs.LOGGER.info("movement after front offset: {}", movement);
        movement = movement.offset(leftNormal.multiply(offsetLeft));
        // MoniLabs.LOGGER.info("movement after left offset: {}", movement);

        // Calculate scaling factors
        float scaleFactorX = 0.0f;
        float scaleFactorY = 0.0f;
        float scaleFactorZ = 0.0f;

        if (leftNormal.getX() != 0) {
            scaleFactorX = scaleFactorLR;
        } else if (leftNormal.getY() != 0) {
            // noinspection
            scaleFactorY = scaleFactorLR;
        } else if (leftNormal.getZ() != 0) {
            scaleFactorZ = scaleFactorLR;
        }

        if (frontNormal.getX() != 0) {
            scaleFactorX = scaleFactorFB;
        } else if (frontNormal.getY() != 0) {
            scaleFactorY = scaleFactorFB;
        } else if (frontNormal.getZ() != 0) {
            scaleFactorZ = scaleFactorFB;
        }

        if (upwardsNormal.getX() != 0) {
            scaleFactorX = scaleFactorUD;
        } else if (upwardsNormal.getY() != 0) {
            scaleFactorY = scaleFactorUD;
        } else if (upwardsNormal.getZ() != 0) {
            scaleFactorZ = scaleFactorUD;
        }

        // Actually do the transformations
        stack.translate(movement.getX() + 0.5f, movement.getY() + 0.5f, movement.getZ() + 0.5f);
        stack.scale(scaleFactorX, scaleFactorY, scaleFactorZ);
        PoseStack.Pose pose = stack.last();

        // Send buffer data, clean up
        VertexConsumer consumer = buffer.getBuffer(RenderType.endPortal());
        List<BakedQuad> quads = cube.getQuads(null, null, GTValues.RNG, ModelData.EMPTY, null);
        for (BakedQuad quad : quads) {
            consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, combinedLight, combinedOverlay);
        }
        stack.popPose();
    }

    @Override
    public boolean hasTESR(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 1024;
    }

    @Override
    public boolean isGlobalRenderer(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public void onAdditionalModel(Consumer<ResourceLocation> registry) {
        super.onAdditionalModel(registry);
        registry.accept(SPHERE);
        registry.accept(CUBE);
    }
}

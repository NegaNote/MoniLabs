package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.data.ModelData;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;

// @SuppressWarnings("unused")
public class MicroverseProjectorRender extends
                                       DynamicRender<MicroverseProjectorMachine, MicroverseProjectorRender> {

    // spotless:off
    public static final Codec<MicroverseProjectorRender> CODEC = Codec.INT.xmap(MicroverseProjectorRender::new, MicroverseProjectorRender::getTier);
    public static final DynamicRenderType<MicroverseProjectorMachine, MicroverseProjectorRender> TYPE = new DynamicRenderType<>(CODEC);
    // spotless:on

    @Getter
    private final int tier;

    public MicroverseProjectorRender(int tier) {
        this.tier = tier;
    }

    public static final ResourceLocation SPHERE = MoniLabs.id("render/sphere");

    public static final ResourceLocation CUBE = MoniLabs.id("render/cube");

    @Override
    public void render(MicroverseProjectorMachine projector, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        var frontFacing = projector.getFrontFacing();
        var upwardsFacing = projector.getUpwardsFacing();

        Direction upwards = RelativeDirection.UP.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

        Direction left = RelativeDirection.LEFT.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

        renderMicroverse(stack, buffer, upwards, frontFacing, left, combinedLight, combinedOverlay);
    }

    private void renderMicroverse(PoseStack stack, MultiBufferSource buffer, Direction upwards, Direction front,
                                  Direction left, int combinedLight, int combinedOverlay) {
        switch (tier) {
            case 1:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -1, 1,
                        1.008f, 1.008f, 1.008f);
                break;
            case 2:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -2, 2,
                        3.008f, 3.008f, 3.008f);
                break;
            case 3:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -4, 2,
                        5f, 7.008f, 5f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -4, 2,
                        7.008f, 5f, 5f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -4, 2,
                        5f, 5f, 7.008f);
                break;
            case 4:
                // Lower square
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -5, 1,
                        3f, 1f, 5.008f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -5, 1,
                        5.008f, 1f, 3f);

                // Middle tube
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -5, 5,
                        1.008f, 7f, 1.008f);

                // Upper square
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -5, 9,
                        3f, 1f, 5.008f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, combinedOverlay, -5, 9,
                        5.008f, 1f, 3f);
                break;
        }
    }

    private void renderCuboid(PoseStack stack, MultiBufferSource buffer, Direction upwards, Direction front,
                              Direction left, int combinedLight, int combinedOverlay, int offsetFront, int offsetUp,
                              float scaleFactorFB, float scaleFactorUD, float scaleFactorLR) {
        // Setup
        stack.pushPose();
        var modelManager = Minecraft.getInstance().getModelManager();
        BakedModel cube = modelManager.getModel(CUBE);

        var upwardsNormal = upwards.getNormal();
        var frontNormal = front.getNormal();
        var leftNormal = left.getNormal();

        // Calculate offset
        Vec3i movement = upwardsNormal.multiply(offsetUp);
        movement = movement.offset(frontNormal.multiply(offsetFront));

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
    public int getViewDistance() {
        return 1024;
    }

    @Override
    public @NotNull DynamicRenderType<MicroverseProjectorMachine, MicroverseProjectorRender> getType() {
        return TYPE;
    }
}

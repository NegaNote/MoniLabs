package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderBufferHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.irisshaders.iris.Iris;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;

// @SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class MicroverseProjectorRender extends
                                       DynamicRender<MicroverseProjectorMachine, MicroverseProjectorRender> {

    public static final MicroverseProjectorRender INSTANCE = new MicroverseProjectorRender();

    // spotless:off
    public static final Codec<MicroverseProjectorRender> CODEC = Codec.unit(INSTANCE);
    public static final DynamicRenderType<MicroverseProjectorMachine, MicroverseProjectorRender> TYPE = new DynamicRenderType<>(MicroverseProjectorRender.CODEC);
    // spotless:on

    public MicroverseProjectorRender() {}

    @Override
    public void render(MicroverseProjectorMachine projector, float partialTicks, @NotNull PoseStack stack,
                       @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        var frontFacing = projector.getFrontFacing();
        var upwardsFacing = projector.getUpwardsFacing();

        Direction front = RelativeDirection.FRONT.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

        Direction upwards = RelativeDirection.UP.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

        Direction left = RelativeDirection.LEFT.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

        int tier = projector.getProjectorTier();

        renderMicroverse(stack, buffer, upwards, front, left, combinedLight, tier);
    }

    private void renderMicroverse(PoseStack stack, MultiBufferSource buffer, Direction upwards, Direction front,
                                  Direction left, int combinedLight, int tier) {
        switch (tier) {
            case 1:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -1, 1,
                        1.002f, 1.002f, 1.002f);
                break;
            case 2:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -2, 2,
                        3.002f, 3.002f, 3.002f);
                break;
            case 3:
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -4, 2,
                        5.002f, 7.002f, 5.002f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -4, 2,
                        7.002f, 5.002f, 5.002f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -4, 2,
                        5.002f, 5.002f, 7.002f);
                break;
            case 4:
                // Lower square
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -5, 1,
                        3.002f, 1.002f, 5.002f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -5, 1,
                        5.002f, 1.002f, 3.002f);

                // Middle tube
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -5, 5,
                        1.002f, 7.002f, 1.002f);

                // Upper square
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -5, 9,
                        3.002f, 1.002f, 5.002f);
                renderCuboid(stack, buffer, upwards, front, left, combinedLight, -5, 9,
                        5.002f, 1.002f, 3.002f);
                break;
        }
    }

    private void renderCuboid(PoseStack stack, MultiBufferSource buffer, Direction upwards, Direction front,
                              Direction left, int combinedLight, int offsetFront, int offsetUp,
                              float scaleFactorFB, float scaleFactorUD, float scaleFactorLR) {
        stack.pushPose();
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

        float minX = movement.getX() + 0.5f - (scaleFactorX / 2.0f);
        float minY = movement.getY() + 0.5f - (scaleFactorY / 2.0f);
        float minZ = movement.getZ() + 0.5f - (scaleFactorZ / 2.0f);

        float maxX = movement.getX() + 0.5f + (scaleFactorX / 2.0f);
        float maxY = movement.getY() + 0.5f + (scaleFactorY / 2.0f);
        float maxZ = movement.getZ() + 0.5f + (scaleFactorZ / 2.0f);

        PoseStack.Pose pose = stack.last();

        // Send buffer data, clean up
        VertexConsumer consumer;
        if (GTCEu.isModLoaded(GTValues.MODID_OCULUS) && Iris.getCurrentPack().isPresent()) {
            consumer = buffer.getBuffer(RenderType.entitySolid(TheEndPortalRenderer.END_PORTAL_LOCATION));
        } else {
            consumer = buffer.getBuffer(RenderType.endPortal());
        }
        RenderBufferHelper.renderCube(consumer, pose, 0xFFFFFF00, combinedLight, UnitTextureAtlasSprite.INSTANCE,
                minX, minY, minZ, maxX, maxY, maxZ);
        stack.popPose();
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull MicroverseProjectorMachine machine) {
        return new AABB(machine.getPos()).inflate(getViewDistance(), 16, getViewDistance());
    }

    @Override
    public @NotNull DynamicRenderType<MicroverseProjectorMachine, MicroverseProjectorRender> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(MicroverseProjectorMachine machine, @NotNull Vec3 cameraPos) {
        return machine.getMicroverse() != Microverse.NONE && machine.isFormed();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull MicroverseProjectorMachine machine) {
        return true;
    }
}

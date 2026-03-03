package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.utils.LaserUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Collection;
import java.util.Objects;

import static com.gregtechceu.gtceu.client.util.RenderUtil.getNormal;
import static com.gregtechceu.gtceu.client.util.RenderUtil.getVertices;
import static net.minecraft.util.FastColor.ARGB32.*;

@SuppressWarnings("unused")
@SideOnly(Side.CLIENT)
public class PrismaticCrucibleRender extends DynamicRender<PrismaticCrucibleMachine, PrismaticCrucibleRender> {

    // spotless:off
    public static final Codec<PrismaticCrucibleRender> CODEC = Codec.unit(PrismaticCrucibleRender::new);
    public static final DynamicRenderType<PrismaticCrucibleMachine, PrismaticCrucibleRender> TYPE = new DynamicRenderType<>(PrismaticCrucibleRender.CODEC);
    public static final PrismaticCrucibleRender INSTANCE = new PrismaticCrucibleRender();
    // spotless:on

    public PrismaticCrucibleRender() {}

    @Override
    public void render(PrismaticCrucibleMachine pcm, float partialTicks, PoseStack poseStack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        var level = pcm.getLevel();
        var color = pcm.getColorState();
        assert level != null;

        Direction resolvedUp = RelativeDirection.UP.getRelative(
                pcm.getFrontFacing(),
                pcm.getUpwardsFacing(),
                pcm.isFlipped());

        Direction faceDir = resolvedUp.getAxis() == Direction.Axis.Y ? resolvedUp : resolvedUp.getOpposite();

        Direction down = faceDir.getOpposite();

        poseStack.pushPose();
        PoseStack.Pose pose = poseStack.last();

        var fluidRenderType = ItemBlockRenderTypes.getRenderLayer(GTMaterials.Iron.getFluid().defaultFluidState());
        var consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));
        consumer = consumer.color(color.r, color.g, color.b, 1.0F);

        drawPlane(faceDir, resolvedUp, pcm.getFluidBlockOffsets(), pose.pose(), consumer, GTMaterials.Iron.getFluid(),
                RenderUtil.FluidTextureType.STILL, combinedOverlay, pcm);

        poseStack.popPose();

        long gameTime = Objects.requireNonNull(Minecraft.getInstance().player).tickCount;

        if (pcm.isActive() && pcm.getFocusPos() != null) {
            Vector3f ray = new Vector3f(6.0F * (float) down.getNormal().getX(),
                    6.0F * (float) down.getNormal().getY(),
                    6.0F * (float) down.getNormal().getZ());

            float xOffset = (float) (pcm.getFocusPos().getX() - pcm.getPos().getX()) + 12.0F;
            float yOffset = (float) (pcm.getFocusPos().getY() - pcm.getPos().getY()) + 0.5F;
            float zOffset = (float) (pcm.getFocusPos().getZ() - pcm.getPos().getZ()) + 0.5F;

            LaserUtil.renderLaser(ray, poseStack, buffer, color.r, color.g, color.b, 1.0F, xOffset, yOffset,
                    zOffset, partialTicks, gameTime, true);
        }
    }

    private BlockPos remapOffset(BlockPos offset, Direction resolvedUp) {
        if (resolvedUp.getAxis() == Direction.Axis.Y) return offset;

        int x = offset.getX();
        int y = offset.getY();
        int z = offset.getZ();

        int sign = resolvedUp.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1 : -1;

        return switch (resolvedUp.getAxis()) {
            case X -> new BlockPos(y * sign, 0, z);
            case Z -> new BlockPos(x, 0, y * sign);
            default -> offset;
        };
    }

    // Stolen and modified from FluidBlockRender
    public void drawPlane(Direction face, Direction resolvedUp, Collection<BlockPos> offsets, Matrix4f pose,
                          VertexConsumer consumer, Fluid fluid, RenderUtil.FluidTextureType texture,
                          int combinedOverlay, PrismaticCrucibleMachine pcm) {
        var fluidClientInfo = IClientFluidTypeExtensions.of(fluid);
        var sprite = texture.map(fluidClientInfo);
        float u0 = sprite.getU0(), v0 = sprite.getV0(), u1 = sprite.getU1(), v1 = sprite.getV1();
        int color = pcm.getColorState().integerColor;
        int r = red(color), g = green(color), b = blue(color), a = alpha(color);

        var normal = getNormal(face);
        var vertices = transformVertices(getVertices(face), face);

        Direction front = pcm.getFrontFacing();
        Direction upDir = resolvedUp;

        Vector3f leftVec = new Vector3f(
                (float) upDir.getStepY() * front.getStepZ() - (float) upDir.getStepZ() * front.getStepY(),
                (float) upDir.getStepZ() * front.getStepX() - (float) upDir.getStepX() * front.getStepZ(),
                (float) upDir.getStepX() * front.getStepY() - (float) upDir.getStepY() * front.getStepX());

        float leftShift = 0.0f;
        float backShift = -8.0f;

        Matrix4f shiftedPose = new Matrix4f(pose);
        shiftedPose.translate(
                (leftVec.x() * leftShift) + (front.getStepX() * backShift),
                (leftVec.y() * leftShift) + (front.getStepY() * backShift),
                (leftVec.z() * leftShift) + (front.getStepZ() * backShift));

        for (var offset : offsets) {
            float dx = (leftVec.x() * -offset.getX()) + (upDir.getStepX() * offset.getY()) +
                    (front.getStepX() * offset.getZ());
            float dy = (leftVec.y() * -offset.getX()) + (upDir.getStepY() * offset.getY()) +
                    (front.getStepY() * offset.getZ());
            float dz = (leftVec.z() * -offset.getX()) + (upDir.getStepZ() * offset.getY()) +
                    (front.getStepZ() * offset.getZ());

            Matrix4f blockPose = new Matrix4f(shiftedPose);
            blockPose.translate(dx, dy, dz);

            drawFace(blockPose, consumer, vertices, normal, u0, u1, v0, v1, r, g, b, a, combinedOverlay);
        }
    }

    // Stolen and modified from FluidBlockRender
    public void drawFace(Matrix4f pose, VertexConsumer consumer, Vector3f[] vertices, Vector3fc normal,
                         float u0, float u1, float v0, float v1,
                         int r, int g, int b, int a,
                         int combinedOverlay) {
        int combinedLight = LightTexture.FULL_BRIGHT;

        var vert = vertices[0];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z,
                r, g, b, a,
                u0, v1,
                combinedOverlay, combinedLight, normal.x(), normal.y(), normal.z());

        vert = vertices[1];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z,
                r, g, b, a,
                u0, v0,
                combinedOverlay, combinedLight, normal.x(), normal.y(), normal.z());

        vert = vertices[2];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z,
                r, g, b, a,
                u1, v0,
                combinedOverlay, combinedLight, normal.x(), normal.y(), normal.z());

        vert = vertices[3];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z,
                r, g, b, a,
                u1, v1,
                combinedOverlay, combinedLight, normal.x(), normal.y(), normal.z());
    }

    // Stolen and modified from FluidBlockRender
    public Vector3f[] transformVertices(Vector3fc[] vertices, Direction face) {
        float offsetX = 0, offsetY = 0, offsetZ = 0;

        switch (face.getAxis()) {
            case X -> offsetX -= 0.125f;
            case Y -> offsetY -= 0.125f;
            case Z -> offsetZ -= 0.125f;
        }

        var newVertices = new Vector3f[4];
        for (int i = 0; i < 4; i++) {
            newVertices[i] = RenderUtil.transformVertex(vertices[i], face, offsetX, offsetY, offsetZ);
        }
        return newVertices;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull PrismaticCrucibleMachine machine) {
        return true;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(@NotNull PrismaticCrucibleMachine machine) {
        return new AABB(machine.getPos()).inflate(getViewDistance(), 16, getViewDistance());
    }

    @Override
    public boolean shouldRender(@NotNull PrismaticCrucibleMachine machine, @NotNull Vec3 cameraPos) {
        return machine.isFormed();
    }

    @Override
    public @NotNull DynamicRenderType<PrismaticCrucibleMachine, PrismaticCrucibleRender> getType() {
        return TYPE;
    }
}

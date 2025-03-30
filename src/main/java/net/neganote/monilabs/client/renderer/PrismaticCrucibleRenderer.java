package net.neganote.monilabs.client.renderer;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import com.gregtechceu.gtceu.client.util.RenderUtil;
import com.gregtechceu.gtceu.common.data.GTMaterials;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.RenderTypeHelper;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.utils.LaserUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.Collection;

import static com.gregtechceu.gtceu.client.util.RenderUtil.getNormal;
import static com.gregtechceu.gtceu.client.util.RenderUtil.getVertices;
import static net.minecraft.util.FastColor.ARGB32.*;
import static net.minecraft.util.FastColor.ARGB32.alpha;

// Parts copied from LargeChemicalBathRenderer
public class PrismaticCrucibleRenderer extends WorkableCasingMachineRenderer {

    public static final ResourceLocation BASE_CASING = MoniLabs.id("block/dimensional_stabilization_netherite_casing");
    public static final ResourceLocation FRONT_TEXTURES = GTCEu.id("block/multiblock/processing_array");

    public PrismaticCrucibleRenderer() {
        super(BASE_CASING, FRONT_TEXTURES);
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
                machineBlockEntity.getMetaMachine() instanceof PrismaticCrucibleMachine pcm && pcm.isFormed() &&
                pcm.isActive() && pcm.getFocusPos() != null) {
            var level = pcm.getLevel();
            var color = pcm.getColorState();
            assert level != null;
            poseStack.pushPose();
            PoseStack.Pose pose = poseStack.last();
            var fluidRenderType = ItemBlockRenderTypes.getRenderLayer(GTMaterials.Iron.getFluid().defaultFluidState());
            var consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));
            consumer = consumer.color(color.r, color.g, color.b, 1.0F);
            var up = RelativeDirection.UP.getRelativeFacing(pcm.getFrontFacing(), pcm.getUpwardsFacing(),
                    pcm.isFlipped());
            if (up.getAxis() != Direction.Axis.Y) up = up.getOpposite();

            drawPlane(up, pcm.getFluidBlockOffsets(), pose.pose(), consumer, GTMaterials.Iron.getFluid(),
                    RenderUtil.FluidTextureType.STILL, combinedOverlay, pcm);
            poseStack.popPose();

            long gameTime = level.getGameTime();

            Vector3f ray = new Vector3f(6.0F * (float) up.getNormal().getX(), 6.0F * (float) up.getNormal().getY(),
                    6.0F * (float) up.getNormal().getZ());

            float xOffset = (float) (pcm.getFocusPos().getX() - pcm.getPos().getX());
            float yOffset = (float) (pcm.getFocusPos().getY() - pcm.getPos().getY());
            float zOffset = (float) (pcm.getFocusPos().getZ() - pcm.getPos().getZ());

            LaserUtil.renderLaser(ray, poseStack, buffer, color.r, color.b, color.g, 1.0F, xOffset, yOffset, zOffset,
                    partialTicks, gameTime,
                    BeaconRenderer.BEAM_LOCATION);
        }
    }

    // Stolen and modified from FluidBlockRenderer
    public static void drawPlane(Direction face, @NotNull Collection<BlockPos> offsets, Matrix4f pose,
                                 VertexConsumer consumer,
                                 Fluid fluid, RenderUtil.@NotNull FluidTextureType texture, int combinedOverlay,
                                 @NotNull PrismaticCrucibleMachine machine) {
        var fluidClientInfo = IClientFluidTypeExtensions.of(fluid);
        var sprite = texture.map(fluidClientInfo);
        float u0 = sprite.getU0();
        float v0 = sprite.getV0();
        float u1 = sprite.getU1();
        float v1 = sprite.getV1();
        int color = machine.getColorState().integerColor;
        int r = red(color);
        int g = green(color);
        int b = blue(color);
        int a = alpha(color);
        var normal = getNormal(face);
        var vertices = transformVertices(getVertices(face), face);
        BlockPos prevOffset = null;
        for (var offset : offsets) {
            BlockPos currOffset = prevOffset == null ? offset : offset.subtract(prevOffset);
            pose.translate(currOffset.getX(), currOffset.getY(), currOffset.getZ());
            drawFace(pose, consumer, vertices, normal, u0, u1, v0, v1, r, g, b, a, combinedOverlay,
                    LightTexture.FULL_BRIGHT);
            prevOffset = offset;
        }
    }

    // Stolen from FluidBlockRenderer
    public static void drawFace(Matrix4f pose, VertexConsumer consumer, Vector3f[] vertices, Vector3f normal, float u0,
                                float u1, float v0, float v1, int r, int g, int b, int a, int combinedOverlay,
                                int combinedLight) {
        var vert = vertices[0];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z, r, g, b, a, u0, v1, combinedOverlay, combinedLight,
                normal.x, normal.y, normal.z);
        vert = vertices[1];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z, r, g, b, a, u0, v0, combinedOverlay, combinedLight,
                normal.x, normal.y, normal.z);
        vert = vertices[2];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z, r, g, b, a, u1, v0, combinedOverlay, combinedLight,
                normal.x, normal.y, normal.z);
        vert = vertices[3];
        RenderUtil.vertex(pose, consumer, vert.x, vert.y, vert.z, r, g, b, a, u1, v1, combinedOverlay, combinedLight,
                normal.x, normal.y, normal.z);
    }

    // Stolen from FluidBlockRenderer
    public static Vector3f[] transformVertices(Vector3f[] vertices, Direction face) {
        var newVertices = new Vector3f[4];
        float offsetX = 0;
        float offsetY = 0;
        float offsetZ = 0;
        switch (face) {
            case DOWN, UP -> offsetY -= 0.125F;
            case NORTH, SOUTH -> offsetZ -= 0.125F;
            case WEST, EAST -> offsetX -= 0.125F;
        }
        for (int i = 0; i < 4; i++)
            newVertices[i] = RenderUtil.transformVertex(vertices[i], face, offsetX, offsetY, offsetZ);
        return newVertices;
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

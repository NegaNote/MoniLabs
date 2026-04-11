package net.neganote.monilabs.client.render;

import net.minecraft.world.phys.AABB;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class BlackHoleRendererHelpers {

    public static boolean isRenderingMinecraftTranslucentLayer = false;
    public static boolean isTranslucentShader;

    public static void addBoxTriangles(PoseStack poseStack, BufferBuilder builder,
                                       float x0, float y0, float z0,
                                       float x1, float y1, float z1,
                                       float r, float g, float b, float a) {
        Matrix4f mat = poseStack.last().pose();
        // Front face (z1)
        builder.vertex(mat, x0, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z1).color(r, g, b, a).endVertex();
        // Back face (z0)
        builder.vertex(mat, x1, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z0).color(r, g, b, a).endVertex();
        // Top face (y1)
        builder.vertex(mat, x0, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z0).color(r, g, b, a).endVertex();
        // Bottom face (y0)
        builder.vertex(mat, x0, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y0, z1).color(r, g, b, a).endVertex();
        // Right face (x1)
        builder.vertex(mat, x1, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x1, y1, z1).color(r, g, b, a).endVertex();
        // Left face (x0)
        builder.vertex(mat, x0, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y0, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y0, z0).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z1).color(r, g, b, a).endVertex();
        builder.vertex(mat, x0, y1, z0).color(r, g, b, a).endVertex();
    }

    public static AABB createAABBAt(Vector3f center, float size) {
        double halfX = (double) size / 2.0;
        double halfY = (double) size / 2.0;
        double halfZ = (double) size / 2.0;

        return new AABB(
                center.x - halfX, center.y - halfY, center.z - halfZ,
                center.x + halfX, center.y + halfY, center.z + halfZ);
    }
}

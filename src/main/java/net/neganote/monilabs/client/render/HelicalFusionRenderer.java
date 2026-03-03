package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Direction;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class HelicalFusionRenderer extends DynamicRender<FusionReactorMachine, HelicalFusionRenderer> {

    public static final HelicalFusionRenderer INSTANCE = new HelicalFusionRenderer();
    public static final Codec<HelicalFusionRenderer> CODEC = Codec.unit(() -> INSTANCE);
    public static final DynamicRenderType<FusionReactorMachine, HelicalFusionRenderer> TYPE = new DynamicRenderType<>(
            CODEC);

    private static final float SCALE = 2.0f;
    private static final Vec3 OFFSET = new Vec3(-2.0, -1.25, 0.0);
    private static final float OUTER_RADIUS = 0.2f;
    private static final float INNER_RADIUS = 0.12f;
    private static final float TWIST_SPEED = 10f;
    private static final float FADEOUT = 60f;
    private static final double LOD_NEAR = 64.0;
    private static final double LOD_MID = 128.0;

    private float delta = 0f;
    private int lastColor = -1;

    public HelicalFusionRenderer() {}

    @Override
    public @NotNull DynamicRenderType<FusionReactorMachine, HelicalFusionRenderer> getType() {
        return TYPE;
    }

    @Override
    public boolean shouldRender(FusionReactorMachine machine, @NotNull Vec3 cameraPos) {
        return machine.isFormed() && (machine.getRecipeLogic().isWorking() || delta > 0);
    }

    @Override
    public void render(FusionReactorMachine machine, float partialTick,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        RecipeLogic logic = machine.getRecipeLogic();
        if (logic.isWorking()) {
            lastColor = machine.getColor();
            delta = FADEOUT;
        } else if (delta > 0 && lastColor != -1) {
            delta -= Minecraft.getInstance().getDeltaFrameTime();
        } else return;

        float alpha = Mth.clamp(delta / FADEOUT, 0f, 1f);
        int[] color = getProcessedColor(lastColor);
        float time = (machine.getOffsetTimer() + partialTick) * 0.02f;

        Direction frontDir = machine.getFrontFacing();
        Direction upDir = RelativeDirection.UP.getRelative(frontDir, machine.getUpwardsFacing(), machine.isFlipped());
        Direction leftDir = RelativeDirection.LEFT.getRelative(frontDir, machine.getUpwardsFacing(),
                machine.isFlipped());

        Vec3 vFront = Vec3.atLowerCornerOf(frontDir.getNormal());
        Vec3 vUp = Vec3.atLowerCornerOf(upDir.getNormal());
        Vec3 vLeft = Vec3.atLowerCornerOf(leftDir.getNormal());

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double distSq = cam.distanceToSqr(Vec3.atCenterOf(machine.getPos()));
        int segments = distSq < LOD_NEAR * LOD_NEAR ? 400 : (distSq < LOD_MID * LOD_MID ? 260 : 160);
        int crossSections = distSq < LOD_NEAR * LOD_NEAR ? 12 : 8;

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);

        Vec3 dynamicOffset = vFront.scale(-2.0)
                .add(vUp.scale(3.5))
                .add(vLeft.scale(4.0));

        poseStack.translate(dynamicOffset.x, dynamicOffset.y, dynamicOffset.z);

        VertexConsumer vc = buffer.getBuffer(HelicalRenderHelpers.LIGHT_RING());

        renderHelix(poseStack, vc, time, 0f, color, alpha, segments, crossSections, vFront, vUp, vLeft);
        renderHelix(poseStack, vc, time, Mth.PI, color, alpha, segments, crossSections, vFront, vUp, vLeft);

        poseStack.popPose();
    }

    private void renderHelix(PoseStack stack, VertexConsumer vc, float time, float phase,
                             int[] c, float alpha, int segments, int crossSections,
                             Vec3 f, Vec3 u, Vec3 l) {
        final float OUTER_ALPHA = 0.35f;
        final float INNER_ALPHA = 0.15f;
        final float OUTER_DEPTH_NUDGE = 0.015f;
        final float INNER_RADIUS_SCALE = 0.85f;

        int ringCount = segments + 1;
        Vec3[] centers = new Vec3[ringCount];
        Vec3[] tangents = new Vec3[ringCount];

        float dt = Mth.TWO_PI / segments;
        for (int i = 0; i < ringCount; i++) {
            float t = (i % segments) * dt + phase;
            centers[i] = computeLissajous(t, time, f, u, l);
        }
        for (int i = 0; i < segments; i++) {
            tangents[i] = centers[i + 1].subtract(centers[i]).normalize();
        }
        tangents[segments] = tangents[0];

        Vec3[] normals = new Vec3[ringCount];
        Vec3[] binorms = new Vec3[ringCount];
        Vec3 helper = Math.abs(tangents[0].y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        normals[0] = helper.subtract(tangents[0].scale(helper.dot(tangents[0]))).normalize();
        binorms[0] = tangents[0].cross(normals[0]).normalize();

        for (int i = 1; i <= segments; i++) {
            normals[i] = normals[i - 1].subtract(tangents[i].scale(normals[i - 1].dot(tangents[i]))).normalize();
            binorms[i] = tangents[i].cross(normals[i]).normalize();
        }

        Matrix4f pose = stack.last().pose();
        for (int i = 0; i < segments; i++) {
            float twist = time * TWIST_SPEED + i * 0.12f;
            for (int v = 0; v < crossSections; v++) {
                // Outer Tube
                Vec3[] qOuter = getQuad(i, v, centers, tangents, normals, binorms, crossSections, OUTER_RADIUS, twist,
                        0, 1.015f);
                drawQuad(vc, pose, qOuter, c, alpha * OUTER_ALPHA);

                // Inner Tube
                Vec3[] qInner = getQuad(i, v, centers, tangents, normals, binorms, crossSections,
                        INNER_RADIUS * INNER_RADIUS_SCALE, twist, time, 1.0f);
                drawQuad(vc, pose, qInner, c, alpha * INNER_ALPHA);
            }
        }
    }

    private Vec3 computeLissajous(float t, float time, Vec3 f, Vec3 u, Vec3 l) {
        float bx = 0.5f * Mth.cos(4 * t + time * 2.0f);
        float by = 0.5f * Mth.sin(4 * t + time * 2.0f);
        float bz = 6.0f * Mth.cos(t) * 0.8f;

        return l.scale(bz + OFFSET.x)
                .add(u.scale(by + OFFSET.y))
                .add(f.scale(bx + OFFSET.z))
                .scale(SCALE);
    }

    private Vec3[] getQuad(int i, int v, Vec3[] c, Vec3[] t, Vec3[] n, Vec3[] b,
                           int res, float rad, float twist, float pTime, float nudge) {
        Vec3[] quad = new Vec3[4];
        for (int k = 0; k < 4; k++) {
            int rIdx = i + (k == 2 || k == 3 ? 1 : 0);
            int vIdx = v + (k == 1 || k == 2 ? 1 : 0);
            float angle = vIdx * Mth.TWO_PI / res;

            Vec3 nt = rotate(n[rIdx], t[rIdx], twist);
            Vec3 bt = t[rIdx].cross(nt).normalize();
            float pulse = pTime == 0 ? 1.0f : 0.96f + 0.04f * Mth.sin(rIdx * 0.1f + pTime * 3f);

            quad[k] = c[rIdx].add(nt.scale(Mth.cos(angle) * rad * pulse))
                    .add(bt.scale(Mth.sin(angle) * rad * pulse))
                    .scale(nudge);
        }
        return quad;
    }

    private void drawQuad(VertexConsumer vc, Matrix4f pose, Vec3[] q, int[] c, float a) {
        for (Vec3 p : q) {
            vc.vertex(pose, (float) p.x, (float) p.y, (float) p.z)
                    .color(c[0] / 255f, c[1] / 255f, c[2] / 255f, a).endVertex();
        }
    }

    private Vec3 rotate(Vec3 v, Vec3 axis, float ang) {
        float co = Mth.cos(ang);
        float si = Mth.sin(ang);
        return v.scale(co).add(axis.cross(v).scale(si)).add(axis.scale(axis.dot(v) * (1 - co)));
    }

    private int[] getProcessedColor(int color) {
        float r = FastColor.ARGB32.red(color) / 255f;
        float g = FastColor.ARGB32.green(color) / 255f;
        float b = FastColor.ARGB32.blue(color) / 255f;
        float lum = 0.2126f * r + 0.7152f * g + 0.0722f * b;
        if (lum > 0.65f) {
            float s = 0.65f / lum;
            r *= s;
            g *= s;
            b *= s;
        }
        return new int[] { (int) (r * 255), (int) (g * 255), (int) (b * 255) };
    }

    @Override
    public boolean shouldRenderOffScreen(FusionReactorMachine m) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public @NotNull AABB getRenderBoundingBox(FusionReactorMachine m) {
        return new AABB(m.getPos()).inflate(60);
    }
}

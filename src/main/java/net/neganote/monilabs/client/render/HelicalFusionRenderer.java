package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
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
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

public class HelicalFusionRenderer extends DynamicRender<FusionReactorMachine, HelicalFusionRenderer> {

    public static final HelicalFusionRenderer INSTANCE = new HelicalFusionRenderer(
            2.0f,
            new Vec3(-2.0, -1, 0),
            0.09f,
            0.12f,
            10f);

    private static final float FADEOUT = 60f;
    private static final double LOD_NEAR = 64.0;
    private static final double LOD_MID = 128.0;

    private final float scale;
    private final Vec3 offset;
    private final float outerRadius;
    private final float innerRadius;
    private final float twistSpeed;

    private float delta = 0f;
    private int lastColor = -1;

    public HelicalFusionRenderer(float scale, Vec3 offset, float outerRadius, float innerRadius, float twistSpeed) {
        this.scale = scale;
        this.offset = offset;
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.twistSpeed = twistSpeed;
    }

    public static final Codec<HelicalFusionRenderer> CODEC = Codec.unit(() -> INSTANCE);
    public static final DynamicRenderType<FusionReactorMachine, HelicalFusionRenderer> TYPE = new DynamicRenderType<>(
            CODEC);

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
        if (!machine.isFormed()) return;

        RecipeLogic logic = machine.getRecipeLogic();
        if (logic.isWorking()) {
            lastColor = machine.getColor();
            delta = FADEOUT;
        } else if (delta > 0 && lastColor != -1) {
            delta -= Minecraft.getInstance().getDeltaFrameTime();
        } else return;

        float alpha = Mth.clamp(delta / FADEOUT, 0f, 1f);

        float rf = FastColor.ARGB32.red(lastColor) / 255f;
        float gf = FastColor.ARGB32.green(lastColor) / 255f;
        float bf = FastColor.ARGB32.blue(lastColor) / 255f;
        float lum = 0.2126f * rf + 0.7152f * gf + 0.0722f * bf;
        if (lum > 0.65f) {
            float s = 0.65f / lum;
            rf *= s;
            gf *= s;
            bf *= s;
        }
        int rBase = (int) (rf * 255f);
        int gBase = (int) (gf * 255f);
        int bBase = (int) (bf * 255f);

        float time = (machine.getOffsetTimer() + partialTick) * 0.02f;

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double distSq = cam.distanceToSqr(Vec3.atCenterOf(machine.getPos()));
        int segments = distSq < LOD_NEAR * LOD_NEAR ? 400 : (distSq < LOD_MID * LOD_MID ? 260 : 160);
        int crossSections = distSq < LOD_NEAR * LOD_NEAR ? 12 : 8;

        poseStack.pushPose();

        poseStack.translate(0.5, 0.5, 0.5);

        Direction facing = machine.getFrontFacing();
        poseStack.mulPose(facing.getRotation());

        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        poseStack.translate(offset.x, offset.y, offset.z);
        poseStack.scale(scale, scale, scale);

        VertexConsumer vc = buffer.getBuffer(HelicalRenderHelpers.LIGHT_RING());

        renderHelix(poseStack, vc, time, 0f, rBase, gBase, bBase, alpha, segments, crossSections);
        renderHelix(poseStack, vc, time, Mth.PI, rBase, gBase, bBase, alpha, segments, crossSections);

        poseStack.popPose();
    }

    private void renderHelix(PoseStack stack, VertexConsumer vc, float time, float phase,
                             int rBase, int gBase, int bBase, float alpha,
                             int segments, int crossSections) {
        final float OUTER_ALPHA = 0.35f;
        final float INNER_ALPHA = 0.15f;
        final float OUTER_DEPTH_NUDGE = 0.015f;
        final float INNER_RADIUS_SCALE = 0.85f;

        int ringCount = segments + 1;
        Vec3[] centers = new Vec3[ringCount];
        Vec3[] tangents = new Vec3[ringCount];
        Vec3[] normals = new Vec3[ringCount];
        Vec3[] binorms = new Vec3[ringCount];

        computeCurve(time, phase, segments, centers, tangents);
        computeBishopFrame(segments, tangents, normals, binorms);

        centers[segments] = centers[0];
        tangents[segments] = tangents[0];
        normals[segments] = normals[0];
        binorms[segments] = binorms[0];

        Vec3[][] outer = new Vec3[ringCount][crossSections + 1];
        Vec3[][] inner = new Vec3[ringCount][crossSections + 1];

        buildRings(time, ringCount, crossSections, centers, tangents, normals, binorms, outer, inner,
                innerRadius * INNER_RADIUS_SCALE);

        renderTube(stack, vc, outer, segments, crossSections, rBase, gBase, bBase, alpha * OUTER_ALPHA,
                OUTER_DEPTH_NUDGE);
        renderTube(stack, vc, inner, segments, crossSections, rBase, gBase, bBase, alpha * INNER_ALPHA, 0.0f);
    }

    private void buildRings(float time, int ringCount, int crossSections, Vec3[] c, Vec3[] t, Vec3[] n, Vec3[] b,
                            Vec3[][] o, Vec3[][] in, float inRad) {
        for (int i = 0; i < ringCount; i++) {
            float twist = time * twistSpeed + i * 0.12f;
            Vec3 nt = rotate(n[i], t[i], twist);
            Vec3 bt = t[i].cross(nt).normalize();

            for (int v = 0; v <= crossSections; v++) {
                float angle = v * Mth.TWO_PI / crossSections;
                float cos = Mth.cos(angle);
                float sin = Mth.sin(angle);

                o[i][v] = c[i].add(nt.scale(cos * outerRadius)).add(bt.scale(sin * outerRadius));
                float pulse = 0.96f + 0.04f * Mth.sin(i * 0.1f + time * 3f);
                in[i][v] = c[i].add(nt.scale(cos * inRad * pulse)).add(bt.scale(sin * inRad * pulse));
            }
        }
    }

    private void renderTube(PoseStack stack, VertexConsumer vc, Vec3[][] rings, int segments, int crossSections, int r,
                            int g, int b, float a, float depthNudge) {
        Matrix4f pose = stack.last().pose();
        for (int i = 0; i < segments; i++) {
            for (int v = 0; v < crossSections; v++) {
                float s = 1.0f + depthNudge;
                quad(vc, pose, rings[i][v].scale(s), rings[i][v + 1].scale(s), rings[i + 1][v + 1].scale(s),
                        rings[i + 1][v].scale(s), r, g, b, a);
            }
        }
    }

    private void computeCurve(float time, float phase, int segments, Vec3[] c, Vec3[] t) {
        float dt = Mth.TWO_PI / segments;
        for (int i = 0; i < segments; i++) {
            c[i] = lissajous(i * dt + phase, time);
        }
        for (int i = 0; i < segments; i++) {
            int next = (i + 1) % segments;
            t[i] = lissajous((next * dt) + phase, time).subtract(c[i]).normalize();
        }
    }

    private void computeBishopFrame(int segments, Vec3[] t, Vec3[] n, Vec3[] b) {
        Vec3 up = Math.abs(t[0].y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        n[0] = up.subtract(t[0].scale(up.dot(t[0]))).normalize();
        b[0] = t[0].cross(n[0]).normalize();
        for (int i = 1; i < segments; i++) {
            n[i] = n[i - 1].subtract(t[i].scale(n[i - 1].dot(t[i]))).normalize();
            b[i] = t[i].cross(n[i]).normalize();
        }
    }

    private Vec3 lissajous(float t, float time) {
        float rotationSpeed = 2.0f;

        float bx = 0.5f * Mth.cos(4 * t + time * rotationSpeed);
        float by = 0.5f * Mth.sin(4 * t + time * rotationSpeed);

        float bz = 6.0f * Mth.cos(t);

        return new Vec3(bx, by, bz * 0.8f);
    }

    private Vec3 rotate(Vec3 v, Vec3 axis, float ang) {
        double c = Math.cos(ang);
        double s = Math.sin(ang);
        return v.scale(c).add(axis.cross(v).scale(s)).add(axis.scale(axis.dot(v) * (1 - c)));
    }

    private void quad(VertexConsumer vc, Matrix4f pose, Vec3 a, Vec3 b, Vec3 c, Vec3 d, int r, int g, int bl,
                      float al) {
        vertex(vc, pose, a, r, g, bl, al);
        vertex(vc, pose, b, r, g, bl, al);
        vertex(vc, pose, c, r, g, bl, al);
        vertex(vc, pose, d, r, g, bl, al);
    }

    private void vertex(VertexConsumer vc, Matrix4f pose, Vec3 p, int r, int g, int b, float a) {
        vc.vertex(pose, (float) p.x, (float) p.y, (float) p.z).color(r / 255f, g / 255f, b / 255f, a).endVertex();
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

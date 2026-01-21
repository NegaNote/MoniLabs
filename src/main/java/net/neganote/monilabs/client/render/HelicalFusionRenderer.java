package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.common.machine.multiblock.electric.FusionReactorMachine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
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

    public static final HelicalFusionRenderer INSTANCE = new HelicalFusionRenderer(
            1f,
            Vec3.ZERO,
            0.45f * 0.2f,
            0.60f * 0.2f,
            100f);
    // ---------------- LOD & Fade ----------------
    private static final float FADEOUT = 60f;
    private static final double LOD_NEAR = 48.0;
    private static final double LOD_MID = 96.0;

    private static final int RING_SEGMENTS = 16;
    private static final int RING_VERTS = RING_SEGMENTS + 1;

    private static final Vec3[] BASE_RING = new Vec3[RING_VERTS];
    static {
        for (int i = 0; i <= RING_SEGMENTS; i++) {
            float a = (float) (2.0 * Math.PI * i / RING_SEGMENTS);
            BASE_RING[i] = new Vec3(Mth.cos(a), Mth.sin(a), 0);
        }
    }

    // ---------------- Parameters ----------------
    private final float scale;
    private final Vec3 offset;         // now relative to machine orientation
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

    // ---------------- DynamicRenderType / Codec ----------------
    public static final Codec<HelicalFusionRenderer> CODEC = Codec
            .unit(() -> new HelicalFusionRenderer(1f, Vec3.ZERO, 0.45f * 0.2f, 0.6f * 0.2f, 100f));
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
        int recipeColor = machine.getColor();

        if (logic.isWorking()) {
            lastColor = recipeColor;
            delta = FADEOUT;
        } else if (delta > 0 && lastColor != -1) {
            delta -= Minecraft.getInstance().getDeltaFrameTime();
        } else return;

        float alpha = Mth.clamp(delta / FADEOUT, 0f, 1f);

        // ---------------- COLOR STABILIZATION ----------------
        float rf = FastColor.ARGB32.red(lastColor) / 255f;
        float gf = FastColor.ARGB32.green(lastColor) / 255f;
        float bf = FastColor.ARGB32.blue(lastColor) / 255f;

        float lum = 0.2126f * rf + 0.7152f * gf + 0.0722f * bf;
        final float MAX_LUM = 0.65f;
        if (lum > MAX_LUM) {
            float scaleLum = MAX_LUM / lum;
            rf *= scaleLum;
            gf *= scaleLum;
            bf *= scaleLum;
        }
        if (bf > rf && bf > gf && lum > 0.55f) {
            bf *= 0.90f;
            gf *= 0.95f;
        }

        int rBase = (int) (rf * 255f);
        int gBase = (int) (gf * 255f);
        int bBase = (int) (bf * 255f);

        float time = (machine.getOffsetTimer() + partialTick) * 0.02f;

        Vec3 cam = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        Vec3 center = Vec3.atCenterOf(machine.getPos());
        double distSq = cam.distanceToSqr(center);

        int segments, crossSections;
        if (distSq < LOD_NEAR * LOD_NEAR) {
            segments = 400;
            crossSections = 12;
        } else if (distSq < LOD_MID * LOD_MID) {
            segments = 260;
            crossSections = 10;
        } else {
            segments = 160;
            crossSections = 8;
        }

        poseStack.pushPose();

        // ---------------- Apply Relative Offset ----------------
        Vec3 frontStep = new Vec3(
                machine.getFrontFacing().step().x(),
                machine.getFrontFacing().step().y(),
                machine.getFrontFacing().step().z()).normalize().scale(offset.length());

        Vec3 rotatedOffset = new Vec3(
                frontStep.x * offset.x - frontStep.z * offset.z,
                offset.y,
                frontStep.x * offset.z + frontStep.z * offset.x);

        poseStack.translate(rotatedOffset.x, rotatedOffset.y, rotatedOffset.z);

        // ---------------- Apply scale ----------------
        poseStack.scale(scale, scale, scale);

        VertexConsumer vc = buffer.getBuffer(HelicalRenderHelpers.LIGHT_RING());

        renderHelix(poseStack, vc, time, 0f, rBase, gBase, bBase, alpha, segments, crossSections);
        renderHelix(poseStack, vc, time, Mth.PI, rBase, gBase, bBase, alpha, segments, crossSections);

        poseStack.popPose();
    }

    // ---------------- HELIX / RING FUNCTIONS ----------------
    private void renderHelix(PoseStack stack, VertexConsumer vc,
                             float time, float phase,
                             int rBase, int gBase, int bBase, float alpha,
                             int segments, int crossSections) {
        final float OUTER_ALPHA = 0.35f;
        final float INNER_ALPHA = 0.15f;
        final float OUTER_DEPTH_NUDGE = 0.015f;
        final float INNER_DEPTH_NUDGE = 0.0f;
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

        float innerRadiusVal = innerRadius * INNER_RADIUS_SCALE;

        buildRings(time, ringCount, crossSections, centers, tangents, normals, binorms, outer, inner, innerRadiusVal);

        renderTube(stack, vc, outer, segments, crossSections, rBase, gBase, bBase, alpha * OUTER_ALPHA,
                OUTER_DEPTH_NUDGE);
        renderTube(stack, vc, inner, segments, crossSections, rBase, gBase, bBase, alpha * INNER_ALPHA,
                INNER_DEPTH_NUDGE);
    }

    private void buildRings(float time, int ringCount, int crossSections,
                            Vec3[] c, Vec3[] t,
                            Vec3[] n, Vec3[] b,
                            Vec3[][] o, Vec3[][] in,
                            float innerRadius) {
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
                in[i][v] = c[i].add(nt.scale(innerRadius * pulse)).add(bt.scale(sin * innerRadius * pulse));
            }
        }
    }

    private void renderTube(PoseStack stack, VertexConsumer vc,
                            Vec3[][] rings, int segments, int crossSections,
                            int rBase, int gBase, int bBase, float alpha, float depthNudge) {
        Matrix4f pose = stack.last().pose();

        for (int i = 0; i < segments; i++) {
            int nextI = i + 1;

            for (int v = 0; v < crossSections; v++) {
                int nextV = v + 1;
                float s = 1.0f + depthNudge;

                Vec3 v1 = rings[i][v].scale(s);
                Vec3 v2 = rings[i][nextV].scale(s);
                Vec3 v3 = rings[nextI][nextV].scale(s);
                Vec3 v4 = rings[nextI][v].scale(s);

                quad(vc, pose, v1, v2, v3, v4, rBase, gBase, bBase, alpha);
            }
        }
    }

    private void computeCurve(float time, float phase, int segments,
                              Vec3[] c, Vec3[] t) {
        float dt = Mth.TWO_PI / segments;
        for (int i = 0; i < segments; i++) {
            Vec3 p0 = lissajous(i * dt + phase, time);
            Vec3 p1 = lissajous((i + 1) * dt + phase, time);
            c[i] = p0;
            t[i] = p1.subtract(p0).normalize();
        }
    }

    private void computeBishopFrame(int segments, Vec3[] t,
                                    Vec3[] n, Vec3[] b) {
        Vec3 up = Math.abs(t[0].y) > 0.9 ? new Vec3(1, 0, 0) : new Vec3(0, 1, 0);
        n[0] = up.subtract(t[0].scale(up.dot(t[0]))).normalize();
        b[0] = t[0].cross(n[0]).normalize();

        for (int i = 1; i < segments; i++) {
            Vec3 ni = n[i - 1].subtract(t[i].scale(n[i - 1].dot(t[i]))).normalize();
            n[i] = ni;
            b[i] = t[i].cross(ni).normalize();
        }
    }

    private Vec3 lissajous(float t, float time) {
        float scale = 0.02f;
        float x = 2f * scale * Mth.cos(4 * t);
        float y = 2f * scale * Mth.sin(4 * t);
        float z = 17f * scale * Mth.cos(t);

        float c = Mth.cos(time);
        float s = Mth.sin(time);

        return new Vec3(
                x * c - y * s,
                x * s + y * c,
                z * 0.4f);
    }

    private Vec3 rotate(Vec3 v, Vec3 axis, float ang) {
        double c = Math.cos(ang);
        double s = Math.sin(ang);
        return v.scale(c).add(axis.cross(v).scale(s)).add(axis.scale(axis.dot(v) * (1 - c)));
    }

    private void quad(VertexConsumer vc, Matrix4f pose,
                      Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                      int r, int g, int bl, float al) {
        vertex(vc, pose, a, r, g, bl, al);
        vertex(vc, pose, b, r, g, bl, al);
        vertex(vc, pose, c, r, g, bl, al);
        vertex(vc, pose, d, r, g, bl, al);
    }

    private void vertex(VertexConsumer vc, Matrix4f pose,
                        Vec3 p, int r, int g, int b, float a) {
        vc.vertex(pose, (float) p.x, (float) p.y, (float) p.z)
                .color(r / 255f, g / 255f, b / 255f, a)
                .endVertex();
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
        return new AABB(m.getPos()).inflate(40);
    }
}

package net.neganote.monilabs.client.render;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.neganote.monilabs.MoniLabs;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.pipeline.TextureTarget;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.*;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MoniLabs.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class BlackHoleRenderer {

    private static RenderTarget depthTextureForTranslucency;
    private static int cachedSlot = -1;

    // Settings
    private static final float uMass = 9.05f;
    private static final float uDistModifier = 0.20f;
    private static final float uSchwarzschildRadius = 2.55f;
    private static final int uAABBSize = 14;
    private static final float uSphereRadius = 5.2f;

    private static final List<Vector3f> blackHoles = new ArrayList<>();
    public static RenderTarget worldTexture = null;
    public static RenderTarget miscTranslucentTexture = null;

    private static int findFreeTextureSlot() {
        int maxUnits = GL41.glGetInteger(GL41.GL_MAX_TEXTURE_IMAGE_UNITS) - 1;
        int freeSlot = -1;

        int originalActiveUnit = GL41.glGetInteger(GL41.GL_ACTIVE_TEXTURE);

        for (int i = maxUnits - 1; i >= 0; i--) {
            GL41.glActiveTexture(GL41.GL_TEXTURE0 + i);
            int boundTexture = GL41.glGetInteger(GL41.GL_TEXTURE_BINDING_2D);

            if (boundTexture == 0) {
                freeSlot = i;
                break;
            }
        }

        GL41.glActiveTexture(originalActiveUnit);

        if (freeSlot == -1) {
            throw new RuntimeException("Failed to find free texture slot.");
        }

        return freeSlot;
    }

    private static void drawBlackHoleToDepthBuffer(PoseStack poseStack, Vector3f bhPos, Camera camera) {
        // Draws the black hole to a separate depth buffer for future minecraft transparent passes filtering
        Vec3 camPos = camera.getPosition();
        poseStack.pushPose();

        var viewSpaceSpherePos = poseStack.last().pose().transform(
                new Vector4f(bhPos.x - (float) camPos.x, bhPos.y - (float) camPos.y, bhPos.z - (float) camPos.z, 1.0f));

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();

        depthTextureForTranslucency.bindWrite(true);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(GL11.GL_ALWAYS);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.clearDepth(1);
        RenderSystem.clear(GL41.GL_DEPTH_BUFFER_BIT, false);
        GL31.glCullFace(GL11.GL_FRONT); // To render only back faces
        ShaderInstance shader = MoniShaders.WORMHOLE_SHADER;
        RenderSystem.setShader(() -> shader);

        shader.safeGetUniform("SpherePos").set(viewSpaceSpherePos.x, viewSpaceSpherePos.y,
                viewSpaceSpherePos.z);
        shader.safeGetUniform("uSphereRadius").set(uSphereRadius);
        shader.safeGetUniform("uWriteOnlyDepth").set(1);

        AABB box = BlackHoleRendererHelpers.createAABBAt(bhPos, uAABBSize);
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        BlackHoleRendererHelpers.addBoxTriangles(poseStack, builder,
                (float) (box.minX - camPos.x), (float) (box.minY - camPos.y), (float) (box.minZ - camPos.z),
                (float) (box.maxX - camPos.x), (float) (box.maxY - camPos.y), (float) (box.maxZ - camPos.z),
                1, 1, 0, 1);

        tessellator.end();

        poseStack.popPose();
        GL31.glCullFace(GL31.GL_BACK);
        RenderSystem.depthFunc(GL31.GL_LEQUAL);
    }

    public static void updateTextures() {
        cachedSlot = -1;
        Window w = Minecraft.getInstance().getWindow();
        int mcWidth = w.getWidth();
        int mcHeight = w.getHeight();
        if (mcWidth == 0 || mcHeight == 0)
            return;
        if (depthTextureForTranslucency != null)
            depthTextureForTranslucency.resize(mcWidth, mcHeight, false);
        else
            depthTextureForTranslucency = new TextureTarget(mcWidth, mcHeight, true, Minecraft.ON_OSX);

        if (BlackHoleRenderer.worldTexture != null)
            BlackHoleRenderer.worldTexture.resize(mcWidth, mcHeight, false);
        else
            BlackHoleRenderer.worldTexture = new TextureTarget(mcWidth, mcHeight, true, Minecraft.ON_OSX);

        if (miscTranslucentTexture != null)
            miscTranslucentTexture.resize(mcWidth, mcHeight, false);
        else
            miscTranslucentTexture = new TextureTarget(mcWidth, mcHeight, true, Minecraft.ON_OSX);
    }

    public static void render(Vector3f position) {
        blackHoles.add(position);
    }

    public static void handleTranslucentPassBegin(int programHandle) {
        if (!BlackHoleRendererHelpers.isRenderingMinecraftTranslucentLayer)
            return;

        int activeUnit = GL11.glGetInteger(GL13.GL_ACTIVE_TEXTURE);
        int uDepthLocation = GL41.glGetUniformLocation(programHandle, "u_BlackHoleDepthTexture");

        if (uDepthLocation != -1) {
            if (cachedSlot == -1) {
                cachedSlot = findFreeTextureSlot();
            }
            int targetUnit = cachedSlot;

            GL41.glUniform1i(uDepthLocation, targetUnit);

            GL13.glActiveTexture(GL13.GL_TEXTURE0 + targetUnit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextureForTranslucency.getDepthTextureId());

            GL13.glActiveTexture(activeUnit);
        }
    }

    public static void preTranslucentPass(LevelRenderer instance,
                                          RenderType renderType,
                                          PoseStack poseStack,
                                          double camX,
                                          double camY,
                                          double camZ,
                                          Matrix4f projectionMatrix,
                                          Operation<Void> original,
                                          Camera camera) {
        miscTranslucentTexture.copyDepthFrom(Minecraft.getInstance().getMainRenderTarget());
        miscTranslucentTexture.bindWrite(true);
        RenderSystem.clear(GL31.GL_COLOR_BUFFER_BIT, false);

        int currentFBO = GL41.glGetInteger(GL41.GL_FRAMEBUFFER_BINDING);

        for (Vector3f bh : blackHoles)
            drawBlackHoleToDepthBuffer(poseStack, bh, camera);

        GL41.glBindFramebuffer(GL41.GL_FRAMEBUFFER, currentFBO);

        BlackHoleRendererHelpers.isRenderingMinecraftTranslucentLayer = true;
        original.call(instance, renderType, poseStack, camX, camY, camZ, projectionMatrix);
        BlackHoleRendererHelpers.isRenderingMinecraftTranslucentLayer = false;
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_WEATHER || MoniShaders.WORMHOLE_SHADER == null)
            return;

        Window w = Minecraft.getInstance().getWindow();
        if (worldTexture.width != w.getWidth() ||
                worldTexture.height != w.getHeight())
            updateTextures();
        Vec3 camPos = event.getCamera().getPosition();
        PoseStack poseStack = event.getPoseStack();
        poseStack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        GL31.glCullFace(GL11.GL_FRONT);

        var mcWorldTexture = Minecraft.getInstance().getMainRenderTarget();

        GL31.glBindFramebuffer(GL31.GL_READ_FRAMEBUFFER, mcWorldTexture.frameBufferId);
        GL31.glBindFramebuffer(GL31.GL_DRAW_FRAMEBUFFER, worldTexture.frameBufferId);
        GlStateManager._glBlitFrameBuffer(0, 0, w.getWidth(), w.getHeight(), 0, 0, w.getWidth(), w.getHeight(),
                GL31.GL_COLOR_BUFFER_BIT, GL31.GL_NEAREST);
        GlStateManager._glBlitFrameBuffer(0, 0, w.getWidth(), w.getHeight(), 0, 0, w.getWidth(), w.getHeight(),
                GL31.GL_DEPTH_BUFFER_BIT, GL31.GL_NEAREST);

        mcWorldTexture.bindWrite(true);

        ShaderInstance shader = MoniShaders.WORMHOLE_SHADER;
        RenderSystem.setShader(() -> shader);
        shader.setSampler("WorldColor", worldTexture);
        RenderSystem.setShaderTexture(0, worldTexture.getColorTextureId());

        shader.safeGetUniform("uMass").set(uMass);
        shader.safeGetUniform("uDistModifier").set(uDistModifier);
        shader.safeGetUniform("uSchwarzschildRadius").set(uSchwarzschildRadius);
        shader.safeGetUniform("uSphereRadius").set(uSphereRadius);
        shader.safeGetUniform("uWriteOnlyDepth").set(0);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder builder = tessellator.getBuilder();
        builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);

        for (Vector3f blackHolePos : blackHoles) {
            var viewSpaceSpherePos = poseStack.last().pose().transform(
                    new Vector4f(blackHolePos.x - (float) camPos.x, blackHolePos.y - (float) camPos.y,
                            blackHolePos.z - (float) camPos.z, 1.0f));
            shader.safeGetUniform("SpherePos").set(viewSpaceSpherePos.x, viewSpaceSpherePos.y,
                    viewSpaceSpherePos.z);

            AABB box = BlackHoleRendererHelpers.createAABBAt(blackHolePos, uAABBSize);

            BlackHoleRendererHelpers.addBoxTriangles(poseStack, builder,
                    (float) (box.minX - camPos.x), (float) (box.minY - camPos.y), (float) (box.minZ - camPos.z),
                    (float) (box.maxX - camPos.x), (float) (box.maxY - camPos.y), (float) (box.maxZ - camPos.z),
                    1, 1, 0, 1);
        }
        tessellator.end();
        GL11.glCullFace(GL11.GL_BACK);
        poseStack.popPose();

        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.enableBlend();
        miscTranslucentTexture.blitToScreen(miscTranslucentTexture.width, miscTranslucentTexture.height, false);
        RenderSystem.enableDepthTest();
        blackHoles.clear();
    }

    @SubscribeEvent
    public static void onResize(ScreenEvent.Init.Post event) {
        updateTextures();
    }
}

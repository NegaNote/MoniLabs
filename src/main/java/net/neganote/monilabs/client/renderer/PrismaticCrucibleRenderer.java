package net.neganote.monilabs.client.renderer;
import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import org.joml.Quaternionf;

import java.util.List;
import java.util.function.Consumer;

// Lots of this is mostly copied from CosmicCore tbh, thanks Ghosti
public class PrismaticCrucibleRenderer extends WorkableCasingMachineRenderer {
    public static final ResourceLocation BASE_CASING = MoniLabs.id("block/casings/prismatic_casing");
    public static final ResourceLocation FRONT_TEXTURES = GTCEu.id("block/multiblock/processing_array");

    public static final ResourceLocation CUBE_MODEL = MoniLabs.id("block/prismac/cube");
    public PrismaticCrucibleRenderer() {
        super(BASE_CASING, FRONT_TEXTURES);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof IMachineBlockEntity machineBlockEntity &&
        machineBlockEntity.getMetaMachine() instanceof PrismaticCrucibleMachine machine && machine.isFormed()) {
            var level = machine.getLevel();
            var frontFacing = machine.getFrontFacing();
            assert level != null;
            float tick = level.getGameTime() + partialTicks;
            renderCube(poseStack, buffer, frontFacing, tick, combinedLight, combinedOverlay);
        }
    }

    private void renderCube(PoseStack poseStack, MultiBufferSource buffer, Direction frontFacing, float tick, int combinedLight, int combinedOverlay) {
        var modelManager = Minecraft.getInstance().getModelManager();
        poseStack.pushPose();

        BakedModel bakedModel = modelManager.getModel(CUBE_MODEL);
        Vec3i oppositeNormal = frontFacing.getOpposite().getNormal();
        float x = (float) oppositeNormal.getX();
        float y = (float) oppositeNormal.getY();
        float z = (float) oppositeNormal.getZ();

        poseStack.translate(x * 30.0 + 0.5, y * 30.0 + 0.5, z * 30.0 + 0.5);
        poseStack.mulPose(new Quaternionf().rotateAxis(tick * Mth.TWO_PI / 40, x, y, z));

        poseStack.scale(10.0f, 10.0f, 10.0f);

        PoseStack.Pose pose = poseStack.last();

        VertexConsumer consumer = buffer.getBuffer(RenderType.solid());

        @SuppressWarnings("deprecation")
        List<BakedQuad> quads = bakedModel.getQuads(null, null, GTValues.RNG);

        for (BakedQuad quad : quads) {
            consumer.putBulkData(pose, quad, 1f, 1f, 1f, combinedLight, combinedOverlay);
        }
        poseStack.popPose();
    }

    @Override
    public void onAdditionalModel(Consumer<ResourceLocation> registry) {
        super.onAdditionalModel(registry);
        registry.accept(CUBE_MODEL);
    }

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
        return 256;
    }
}

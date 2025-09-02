package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;
import com.gregtechceu.gtceu.client.util.ModelUtils;

import net.irisshaders.iris.Iris;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.CreativeEnergyMultiMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.Codec;

import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class CreativeEnergyRender extends DynamicRender<CreativeEnergyMultiMachine, CreativeEnergyRender> {

    public static final CreativeEnergyRender INSTANCE = new CreativeEnergyRender();

    // spotless:off
    public static final Codec<CreativeEnergyRender> CODEC = Codec.unit(INSTANCE);
    public static DynamicRenderType<CreativeEnergyMultiMachine, CreativeEnergyRender> TYPE = new DynamicRenderType<>(CreativeEnergyRender.CODEC);
    // spotless:on

    public static final ResourceLocation SPHERE = MoniLabs.id("renderer/sphere");
    private static BakedModel sphereModel = null;

    private CreativeEnergyRender() {
        ModelUtils.registerBakeEventListener(true, event -> {
            sphereModel = event.getModels().get(SPHERE);
        });
    }

    @Override
    public DynamicRenderType<CreativeEnergyMultiMachine, CreativeEnergyRender> getType() {
        return CreativeEnergyRender.TYPE;
    }

    @Override
    public void render(CreativeEnergyMultiMachine machine, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        var frontFacing = machine.getFrontFacing();
        var upwardsFacing = machine.getUpwardsFacing();

        Direction front = RelativeDirection.FRONT.getRelative(frontFacing, upwardsFacing, machine.isFlipped());

        Direction upwards = RelativeDirection.UP.getRelative(frontFacing, upwardsFacing, machine.isFlipped());

        List<BakedQuad> sphereQuads = sphereModel.getQuads(null, null, RandomSource.create(), ModelData.EMPTY,
                null);

        poseStack.pushPose();
        float translateX = 0.5f;
        float translateY = 0.5f;
        float translateZ = 0.5f;

        Direction back = front.getOpposite();
        Vec3i backVec = back.getNormal().multiply(6);
        Vec3i upVec = upwards.getNormal().multiply(13);

        translateX += backVec.getX() + upVec.getX();
        translateY += backVec.getY() + upVec.getY();
        translateZ += backVec.getZ() + upVec.getZ();

        poseStack.translate(translateX, translateY, translateZ);
        float radius = 3.0f;
        poseStack.scale(radius, radius, radius);

        PoseStack.Pose pose = poseStack.last();

        VertexConsumer consumer = buffer
                .getBuffer(GTCEu.isModLoaded(GTValues.MODID_OCULUS) && Iris.getCurrentPack().isPresent() ?
                        RenderType.solid() : MoniRenderTypes.WORMHOLE);

        for (BakedQuad quad : sphereQuads) {
            consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, packedLight, packedOverlay);
        }
        poseStack.popPose();
    }

    @Override
    public boolean shouldRender(CreativeEnergyMultiMachine machine, Vec3 cameraPos) {
        return machine.isFormed() && machine.isActive();
    }

    @Override
    public boolean shouldRenderOffScreen(CreativeEnergyMultiMachine machine) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 128;
    }

    @Override
    public AABB getRenderBoundingBox(CreativeEnergyMultiMachine machine) {
        return new AABB(machine.getPos()).inflate(getViewDistance(), getViewDistance() + 32, getViewDistance());
    }
}

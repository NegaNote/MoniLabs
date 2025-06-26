package net.neganote.monilabs.client.renderer;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.machine.WorkableCasingMachineRenderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.client.model.data.ModelData;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

import java.util.List;
import java.util.function.Consumer;

@SuppressWarnings("unused")
public class MicroverseProjectorRenderer extends WorkableCasingMachineRenderer {

    public MicroverseProjectorRenderer(ResourceLocation baseCasing, ResourceLocation workableModel) {
        super(baseCasing, workableModel);
    }

    public static final ResourceLocation SPHERE = MoniLabs.id("render/sphere");

    public static final ResourceLocation CUBE = MoniLabs.id("render/cube");

    @Override
    public void render(BlockEntity blockEntity, float partialTicks, PoseStack stack, MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {
        if (blockEntity instanceof MetaMachineBlockEntity mmbe &&
                mmbe.getMetaMachine() instanceof MicroverseProjectorMachine projector && projector.isFormed()) {
            var frontFacing = projector.getFrontFacing();
            var upwardsFacing = projector.getUpwardsFacing();

            Direction upwards = RelativeDirection.UP.getRelative(frontFacing, upwardsFacing, projector.isFlipped());

            renderMicroverse(stack, buffer, upwards, frontFacing, combinedLight, combinedOverlay);
        }
    }

    private void renderMicroverse(PoseStack stack, MultiBufferSource bufferSource, Direction upwards, Direction front,
                                  int combinedLight, int combinedOverlay) {
        stack.pushPose();
        var modelManager = Minecraft.getInstance().getModelManager();
        BakedModel cube = modelManager.getModel(CUBE);
        Vec3i movement = upwards.getNormal().offset(front.getOpposite().getNormal());
        stack.scale(1.004f, 1.004f, 1.004f);
        stack.translate(movement.getX() + 0.5f, movement.getY() + 0.5f, movement.getZ() + 0.5f);
        PoseStack.Pose pose = stack.last();
        VertexConsumer consumer = bufferSource.getBuffer(RenderType.endPortal());
        List<BakedQuad> quads = cube.getQuads(null, null, GTValues.RNG, ModelData.EMPTY, null);
        for (BakedQuad quad : quads) {
            consumer.putBulkData(pose, quad, 1.0f, 1.0f, 1.0f, combinedLight, combinedOverlay);
        }
        stack.popPose();
    }

    @Override
    public boolean hasTESR(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 1024;
    }

    @Override
    public boolean isGlobalRenderer(BlockEntity blockEntity) {
        return true;
    }

    @Override
    public void onAdditionalModel(Consumer<ResourceLocation> registry) {
        super.onAdditionalModel(registry);
        registry.accept(SPHERE);
        registry.accept(CUBE);
    }
}

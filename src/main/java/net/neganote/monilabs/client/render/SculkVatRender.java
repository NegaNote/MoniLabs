package net.neganote.monilabs.client.render;

import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.client.renderer.block.FluidBlockRenderer;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRender;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderType;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.phys.AABB;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class SculkVatRender extends DynamicRender<SculkVatMachine, SculkVatRender> {

    public static final List<RelativeDirection> DEFAULT_FACES = Collections.singletonList(RelativeDirection.UP);

    // spotless:off
    public static final Codec<SculkVatRender> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FluidBlockRenderer.CODEC.forGetter(SculkVatRender::getFluidBlockRenderer),
            RelativeDirection.CODEC.listOf().optionalFieldOf("drawn_faces", DEFAULT_FACES).forGetter(SculkVatRender::getDrawFaces)
    ).apply(instance, SculkVatRender::new));
    public static final DynamicRenderType<SculkVatMachine, SculkVatRender> TYPE = new DynamicRenderType<>(SculkVatRender.CODEC);
    // spotless:on

    @Getter
    private final FluidBlockRenderer fluidBlockRenderer;
    @Getter
    private final List<RelativeDirection> drawFaces;

    public SculkVatRender(FluidBlockRenderer fluidBlockRenderer, List<RelativeDirection> drawFaces) {
        this.fluidBlockRenderer = fluidBlockRenderer;
        this.drawFaces = drawFaces.isEmpty() ? DEFAULT_FACES : drawFaces;
    }

    @Override
    public DynamicRenderType<SculkVatMachine, SculkVatRender> getType() {
        return TYPE;
    }

    @Override
    public int getViewDistance() {
        return 32;
    }

    @Override
    public void render(SculkVatMachine sculkVat, float partialTick,
                       PoseStack poseStack, MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {
        // it's acting supremely fucked right now and not actually rendering the thing. so...
        // comment it out for the moment

        /*
         * if (!ConfigHolder.INSTANCE.client.renderer.renderFluids) return;
         * if (!sculkVat.isFormed() || sculkVat.getFluidOffsets() == null) {
         * return;
         * }
         * 
         * var fluidStack = sculkVat.getOutputTankFluid();
         * 
         * var cachedFluid = fluidStack.getFluid();
         * 
         * if (cachedFluid == null) {
         * return;
         * }
         * 
         * poseStack.pushPose();
         * var pose = poseStack.last().pose();
         * 
         * var fluidRenderType = ItemBlockRenderTypes.getRenderLayer(cachedFluid.defaultFluidState());
         * var consumer = buffer.getBuffer(RenderTypeHelper.getEntityRenderType(fluidRenderType, false));
         * 
         * for (RelativeDirection face : this.drawFaces) {
         * var dir = face.getRelative(sculkVat.self().getFrontFacing(), sculkVat.self().getUpwardsFacing(),
         * sculkVat.self().isFlipped());
         * if (dir.getAxis() != Direction.Axis.Y) dir = dir.getOpposite();
         * 
         * fluidBlockRenderer.drawPlane(dir, sculkVat.getFluidOffsets(), pose, consumer, cachedFluid,
         * RenderUtil.FluidTextureType.STILL, packedOverlay, sculkVat.self().getPos());
         * }
         * 
         * poseStack.popPose();
         */
    }

    @Override
    public boolean shouldRenderOffScreen(SculkVatMachine machine) {
        return true;
    }

    @Override
    public AABB getRenderBoundingBox(SculkVatMachine machine) {
        AABB box = super.getRenderBoundingBox(machine);
        var offsets = machine.getFluidOffsets();
        for (var offset : offsets) {
            box = box.minmax(new AABB(offset));
        }
        return box.inflate(getViewDistance());
    }
}

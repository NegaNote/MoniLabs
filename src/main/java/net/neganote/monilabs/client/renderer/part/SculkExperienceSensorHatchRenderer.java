package net.neganote.monilabs.client.renderer.part;

import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.client.renderer.machine.MachineRenderer;
import com.gregtechceu.gtceu.client.util.StaticFaceBakery;

import com.lowdragmc.lowdraglib.client.bakedpipeline.Quad;
import com.lowdragmc.lowdraglib.client.model.ModelFactory;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;
import net.neganote.monilabs.common.machine.part.SculkExperienceSensorHatchPartMachine;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class SculkExperienceSensorHatchRenderer extends MachineRenderer {

    private final String texturePath;

    public SculkExperienceSensorHatchRenderer(String texturePath) {
        super(MoniLabs.kjsResLoc("block/cryolobus_casing"));
        this.texturePath = texturePath;
    }

    @Override
    public void renderMachine(List<BakedQuad> quads, MachineDefinition definition, @Nullable MetaMachine machine,
                              Direction frontFacing, @Nullable Direction side, RandomSource rand,
                              @Nullable Direction modelFacing, ModelState modelState) {
        super.renderMachine(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState);
        if (side == frontFacing && modelFacing != null) {
            quads.add(Quad.from(StaticFaceBakery.bakeFace(modelFacing,
                    ModelFactory.getBlockSprite(MoniLabs.id(texturePath + "_base")), modelState), 0.001F).rebake());
            int fillLevel = 0;
            if (machine instanceof SculkExperienceSensorHatchPartMachine expSensor) {
                var controllers = expSensor.getControllers().stream()
                        .filter(SculkVatMachine.class::isInstance)
                        .map(SculkVatMachine.class::cast)
                        .toList();
                if (!controllers.isEmpty()) {
                    var controller = controllers.get(0);
                    if (controller.isFormed()) {
                        fillLevel = (int) (4 * controller.getXpBuffer() /
                                ((float) SculkVatMachine.XP_BUFFER_MAX));
                        if (fillLevel == 4) {
                            fillLevel = 3;
                        }
                    }
                }
            }
            quads.add(Quad.from(StaticFaceBakery.bakeFace(modelFacing,
                    ModelFactory.getBlockSprite(MoniLabs.id(texturePath + "_overlay_" + fillLevel)), modelState, -101,
                    15), 0.002F)
                    .rebake());
        }
    }

    @Override
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        if (atlasName.equals(InventoryMenu.BLOCK_ATLAS)) {
            register.accept(MoniLabs.id(texturePath + "_base"));
            for (int i = 0; i < 4; i++) {
                register.accept(MoniLabs.id(texturePath + "_overlay_" + i));
            }

        }
    }
}

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
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;
import net.neganote.monilabs.common.machine.part.ChromaSensorHatchPartMachine;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ChromaSensorHatchRenderer extends MachineRenderer {

    private final String texturePath;

    public ChromaSensorHatchRenderer(String texturePath) {
        super(MoniLabs.kjsResLoc("block/dimensional_stabilization_netherite_casing"));
        this.texturePath = texturePath;
    }

    @Override
    public void renderMachine(List<BakedQuad> quads, MachineDefinition definition, @Nullable MetaMachine machine,
                              Direction frontFacing, @Nullable Direction side, RandomSource rand,
                              @Nullable Direction modelFacing, ModelState modelState) {
        super.renderMachine(quads, definition, machine, frontFacing, side, rand, modelFacing, modelState);
        int color = 0;
        if (side == frontFacing && modelFacing != null) {
            if (machine instanceof ChromaSensorHatchPartMachine chromaSensor) {
                var controllers = chromaSensor.getControllers().stream()
                        .filter(PrismaticCrucibleMachine.class::isInstance)
                        .map(PrismaticCrucibleMachine.class::cast)
                        .toList();
                if (!controllers.isEmpty()) {
                    var controller = controllers.get(0);
                    color = controller.isFormed() ? controller.getColorState().key + 1 : 0;
                }
            }
            quads.add(Quad.from(StaticFaceBakery.bakeFace(modelFacing,
                    ModelFactory.getBlockSprite(MoniLabs.id(texturePath + "_" + color)), modelState), 0.004F).rebake());
        }
    }

    @Override
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        if (atlasName.equals(InventoryMenu.BLOCK_ATLAS)) {
            for (int i = 0; i <= 12; i++) {
                register.accept(MoniLabs.id(texturePath + "_" + i));
            }
        }
    }
}

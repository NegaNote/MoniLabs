package net.neganote.monilabs.client.renderer.cover;

import com.gregtechceu.gtceu.api.cover.CoverBehavior;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.client.renderer.cover.ICoverRenderer;
import com.gregtechceu.gtceu.client.util.StaticFaceBakery;

import com.lowdragmc.lowdraglib.client.model.ModelFactory;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class ChromaDetectorCoverRenderer implements ICoverRenderer {

    String texturePath;

    public ChromaDetectorCoverRenderer(String texturePath) {
        this.texturePath = texturePath;
    }

    @Override
    public void onPrepareTextureAtlas(ResourceLocation atlasName, Consumer<ResourceLocation> register) {
        if (atlasName.equals(InventoryMenu.BLOCK_ATLAS)) {
            for (int i = 0; i <= 12; i++) {
                register.accept(MoniLabs.id(texturePath + i));
            }
        }
    }

    @Override
    public void renderCover(List<BakedQuad> quads, @Nullable Direction side, RandomSource randomSource,
                            @NotNull CoverBehavior coverBehavior, @Nullable Direction modelFacing, BlockPos blockPos,
                            BlockAndTintGetter blockAndTintGetter, ModelState modelState) {
        if (MetaMachine.getMachine(blockAndTintGetter, blockPos) instanceof PrismaticCrucibleMachine machine) {
            if (side == coverBehavior.attachedSide && modelFacing != null) {
                int color = machine.isFormed() ? machine.getColorState().key + 1 : 0;
                quads.add(StaticFaceBakery.bakeFace(modelFacing,
                        ModelFactory.getBlockSprite(MoniLabs.id(texturePath + color)), modelState));
            }
        }
    }
}

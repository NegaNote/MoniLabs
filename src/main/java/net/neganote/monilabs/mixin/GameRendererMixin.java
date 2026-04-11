package net.neganote.monilabs.mixin;

import net.irisshaders.iris.Iris;
import net.irisshaders.iris.shadows.ShadowRenderer;
import net.minecraft.client.renderer.GameRenderer;
import net.neganote.monilabs.client.render.BlackHoleRenderer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "render",
            at = @At(
                     value = "INVOKE",
                     target = "Lnet/minecraft/client/renderer/LevelRenderer;doEntityOutline()V"))
    public void moniLabs$renderBlackHoleWithShaders(float partialTicks, long nanoTime, boolean renderLevel,
                                                    CallbackInfo ci) {
        if (Iris.getCurrentPack().isPresent() && renderLevel && !ShadowRenderer.ACTIVE) {
            BlackHoleRenderer.renderWithShadersOn();
        }
    }
}

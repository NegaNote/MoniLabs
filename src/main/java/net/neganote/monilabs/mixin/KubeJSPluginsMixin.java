package net.neganote.monilabs.mixin;

import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(KubeJSPlugins.class)
public class KubeJSPluginsMixin {
    @Shadow
    private static void load(Mod mod, boolean loadClientPlugins) {}

    @Inject(method= "load(Ljava/util/List;Z)V", at = @At(value = "HEAD"))
    private static void beforeLoad(List<Mod> mods, boolean loadClientPlugins, CallbackInfo ci) {
        mods.remove(Platform.getMod("monilabs"));
    }

    @Inject(method= "load(Ljava/util/List;Z)V", at = @At(value = "RETURN"))
    private static void afterLoad(List<Mod> mods, boolean loadClientPlugins, CallbackInfo ci) {
        load(Platform.getMod("monilabs"), loadClientPlugins);
    }
}

package net.neganote.monilabs.mixin;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(KubeJSPlugins.class)
public class KubeJSPluginsMixin {
    @Inject(method= "load(Ljava/util/List;Z)V", at = @At(value = "HEAD"))
    private static void beforeLoad(List<Mod> mods, boolean loadClientPlugins, CallbackInfo ci) {
        KubeJS.LOGGER.debug("Attempting MoniLabs mixin...");
        mods.remove(Platform.getMod("monilabs"));
        mods.add(Platform.getMod("monilabs"));
        KubeJS.LOGGER.debug(mods.stream().map(Mod::getModId).collect(Collectors.joining("\n")));
    }
}

package net.neganote.monilabs.mixin;

import dev.latvian.mods.kubejs.util.KubeJSPlugins;
import dev.architectury.platform.Mod;
import dev.architectury.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(KubeJSPlugins.class)
public class KubeJSPluginsMixin {
    @Unique
    private static Logger moniLabs$LOGGER = LoggerFactory.getLogger("KubeJSMoniMixin");

    @Inject(method= "load(Ljava/util/List;Z)V", at = @At(value = "HEAD"))
    private static void beforeLoad(List<Mod> mods, boolean loadClientPlugins, CallbackInfo ci) {
        moniLabs$LOGGER.debug("Attempting MoniLabs mixin...");
        int index = mods.indexOf(Platform.getMod("monilabs"));
        Mod mod = mods.remove(index);
        mods.add(mod);
        moniLabs$LOGGER.debug(mods.stream().map(Mod::getModId).collect(Collectors.joining("\n")));
    }
}

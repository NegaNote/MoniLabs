package net.neganote.monilabs.client.render;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class MoniShaders {

    public static ShaderInstance MICROVERSE_SHADER;

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) {
        // try {
        // event.registerShader(new ShaderInstance(event.getResourceProvider(),
        // MoniLabs.id("rendertype_microverse"), DefaultVertexFormat.POSITION),
        // (shaderInstance -> MICROVERSE_SHADER = shaderInstance));
        // } catch (IOException e) {
        // throw new RuntimeException(e);
        // }
    }
}

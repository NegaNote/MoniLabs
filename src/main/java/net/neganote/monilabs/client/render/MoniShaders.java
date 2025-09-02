package net.neganote.monilabs.client.render;

import net.minecraft.client.renderer.ShaderInstance;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.neganote.monilabs.MoniLabs;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;

import java.io.IOException;

public class MoniShaders {

    public static ShaderInstance WORMHOLE_SHADER;

    @SubscribeEvent
    public static void shaderRegistry(RegisterShadersEvent event) {
        try {
            event.registerShader(new ShaderInstance(event.getResourceProvider(),
                    MoniLabs.id("rendertype_wormhole"), DefaultVertexFormat.POSITION),
                    (shaderInstance -> WORMHOLE_SHADER = shaderInstance));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

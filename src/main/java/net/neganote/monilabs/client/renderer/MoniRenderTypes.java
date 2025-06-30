package net.neganote.monilabs.client.renderer;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;

public class MoniRenderTypes {

    public static final ShaderStateShard MICROVERSE_SHADER_SHARD = new ShaderStateShard(
            () -> MoniShaders.MICROVERSE_SHADER);

    public static RenderType MICROVERSE = RenderType.create("microverse", DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder().setShaderState(MICROVERSE_SHADER_SHARD)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
                    .createCompositeState(false));
}

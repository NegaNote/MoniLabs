package net.neganote.monilabs.client.render;

import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class MoniRenderTypes {

    public static final ShaderStateShard WORMHOLE_SHADER_SHARD = new ShaderStateShard(
            () -> MoniShaders.WORMHOLE_SHADER);

    public static RenderType WORMHOLE = RenderType.create("wormhole", DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder().setShaderState(WORMHOLE_SHADER_SHARD)
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
                    .createCompositeState(false));
    public static RenderType END_PORTAL_COLORED = RenderType.create("end_portal_colored",
            DefaultVertexFormat.POSITION_COLOR, VertexFormat.Mode.QUADS, 256, false, false,
            RenderType.CompositeState.builder()
                    .setShaderState(new ShaderStateShard(() -> {
                        ShaderInstance shader = MoniShaders.ENDPORTAL_COLORED_SHADER;
                        shader.safeGetUniform("EndPortalLayers").set(15);
                        return shader;
                    }))
                    .setTextureState(RenderStateShard.MultiTextureStateShard.builder()
                            .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                            .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false).build())
                    .createCompositeState(false));
}

package net.neganote.monilabs.mixin.accessor;

import net.minecraft.client.renderer.RenderType;

import me.jellysquid.mods.sodium.client.render.chunk.terrain.TerrainRenderPass;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TerrainRenderPass.class, remap = false)
public interface TerrainRenderPassAccessor {

    @Accessor("layer")
    RenderType getLayer();
}

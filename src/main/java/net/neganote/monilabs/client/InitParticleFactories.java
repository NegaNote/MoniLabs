package net.neganote.monilabs.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.ParticleEngine;
import net.neganote.monilabs.client.render.effects.ParticleTypes;
import net.neganote.monilabs.client.render.effects.PrismFX;

public final class InitParticleFactories {

    public static void init() {
        ParticleEngine particles = Minecraft.getInstance().particleEngine;
        particles
            .register(ParticleTypes.CHROMA_BACKGROUND,
                PrismFX.PositionalColor::new);
    }
}

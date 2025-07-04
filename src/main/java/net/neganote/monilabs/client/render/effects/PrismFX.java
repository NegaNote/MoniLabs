/*
 * This file is part of Applied Energistics 2.
 * Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved.
 *
 * Applied Energistics 2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Applied Energistics 2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Applied Energistics 2.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package net.neganote.monilabs.client.render.effects;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.neganote.monilabs.client.render.effects.FastNoiseLite.NoiseType;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class PrismFX extends TextureSheetParticle {
    protected SpriteSet sprites;
    public PrismFX(ClientLevel level, double x, double y, double z,
        double par8, double par10, double par12, SpriteSet sprite) {
        super(level, x, y, z, par8, par10, par12);
        this.setSize(0.04F, 0.04F);
        this.quadSize *= random.nextFloat() * .6f + 1.2f;
        // this.xd = this.random.nextGaussian() / 256;
        // this.yd = this.random.nextGaussian() / 256;
        // this.zd = this.random.nextGaussian() / 256;
        this.xd *= (double) 0.05F;
        this.yd *= (double) 0.05F;
        this.zd *= (double) 0.05F;
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.lifetime = (int) (40.0D / (Math.random() * 0.8D + 0.1D));
        // this.lifetime = 100;
        // this.pickSprite(sprite);
        this.sprites = sprite;
        this.setSpriteFromAge(this.sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        // FIXME Might be PARTICLE_SHEET_LIT
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    @Override
    public int getLightColor(float par1) {
        // This just means full brightness
        return 15 << 20 | 15 << 4;
    }

    /**
     * Called to update the entity's position/logic.
     */
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        x += xd;
        y += yd;
        z += zd;
        // this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.quadSize *= 0.98;

        if (this.age++ >= this.lifetime || this.quadSize < .1) {
            this.remove();
        } else {
        this.setSpriteFromAge(this.sprites);
        this.age++;
    }
    }

    @OnlyIn(Dist.CLIENT)
    public static class SetColor
        implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;

        private static float RGB[] = new float[3];
        private static float alpha = 1f;

        public SetColor(SpriteSet spriteSet) { this.spriteSet = spriteSet; }

        @Override
        public Particle createParticle(SimpleParticleType typeIn,
            ClientLevel level, double x, double y, double z, double xSpeed,
            double ySpeed, double zSpeed) {
            PrismFX particle = new PrismFX(level, x, y, z, xSpeed, ySpeed,
                zSpeed,
                spriteSet);
            float r = 1 + (particle.random.nextFloat() - 1) / 10;
            particle.setColor(RGB[0] * r, RGB[1] * r, RGB[2] * r);
            particle.setAlpha(alpha);
            return particle;

        }

        public static void setColor(float... rgb) { SetColor.RGB = rgb; }

        public static void setAlpha(float alpha) { SetColor.alpha = alpha; }
    }

    /**
     * Particle that has it's color set depending on Perlin noise of it's
     * spatial coordinates
     */
    @OnlyIn(Dist.CLIENT)
    public static class PositionalColor
        implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        private static final FastNoiseLite noise = new FastNoiseLite();
        private static List<int[]> pride_flags;

        static {
            noise.SetNoiseType(NoiseType.OpenSimplex2S);
            noise.SetFrequency(.15f);
        }

        public PositionalColor(SpriteSet spriteSet) {
            this.spriteSet = spriteSet;
        }

        @Override
        public Particle createParticle(SimpleParticleType typeIn,
            ClientLevel level, double x, double y, double z, double xSpeed,
            double ySpeed, double zSpeed) {
            noise.SetSeed((int) level.getGameTime());
            PrismFX particle = new PrismFX(level, x, y, z, xSpeed, ySpeed,
                zSpeed, spriteSet);
            float value = Math
                .max(0f,
                    Math
                        .min(.999f,
                            ((noise.GetNoise(x, y, z) * 1.10f + 1) / 2)));
            int rgb;
            if (pride_flags != null) {
                Random rand = new Random(level.getGameTime());

                int[] flag = pride_flags.get(rand.nextInt(pride_flags.size()));
                int index = (int) (value * flag.length);
                rgb = flag[index];
            } else {
                rgb = Color.HSBtoRGB(value, 1, 1);
        }

            particle
                .setColor((float) ((rgb & 0xFF0000) >> 16) / 255,
                    (float) ((rgb & 0x00FF00) >> 8) / 255,
                    (float) ((rgb & 0x0000FF)) / 255);
            return particle;

        }

        public static void initPride() {
         // these flags are in no particular order (aside from being grouped
            // by gender then sexuality/romantic). They were picked by browsing
            // a list of pride flags and picking the neat looking ones. If
            // your's isn't here, it's because I never saw it or decided against
            // it for whatever reason. Feel free to ask for it
            pride_flags = new ArrayList<>();
            pride_flags
                .add(new int[]
                { 0x5BCEFA, 0xF5A9B8, 0xFFFFFF, 0xF5A9B8, 0x5BCEFA }); // transgender
            pride_flags
                .add(new int[]
                { 0xFF76A4, 0xFFFFFF, 0xC011D7, 0x000000, 0x2F3CBE }); // genderfluid
            pride_flags
                .add(new int[]
                { 0xFFF433, 0xFFF8E7, 0x9B59D0, 0x2D2D2D }); // non-binary
            pride_flags
                .add(new int[]
                { 0x000000, 0xA3A3A3, 0xFFFFFF, 0x800080 }); // asexual
            pride_flags
                .add(new int[]
                { 0x3DA542, 0xA7D379, 0xFFFFFF, 0xA9A9A9, 0x000000 }); // aromantic
            pride_flags.add(new int[] { 0xD60270, 0x9B4F96, 0x0038A8 }); // bisexual
            pride_flags
                .add(new int[]
                { 0xD52D00, 0xEF7627, 0xFF9A56, 0xFFFFFF, 0xD162A4, 0xB55690,
                    0xA30262 }); // lesbian
            pride_flags.add(new int[] { 0xFF218C, 0xFFD800, 0x21B1FF }); // pansexual
            pride_flags
                .add(new int[]
                { 0x0000FF, 0xFF0000, 0xFFFF00, 0x000000 }); // polyamory
        }
    }
}

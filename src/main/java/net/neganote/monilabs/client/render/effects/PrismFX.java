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

@OnlyIn(Dist.CLIENT)
public class PrismFX extends TextureSheetParticle {
    protected SpriteSet sprites;
    public PrismFX(ClientLevel level, double x, double y, double z,
        double par8, double par10, double par12, SpriteSet sprite) {
        super(level, x, y, z, par8, par10, par12);
        this.setSize(0.04F, 0.04F);
        this.quadSize *= this.random.nextFloat() * 0.6F + 1.9F;
        // this.xd = this.random.nextGaussian() / 256;
        // this.yd = this.random.nextGaussian() / 256;
        // this.zd = this.random.nextGaussian() / 256;
        this.xd *= (double) 0.1F;
        this.yd *= (double) 0.1F;
        this.zd *= (double) 0.1F;
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
        this.x += xd;
        y += yd;
        z += zd;
        // this.moveEntity(this.motionX, this.motionY, this.motionZ);
        // this.quadSize *= 0.95;

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
            particle.setColor(RGB[0], RGB[1], RGB[2]);
            particle.setAlpha(alpha);
            return particle;

        }

        /**
         * Pushes a color onto the stack, returning the previous color
         * 
         * @param rgb a float array containing 3 float values: red, green, and
         *            blue
         * @return previous color, remember to set it back with
         *         {@link Factory#popColor(float[])} once done
         */
        public float[] pushColor(float... rgb) {
            float[] old = RGB;
            RGB = rgb;
            return old;
        }

        /**
         * Pops a color off the stack, returning to the previous value
         * 
         * @param rgb the previous color value
         */
        public void popColor(float[] rgb) {
            RGB = rgb;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class PositionalColor
        implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet spriteSet;
        private static final FastNoiseLite noise = new FastNoiseLite();

        static {
            noise.SetNoiseType(NoiseType.Perlin);
            noise.SetFrequency(.1f);
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
            float hue = noise.GetNoise(x, y, z);
            int rgb = Color.HSBtoRGB(hue, 1, 1);
            particle
                .setColor((float) ((rgb & 0xFF0000) >> 16) / 255,
                    (float) ((rgb & 0x00FF00) >> 8) / 255,
                    (float) ((rgb & 0x0000FF)) / 255);
            return particle;

        }
    }
}

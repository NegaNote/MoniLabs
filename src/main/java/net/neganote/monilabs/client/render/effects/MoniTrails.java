package net.neganote.monilabs.client.render.effects;

import net.neganote.monilabs.MoniLabs;

import stone.mae2.api.client.trails.BackgroundTrail;
import stone.mae2.api.client.trails.CloudChamberUtil;
import stone.mae2.api.client.trails.SimpleTrail;
import stone.mae2.api.client.trails.Trail;

public abstract class MoniTrails {

    public static BackgroundTrail CHROMA_BACKGROUND;

    public static Trail CHROMA_BETA;

    public static void init() {
        CHROMA_BACKGROUND = CloudChamberUtil
            .registerBackgroundTrail(MoniLabs.id("chroma"),
                    new SimpleTrail(20, 0, .1, .1, ParticleTypes.CHROMA_BACKGROUND));

        CHROMA_BETA = new SimpleTrail(20, 0, 0, 0,
            ParticleTypes.CHROMA_BETA);
    }
}

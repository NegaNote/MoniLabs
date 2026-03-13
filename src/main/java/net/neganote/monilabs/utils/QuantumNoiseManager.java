package net.neganote.monilabs.utils;

import net.minecraft.util.Mth;

public class QuantumNoiseManager {

    private double startValue;
    private double endValue;

    private double offset = 0F;
    private int counter0 = 0;
    private int counter15 = 0;

    public QuantumNoiseManager() {
        resetNoise();
    }

    public int nextInt() {
        offset += (0.1 + Math.random() * 0.05);
        if (offset >= 1) {
            offset -= 1;
            startValue = endValue;
            endValue = randomCall();
        }
        double v = startValue + smoothStep(offset) * endValue;

        return (int) Math.floor(v);
    }

    /*
     * A random method with slight weight towards extremes to ensure a uniform distribution after interpolation.
     * Takes a random number r, and returns (3.5*smoothStep(r) + r) / 4.5
     * Also contains bad luck protection (in the 0.0012% of cases where you're *really* unlucky)
     */
    private double randomCall() {
        double rand = Math.random();
        rand = 16 * ((smoothStep(rand) * 3.5 + rand) / 4.5);

        if (counter15 >= 75) {
            rand = 15.5;
            counter15 = 0;
        } else if (counter0 >= 75) {
            rand = 0.5;
            counter0 = 0;
        }

        if (rand >= 15) {
            counter15 = 0;
        } else {
            counter15++;
        }
        if (rand < 1) {
            counter0 = 0;
        } else {
            counter0++;
        }

        return rand;
    }

    private double smoothStep(double x) {
        double y = 3 * (x * x) - 2 * (x * x * x);     // 3x^2-2x^3
        return Mth.clamp(y, 0F, 1F);
    }

    public void resetNoise() {
        startValue = randomCall();
        endValue = randomCall();
        offset = 0;
    }
}

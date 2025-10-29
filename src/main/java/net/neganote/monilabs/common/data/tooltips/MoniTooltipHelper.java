package net.neganote.monilabs.common.data.tooltips;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.utils.GradientUtil;

import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Tooltip helper for MoniLabs — provides custom animated gradients.
 */
public class MoniTooltipHelper {

    public static TextColor lerpSpaceColor(float speed) {
        int start = 0x0A0A7A;
        int end = 0x8A2BE2;

        float time = (GTValues.CLIENT_TIME & ((1 << 20) - 1)) * speed;

        float t = (float) ((Math.sin(time * Math.PI / 180.0) + 1.0) / 2.0);

        int r1 = (start >> 16) & 0xFF;
        int g1 = (start >> 8) & 0xFF;
        int b1 = start & 0xFF;

        int r2 = (end >> 16) & 0xFF;
        int g2 = (end >> 8) & 0xFF;
        int b2 = end & 0xFF;

        int r = (int) (r1 + (r2 - r1) * t);
        int g = (int) (g1 + (g2 - g1) * t);
        int b = (int) (b1 + (b2 - b1) * t);

        return TextColor.fromRgb((r << 16) | (g << 8) | b);
    }

    public static TextColor nebulaColor(float speed) {
        float baseHue = 260.5f;
        float hueRange = 25f;

        float time = (GTValues.CLIENT_TIME & ((1 << 20) - 1)) * speed;

        float hue = baseHue + (float) (Math.sin(time * Math.PI / 180.0) * (hueRange / 2f));

        return TextColor.fromRgb(GradientUtil.toRGB(hue, 95f, 65f));
    }

    public static TextColor universeColor(float speed) {
        final float HUE_A = 330.0f;
        final float BRIGHTNESS_A = 85.0f;
        final float HUE_B = 240.0f;
        final float BRIGHTNESS_B = 40.0f;
        final float SATURATION = 95.0f;

        float time = (GTValues.CLIENT_TIME & ((1 << 20) - 1)) * speed;

        float oscillator = (float) (Math.sin(time * Math.PI / 180.0));

        float t = (oscillator + 1.0f) / 2.0f;

        float finalHue = HUE_A + t * (HUE_B - HUE_A);
        float finalBrightness = BRIGHTNESS_A + t * (BRIGHTNESS_B - BRIGHTNESS_A);

        return TextColor.fromRgb(GradientUtil.toRGB(finalHue, SATURATION, finalBrightness));
    }

    public static TextColor sculkColor(float speed) {
        final float HUE_A = 180.0f;
        final float BRIGHTNESS_A = 45.0f;
        final float HUE_B = 158.0f;
        final float BRIGHTNESS_B = 60.0f;
        final float SATURATION = 45.0f;

        float time = (GTValues.CLIENT_TIME & ((1 << 20) - 1)) * speed;

        float oscillator = (float) (Math.sin(time * Math.PI / 180.0));

        float t = (oscillator + 1.0f) / 2.0f;

        float finalHue = HUE_A + t * (HUE_B - HUE_A);
        float finalBrightness = BRIGHTNESS_A + t * (BRIGHTNESS_B - BRIGHTNESS_A);

        return TextColor.fromRgb(GradientUtil.toRGB(finalHue, SATURATION, finalBrightness));
    }

    private static final TextColor[] SPACE_NEBULA_COLORS = new TextColor[] {
            TextColor.fromRgb(0x0A0A7A), // Deep Space Blue
            TextColor.fromRgb(0x8A2BE2), // Amethyst Purple
            TextColor.fromRgb(0xC850D2), // Magenta
            TextColor.fromRgb(0xFF80FF),  // Bright Pink
            TextColor.fromRgb(0xC850D2), // Magenta
            TextColor.fromRgb(0x8A2BE2), // Amethyst Purple
    };
    public static final UnaryOperator<Style> SPACE_LERP_HSL = style -> style.withColor(lerpSpaceColor(0.5f));
    private static final List<GTFormattingCode> NEBULA_CODES = new ArrayList<>();

    public static final GTFormattingCode SPACE_NEBULA_STYLE = createNewCode(10, SPACE_NEBULA_COLORS);

    public static UnaryOperator<Style> getNebulaOperator() {
        return style -> style.withColor(SPACE_NEBULA_STYLE.getCurrent());
    }

    public static final UnaryOperator<Style> NEBULA_HSL = style -> style.withColor(nebulaColor(8.0f));
    public static final UnaryOperator<Style> UNIVERSE_HSL = style -> style.withColor(universeColor(6.0f));
    public static final UnaryOperator<Style> SCULK_HSL = style -> style.withColor(sculkColor(1.0f));

    /**
     * Creates a formatting code which cycles through custom TextColor values.
     *
     * @param rate  Number of ticks to wait before changing to the next color.
     * @param codes The color list to oscillate through.
     */
    public static GTFormattingCode createNewCode(int rate, TextColor... codes) {
        if (rate <= 0) {
            GTCEu.LOGGER.error("Could not create GT Formatting Code with rate {}, must be greater than zero!", rate);
            return null;
        }
        if (codes == null || codes.length <= 1) {
            GTCEu.LOGGER.error("Could not create GT Formatting Code with codes {}, must have length greater than one!",
                    Arrays.toString(codes));
            return null;
        }
        GTFormattingCode code = new GTFormattingCode(rate, codes);
        NEBULA_CODES.add(code);
        return code;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            NEBULA_CODES.forEach(GTFormattingCode::updateIndex);
        }
    }

    public static class GTFormattingCode {

        private final int rate;
        private final TextColor[] codes;
        private int index = 0;

        private GTFormattingCode(int rate, TextColor... codes) {
            this.rate = rate;
            this.codes = codes;
        }

        public void updateIndex() {
            if (GTValues.CLIENT_TIME % rate == 0) {
                index = (index + 1) % codes.length;
            }
        }

        public TextColor getCurrent() {
            return codes[index];
        }

        @Override
        public String toString() {
            return codes[index].toString();
        }
    }
}

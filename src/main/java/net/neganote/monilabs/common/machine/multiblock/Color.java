package net.neganote.monilabs.common.machine.multiblock;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public enum Color implements StringRepresentable {

    RED("red", 0, "monilabs.prismatic.color_name.red", 1.0f, 0f, 0f, 0xFFFF0000),
    ORANGE("orange", 1, "monilabs.prismatic.color_name.orange", 1.0f, 0.5f, 0f, 0xFFFF8000),
    YELLOW("yellow", 2, "monilabs.prismatic.color_name.yellow", 1.0f, 1.0f, 0f, 0xFFFFFF00),
    LIME("lime", 3, "monilabs.prismatic.color_name.lime", 0.5f, 1.0f, 0f, 0xFF80FF00),
    GREEN("green", 4, "monilabs.prismatic.color_name.green", 0f, 1.0f, 0f, 0xFF00FF00),
    TEAL("teal", 5, "monilabs.prismatic.color_name.teal", 0f, 1.0f, 0.5f, 0xFF00FF80),
    CYAN("cyan", 6, "monilabs.prismatic.color_name.cyan", 0f, 1.0f, 1.0f, 0xFF00FFFF),
    AZURE("azure", 7, "monilabs.prismatic.color_name.azure", 0f, 0.5f, 1.0f, 0xFF0080FF),
    BLUE("blue", 8, "monilabs.prismatic.color_name.blue", 0f, 0f, 1.0f, 0xFF0000FF),
    INDIGO("indigo", 9, "monilabs.prismatic.color_name.indigo", 0.5f, 0f, 1.0f, 0xFF8000FF),
    MAGENTA("magenta", 10, "monilabs.prismatic.color_name.magenta", 1.0f, 0f, 1.0f, 0xFFFF00FF),
    PINK("pink", 11, "monilabs.prismatic.color_name.pink", 1.0f, 0f, 0.5f, 0xFFFF0080),
    PRIMARY("primary", 12, "", 0f, 0f, 0f, 0),
    SECONDARY("secondary", 13, "", 0f, 0f, 0f, 0),
    BASIC("basic", 14, "", 0f, 0f, 0f, 0),
    TERTIARY("tertiary", 15, "", 0f, 0f, 0f, 0),
    ANY("any", 16, "", 0f, 0f, 0f, 0),
    NOT_RED("not_red", 17, "", 0f, 0f, 0f, 0),
    NOT_ORANGE("not_orange", 18, "", 0f, 0f, 0f, 0),
    NOT_YELLOW("not_yellow", 19, "", 0f, 0f, 0f, 0),
    NOT_LIME("not_lime", 20, "", 0f, 0f, 0f, 0),
    NOT_GREEN("not_green", 21, "", 0f, 0f, 0f, 0),
    NOT_TEAL("not_teal", 22, "", 0f, 0f, 0f, 0),
    NOT_CYAN("not_cyan", 23, "", 0f, 0f, 0f, 0),
    NOT_AZURE("not_azure", 24, "", 0f, 0f, 0f, 0),
    NOT_BLUE("not_blue", 25, "", 0f, 0f, 0f, 0),
    NOT_INDIGO("not_indigo", 26, "", 0f, 0f, 0f, 0),
    NOT_MAGENTA("not_magenta", 27, "", 0f, 0f, 0f, 0),
    NOT_PINK("not_pink", 28, "", 0f, 0f, 0f, 0);

    public static final Color[] COLORS = Color.values();

    public static final Color[] ACTUAL_COLORS = Arrays.copyOfRange(COLORS, Color.RED.key, Color.RED.key + 12);

    public static final Color[] NOT_COLORS = Arrays.copyOfRange(COLORS, Color.NOT_RED.key, Color.NOT_RED.key + 12);

    public static final Color[] PRIMARY_COLORS = Arrays.stream(ACTUAL_COLORS).filter(Color::isPrimary)
            .toArray(Color[]::new);

    public static final Color[] SECONDARY_COLORS = Arrays.stream(ACTUAL_COLORS).filter(Color::isSecondary)
            .toArray(Color[]::new);

    public static final Color[] BASIC_COLORS = Arrays.stream(ACTUAL_COLORS).filter(Color::isSecondary)
            .toArray(Color[]::new);

    public static final Color[] TERTIARY_COLORS = Arrays.stream(ACTUAL_COLORS).filter(Color::isTertiary)
            .toArray(Color[]::new);

    public final String nameKey;
    public final int key;
    public final float r;
    public final float g;
    public final float b;

    public final int integerColor;

    @Getter
    public final String serializedName;

    public static final int COLOR_COUNT = COLORS.length;

    public static final int ACTUAL_COLOR_COUNT = ACTUAL_COLORS.length;

    Color(String serializedName, int key, String nameKey, float r, float g, float b, int integerColor) {
        this.key = key;
        this.nameKey = nameKey;
        this.r = r;
        this.g = g;
        this.b = b;
        this.integerColor = integerColor;
        this.serializedName = serializedName;
    }

    public static final Map<Color, Color> TO_NOT_COLOR = new Object2ObjectArrayMap<>();

    public String getColoredDisplayName() {
        String displayName;

        if (nameKey != null && !nameKey.isEmpty()) {
            displayName = Component.translatable(nameKey).getString();
        } else {
            displayName = this.name().toUpperCase().replace('_', ' ');
        }

        boolean isRealColor = this.key >= Color.RED.key && this.key <= Color.PINK.key;

        if (isRealColor) {
            int rgb = this.integerColor;
            String hex = String.format("%06X", rgb & 0xFFFFFF);
            StringBuilder sb = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                sb.append('§').append(c);
            }
            return sb + displayName;
        } else {
            String hex = String.format("%06X", 0x555555 & 0xFFFFFF);
            StringBuilder sb = new StringBuilder("§x");
            for (char c : hex.toCharArray()) {
                sb.append('§').append(c);
            }
            return sb + displayName;
        }
    }

    public static final Map<Color, Color> FROM_NOT_COLOR = new Object2ObjectArrayMap<>();

    static {
        for (int i = 0; i < 12; i++) {
            TO_NOT_COLOR.put(COLORS[i], NOT_COLORS[i]);
            FROM_NOT_COLOR.put(NOT_COLORS[i], COLORS[i]);
        }
    }

    public boolean isPrimary() {
        return this == RED || this == GREEN || this == BLUE;
    }

    public boolean isSecondary() {
        return this == YELLOW || this == CYAN || this == MAGENTA;
    }

    public boolean isBasic() {
        return isPrimary() || isSecondary();
    }

    public boolean isTertiary() {
        return !isBasic() && isRealColor();
    }

    public static List<Color> getColorsWithCategories(Color color) {
        List<Color> colors = new ObjectArrayList<>();
        colors.add(color);
        int key = color.key;

        if (color.isRealColor()) {
            if (key % 4 == 0) {
                colors.add(Color.PRIMARY);
                colors.add(Color.BASIC);
            } else if ((key + 2) % 4 == 0) {
                colors.add(Color.SECONDARY);
                colors.add(Color.BASIC);
            } else {
                colors.add(Color.TERTIARY);
            }
            colors.add(Color.ANY);
            colors.addAll(Arrays.stream(NOT_COLORS).filter(c -> c != TO_NOT_COLOR.get(color)).toList());
        } else {
            if (color.isTypeNotColor()) {
                colors.addAll(Arrays.stream(ACTUAL_COLORS).filter(c -> c != FROM_NOT_COLOR.get(color)).toList());
            } else {
                switch (color) {
                    case ANY -> colors
                            .addAll(Arrays.asList(Color.ACTUAL_COLORS));
                    case PRIMARY -> colors
                            .addAll(Arrays.asList(Color.PRIMARY_COLORS));
                    case SECONDARY -> colors
                            .addAll(Arrays.asList(Color.SECONDARY_COLORS));
                    case BASIC -> colors
                            .addAll(Arrays.asList(Color.BASIC_COLORS));
                    case TERTIARY -> colors
                            .addAll(Arrays.asList(Color.TERTIARY_COLORS));
                }
            }
        }
        return colors;
    }

    public static Color getColorFromKey(int pKey) {
        return COLORS[pKey];
    }

    @Override
    public String toString() {
        return "Color{" + this.name() + '}';
    }

    public static int getRandomColor() {
        return (int) Math.floor(Math.random() * Color.ACTUAL_COLOR_COUNT);
    }

    public static int getRandomColorFromKeys(int[] keys) {
        return keys[(int) Math.floor(Math.random() * (double) keys.length)];
    }

    public boolean isRealColor() {
        return Arrays.asList(ACTUAL_COLORS).contains(this);
    }

    public boolean isTypeNotColor() {
        return Stream.of(NOT_COLORS).anyMatch(c -> c == this);
    }
}

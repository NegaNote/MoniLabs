package net.neganote.monilabs.common.machine.part;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import org.jetbrains.annotations.NotNull;

public enum RenderColor implements StringRepresentable {

    NONE(-1),
    RED(0),
    ORANGE(1),
    YELLOW(2),
    LIME(3),
    GREEN(4),
    TEAL(5),
    CYAN(6),
    AZURE(7),
    BLUE(8),
    INDIGO(9),
    MAGENTA(10),
    PINK(11);

    public final int key;

    RenderColor(int key) {
        this.key = key;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase();
    }

    public static final EnumProperty<RenderColor> COLOR_PROPERTY = EnumProperty.create("prismac_color",
            RenderColor.class);
}

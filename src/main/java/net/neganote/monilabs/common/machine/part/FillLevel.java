package net.neganote.monilabs.common.machine.part;

import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import org.jetbrains.annotations.NotNull;

public enum FillLevel implements StringRepresentable {

    EMPTY_TO_QUARTER(0),
    QUARTER_TO_HALF(1),
    HALF_TO_THREE_QUARTERS(2),
    THREE_QUARTERS_TO_FULL(3);

    public final int key;

    FillLevel(int key) {
        this.key = key;
    }

    @Override
    public @NotNull String getSerializedName() {
        return name().toLowerCase();
    }

    public static final EnumProperty<FillLevel> FILL_PROPERTY = EnumProperty.create("fill_level",
            FillLevel.class);
}

package net.neganote.monilabs.common.machine.multiblock;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

@MethodsReturnNonnullByDefault
public enum Microverse implements StringRepresentable {

    NONE(0, 0, false, false, "microverse.monilabs.type.none"),
    NORMAL(1, 0, true, false, "microverse.monilabs.type.normal"),
    HOSTILE(2, 10, false, false, "microverse.monilabs.type.hostile"),
    SHATTERED(3, 0, false, false, "microverse.monilabs.type.shattered"),
    CORRUPTED(4, 10, true, true, "microverse.monilabs.type.corrupted");

    public static final Microverse[] MICROVERSES = Microverse.values();

    public static final EnumProperty<Microverse> MICROVERSE_TYPE = EnumProperty.create("microverse_type",
            Microverse.class);

    public final int decayRate;
    public final boolean isRepairable;

    public final boolean isHungry;

    public final String langKey;

    public final int key;

    Microverse(int key, int decayRate, boolean isRepairable, boolean isHungry, String langKey) {
        this.decayRate = decayRate;
        this.isRepairable = isRepairable;
        this.isHungry = isHungry;
        this.langKey = langKey;
        this.key = key;
    }

    public static Microverse getMicroverseFromKey(int pKey) {
        return MICROVERSES[pKey];
    }

    @Override
    public String toString() {
        return "Microverse{" + name() + "}";
    }

    @Override
    public String getSerializedName() {
        return name().toLowerCase();
    }
}

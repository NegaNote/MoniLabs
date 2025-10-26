package net.neganote.monilabs.common.machine.multiblock;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import lombok.Getter;

@MethodsReturnNonnullByDefault
public enum Microverse implements StringRepresentable {

    NONE("none", 0, 0, false, false, "microverse.monilabs.type.none"),
    NORMAL("normal", 1, 0, true, false, "microverse.monilabs.type.normal"),
    HOSTILE("hostile", 2, 10, false, false, "microverse.monilabs.type.hostile"),
    SHATTERED("shattered", 3, 0, false, false, "microverse.monilabs.type.shattered"),
    CORRUPTED("corrupted", 4, 10, true, true, "microverse.monilabs.type.corrupted");

    public static final Microverse[] MICROVERSES = Microverse.values();

    public static final EnumProperty<Microverse> MICROVERSE_TYPE = EnumProperty.create("microverse_type",
            Microverse.class);

    public final int decayRate;
    public final boolean isRepairable;

    public final boolean isHungry;

    public final String langKey;

    @Getter
    public final String serializedName;

    public final int key;

    Microverse(String serializedName, int key, int decayRate, boolean isRepairable, boolean isHungry, String langKey) {
        this.decayRate = decayRate;
        this.isRepairable = isRepairable;
        this.isHungry = isHungry;
        this.langKey = langKey;
        this.key = key;
        this.serializedName = serializedName;
    }

    public static Microverse getMicroverseFromKey(int pKey) {
        return MICROVERSES[pKey];
    }

    @Override
    public String toString() {
        return "Microverse{" + name() + "}";
    }
}

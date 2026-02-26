package net.neganote.monilabs.common.machine.multiblock;

import net.minecraft.network.chat.*;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.neganote.monilabs.config.MoniConfig;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public enum Microverse implements StringRepresentable {

    NONE("none", 0, 0, false, false, "microverse.monilabs.type.none"),
    NORMAL("normal", 1, 0, true, false, "microverse.monilabs.type.normal"),
    HOSTILE("hostile", 2, MoniConfig.INSTANCE.values.hostileDecayRate, false, false,
            "microverse.monilabs.type.hostile"),
    SHATTERED("shattered", 3, 0, false, false, "microverse.monilabs.type.shattered"),
    CORRUPTED("corrupted", 4, 10, true, true, "microverse.monilabs.type.corrupted");

    public static final Microverse[] MICROVERSES = values();

    public static final EnumProperty<Microverse> MICROVERSE_TYPE = EnumProperty.create("microverse_type",
            Microverse.class);

    public final int decayRate;
    public final boolean isRepairable;
    public final boolean isHungry;
    public final String langKey;
    public final int key;

    @Getter
    public final String serializedName;

    Microverse(String serializedName,
               int key,
               int decayRate,
               boolean isRepairable,
               boolean isHungry,
               String langKey) {
        this.decayRate = decayRate;
        this.isRepairable = isRepairable;
        this.isHungry = isHungry;
        this.langKey = langKey;
        this.key = key;
        this.serializedName = serializedName;
    }

    public String getDisplayName() {
        if (langKey != null && !langKey.isEmpty()) {
            return Component.translatable(langKey).getString();
        }
        return this.name().toLowerCase().replace('_', ' ');
    }

    public static final Microverse[] ACTUAL_MICROVERSES = Arrays.copyOfRange(MICROVERSES, 0,
            Microverse.CORRUPTED.ordinal() + 1);

    public static Microverse getMicroverseFromKey(int pKey) {
        return MICROVERSES[pKey];
    }

    @Override
    public @NotNull String getSerializedName() {
        return serializedName;
    }
}

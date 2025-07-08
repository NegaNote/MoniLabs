package net.neganote.monilabs.common.machine.multiblock;

public enum Microverse {

    NONE(0, 0, false, false, "microverse.monilabs.type.none"),
    NORMAL(1, 0, true, false, "microverse.monilabs.type.normal"),
    HOSTILE(2, 20, false, false, "microverse.monilabs.type.hostile"),
    SHATTERED(3, 0, false, false, "microverse.monilabs.type.shattered"),
    CORRUPTED(4, 10, true, true, "microverse.monilabs.type.corrupted");

    public static final Microverse[] MICROVERSES = Microverse.values();

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

    /*
     * @Override
     * public String toString() {
     * return "Color{" +
     * "nameKey='" + nameKey + '\'' +
     * '}';
     * }
     */
}

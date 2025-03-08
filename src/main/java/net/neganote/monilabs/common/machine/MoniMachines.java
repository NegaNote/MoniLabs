package net.neganote.monilabs.common.machine;

import net.neganote.monilabs.MoniLabsMod;

import static net.neganote.monilabs.MoniLabsMod.REGISTRATE;

@SuppressWarnings("unused")
public class MoniMachines {

    static {
        REGISTRATE.creativeModeTab(() -> MoniLabsMod.MONI_CREATIVE_TAB);
    }

    public static void init() {}
}

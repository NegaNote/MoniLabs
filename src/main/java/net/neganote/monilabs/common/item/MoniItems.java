package net.neganote.monilabs.common.item;


import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.neganote.monilabs.MoniLabsMod;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static net.neganote.monilabs.MoniLabsMod.REGISTRATE;

public class MoniItems {
    static {
        REGISTRATE.creativeModeTab(() -> MoniLabsMod.MONI_CREATIVE_TAB);
    }

    @SuppressWarnings("unused")
    public static ItemEntry<OmniToolItem> OMNITOOL = REGISTRATE.item("omnitool", OmniToolItem::create)
            .lang("Omnitool")

            .properties(p -> p.stacksTo(1).durability(0))
            .onRegister(attach(ElectricStats.createElectricItem(25_600_000L, GTValues.IV), new PrecisionBreakBehavior()))
            .register();

    public static void init() {}
}

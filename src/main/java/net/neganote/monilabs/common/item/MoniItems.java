package net.neganote.monilabs.common.item;


import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.data.tag.TagUtil;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.item.component.ElectricStats;
import com.gregtechceu.gtceu.api.item.tool.GTToolType;
import com.gregtechceu.gtceu.common.data.GTSoundEntries;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neganote.monilabs.MoniLabsMod;

import static com.gregtechceu.gtceu.common.data.GTItems.attach;
import static net.neganote.monilabs.MoniLabsMod.REGISTRATE;

public class MoniItems {
    static {
        REGISTRATE.creativeModeTab(() -> MoniLabsMod.MONI_CREATIVE_TAB);
    }

    public static ItemEntry<ComponentItem> OMNITOOL = REGISTRATE.item("Multitool", ComponentItem::create)
            .lang("Multitool")
            .properties(p -> p.stacksTo(1).durability(0))
            .onRegister(attach(ElectricStats.createElectricItem(25_600_000L, GTValues.IV)))
            .register();

    public static void init() {}
}

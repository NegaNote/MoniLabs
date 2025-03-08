package net.neganote.monilabs.common.item;

import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;
import com.tterrag.registrate.util.nullness.NonNullConsumer;
import net.neganote.monilabs.MoniLabsMod;
import static net.neganote.monilabs.MoniLabsMod.REGISTRATE;

public class MoniItems {
    static {
        REGISTRATE.creativeModeTab(() -> MoniLabsMod.MONI_CREATIVE_TAB);
    }

    public static void init() {}

    // Copied from GTItems
    @SuppressWarnings("unused")
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}

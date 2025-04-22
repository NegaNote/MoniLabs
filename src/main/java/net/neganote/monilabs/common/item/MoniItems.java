package net.neganote.monilabs.common.item;

import com.gregtechceu.gtceu.api.item.IComponentItem;
import com.gregtechceu.gtceu.api.item.component.IItemComponent;

import net.minecraft.world.item.Item;
import net.neganote.monilabs.MoniLabs;

import com.tterrag.registrate.util.entry.ItemEntry;
import com.tterrag.registrate.util.nullness.NonNullConsumer;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

public class MoniItems {

    static {
        REGISTRATE.creativeModeTab(() -> MoniLabs.MONI_CREATIVE_TAB);
    }

    public static ItemEntry<Item> MOTE_OF_OMNIUM = REGISTRATE
            .item("mote_of_omnium", Item::new)
            .lang("Mote of Omnium")
            .model((ctx, prov) -> {
                Item item = ctx.getEntry();
                prov.generated(() -> item)
                        .texture("layer0", MoniLabs.id("item/mote_of_omnium"))
                        .texture("layer1", MoniLabs.id("item/mote_of_omnium_overlay"));
            })
            .register();

    public static void init() {}

    // Copied from GTItems
    @SuppressWarnings("unused")
    public static <T extends IComponentItem> NonNullConsumer<T> attach(IItemComponent... components) {
        return item -> item.attachComponents(components);
    }
}

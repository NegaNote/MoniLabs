package net.neganote.monilabs.data.tags;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.MoniLabs;

public class MoniTags {

    public static TagKey<Item> OMNIC_SYNTHESIZER_INPUT = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(),
            MoniLabs.id("omnic_synthesizer_input"));
}

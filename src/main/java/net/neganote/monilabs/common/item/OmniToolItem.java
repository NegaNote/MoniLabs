package net.neganote.monilabs.common.item;

import com.gregtechceu.gtceu.api.item.ComponentItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public class OmniToolItem extends ComponentItem {
    protected OmniToolItem(Properties properties) {
        super(properties);
    }

    public static OmniToolItem create(Item.Properties properties) {
        return new OmniToolItem(properties);
    }

    // Should make it so it can harvest anything
    @Override
    public boolean isCorrectToolForDrops(ItemStack stack, BlockState state) {
        return true;
    }

    // Effectively instamines
    @Override
    @SuppressWarnings("All") // Otherwise it complains about annotated parameters, which I'm ignoring
    public float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return 1000.0F;
    }
}

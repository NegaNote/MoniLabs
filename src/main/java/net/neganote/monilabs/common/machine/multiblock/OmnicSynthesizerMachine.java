package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.RecipeHelper;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.modifier.ModifierFunction;
import com.gregtechceu.gtceu.api.recipe.modifier.RecipeModifier;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.world.item.Item;
import net.neganote.monilabs.config.MoniConfig;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class OmnicSynthesizerMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            OmnicSynthesizerMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    private List<Item> diversityList = new ArrayList<>();

    @Persisted
    @DescSynced
    public int diversityPoints = 0;

    public OmnicSynthesizerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public GTRecipe fullModifyRecipe(GTRecipe recipe) {
        return super.fullModifyRecipe(recipe);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    public static RecipeModifier recipeModifier() {
        return (metaMachine, gtRecipe) -> {
            if (metaMachine instanceof OmnicSynthesizerMachine omnic) {
                Item item = RecipeHelper.getInputItems(gtRecipe).get(0).getItem();
                double multiplier = 0.0;
                if (omnic.diversityList.contains(item)) {
                    int index = omnic.diversityList.indexOf(item);
                    omnic.diversityPoints += (int) Math
                            .floor(Math.pow(index, MoniConfig.INSTANCE.values.omnicSynthesizerExponent));
                    multiplier = (double) omnic.diversityPoints / 100;
                    omnic.diversityPoints = omnic.diversityPoints % 100;
                    Item temp = omnic.diversityList.remove(index);
                    omnic.diversityList.add(0, temp);
                } else {
                    omnic.diversityList.add(0, item);
                }
                return ModifierFunction.builder()
                        .outputModifier(ContentModifier.multiplier(multiplier))
                        .build();
            }
            return ModifierFunction.NULL;
        };
    }
}

package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;

import net.neganote.monilabs.common.machine.multiblock.Microverse;

import java.util.List;

public class MapMicroverseIngredient extends AbstractMapIngredient {

    public final Microverse microverse;

    public MapMicroverseIngredient(Microverse microverse) {
        this.microverse = microverse;
    }

    @Override
    protected int hash() {
        return microverse.key;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapMicroverseIngredient other)) {
            return false;
        }
        return other.microverse == microverse;
    }

    public static List<AbstractMapIngredient> from(Microverse ingredient) {
        return List.of(new MapMicroverseIngredient(ingredient));
    }
}

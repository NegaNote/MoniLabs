package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;

import net.neganote.monilabs.common.machine.multiblock.Color;

import java.util.List;

public class MapColorIngredient extends AbstractMapIngredient {

    public final Color color;

    public MapColorIngredient(Color color) {
        this.color = color;
    }

    @Override
    protected int hash() {
        return color.key;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof MapColorIngredient other)) {
            return false;
        }
        return color == other.color;
    }

    public static List<AbstractMapIngredient> from(ChromaIngredient ingredient) {
        Color color = ingredient.color();
        return List.of(new MapColorIngredient(color));
    }

    @Override
    public String toString() {
        return "MapColorIngredient{" +
                "color=" + color + "}";
    }
}

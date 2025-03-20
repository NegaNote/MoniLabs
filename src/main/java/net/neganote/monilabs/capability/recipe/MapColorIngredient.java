package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;

import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

public class MapColorIngredient extends AbstractMapIngredient {

    private final Color color;

    public MapColorIngredient(Color color) {
        this.color = color;
    }
    
    @Override
    protected int hash() {
        return color.key;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) {
            MapColorIngredient other = (MapColorIngredient) obj;
            return this.color == other.color;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "MapColorIngredient{" +
            "color=" + color + "}";
    }

}

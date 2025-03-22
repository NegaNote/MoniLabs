package net.neganote.monilabs.capability.recipe;

import java.util.function.Predicate;

import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

public record ChromaIngredient(Color color) implements Predicate<Color> {
    public static ChromaIngredient of(Color color) {
        return new ChromaIngredient(color);
    }

    @Override
    public boolean test(Color color) {
        return this.color == color;
    }

    @Override
    public String toString() {
        return "ChromaIngredient{" +
                "color=" + color + "}";
    }
}

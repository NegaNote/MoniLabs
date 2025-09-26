package net.neganote.monilabs.capability.recipe;

import net.neganote.monilabs.common.machine.multiblock.Color;

import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public record ChromaIngredient(Color color) implements Predicate<Color> {

    public static ChromaIngredient of(Color color) {
        return new ChromaIngredient(color);
    }

    @Override
    public boolean test(Color color) {
        return this.color == color;
    }

    @Override
    public @NotNull String toString() {
        return "ChromaIngredient{" +
                "color=" + color + "}";
    }
}

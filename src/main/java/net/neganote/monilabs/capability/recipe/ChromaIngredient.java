package net.neganote.monilabs.capability.recipe;

import java.util.function.Predicate;
import java.util.stream.Stream;

import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

public class ChromaIngredient implements Predicate<Color> {

    public final Color color;

    public ChromaIngredient(Color color) {
        this.color = color;
    }

    //TODO share instances to reduce memory usage/improve performance
    public static ChromaIngredient of(Color color) {
        return new ChromaIngredient(color);
    }
    @Override
    public boolean test(Color color) {
        return this.color == color;
        /*if (color.key >= Color.ACTUAL_COLOR_COUNT) {
            int key = this.color.key;
            if (key % 4 == 0) {
                if (color == Color.PRIMARY)
                    return true;
                if (color == Color.BASIC)
                    return true;
            } else if ((key + 2) % 4 == 0) {
                if (color == Color.SECONDARY)
                    return true;
                if (color == Color.BASIC)
                    return true;
            } else {
                if (color == Color.TERTIARY)
                    return true;
            }
            if (color == Color.ANY)
                return true;
        } else {
            return this.color == color;
        }

        return false;*/
    }

    @Override
    public String toString() {
        return "ChromaIngredient{" +
            "color=" + color + "}";
    }
}

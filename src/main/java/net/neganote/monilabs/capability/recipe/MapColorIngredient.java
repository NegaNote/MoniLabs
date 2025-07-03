package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.AbstractMapIngredient;

import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

import java.util.List;
import java.util.stream.Stream;

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
        if (super.equals(obj)) {
            MapColorIngredient other = (MapColorIngredient) obj;
            return this.color == other.color;
        } else {
            return false;
        }
    }

    public static List<AbstractMapIngredient> from(ChromaIngredient ingredient) {
        List<AbstractMapIngredient> ingredients = new ObjectArrayList<>();
        Color ingredientColor = ingredient.color();
        ingredients.add(new MapColorIngredient(ingredientColor));
        int key = ingredientColor.key;

        if (key < Color.ACTUAL_COLOR_COUNT) {
            if (key % 4 == 0) {
                ingredients.add(new MapColorIngredient(Color.PRIMARY));
                ingredients.add(new MapColorIngredient(Color.BASIC));
            } else if ((key + 2) % 4 == 0) {
                ingredients.add(new MapColorIngredient(Color.SECONDARY));
                ingredients.add(new MapColorIngredient(Color.BASIC));
            } else {
                ingredients.add(new MapColorIngredient(Color.TERTIARY));
            }
            ingredients.add(new MapColorIngredient(Color.ANY));
            switch (ingredientColor) {
                case RED -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_RED)
                        .map(MapColorIngredient::new).toList());
                case ORANGE -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_ORANGE)
                        .map(MapColorIngredient::new).toList());
                case YELLOW -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_YELLOW)
                        .map(MapColorIngredient::new).toList());
                case LIME -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_LIME)
                        .map(MapColorIngredient::new).toList());
                case GREEN -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_GREEN)
                        .map(MapColorIngredient::new).toList());
                case TEAL -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_TEAL)
                        .map(MapColorIngredient::new).toList());
                case CYAN -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_CYAN)
                        .map(MapColorIngredient::new).toList());
                case AZURE -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_AZURE)
                        .map(MapColorIngredient::new).toList());
                case BLUE -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_BLUE)
                        .map(MapColorIngredient::new).toList());
                case INDIGO -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_INDIGO)
                        .map(MapColorIngredient::new).toList());
                case MAGENTA -> ingredients
                        .addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_MAGENTA)
                                .map(MapColorIngredient::new).toList());
                case PINK -> ingredients.addAll(Stream.of(Color.NOT_COLORS).filter(c -> c != Color.NOT_PINK)
                        .map(MapColorIngredient::new).toList());

            }
        } else {
            switch (ingredientColor) {
                case NOT_RED -> ingredients.addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.RED)
                        .map(MapColorIngredient::new).toList());
                case NOT_ORANGE -> ingredients
                        .addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.ORANGE)
                                .map(MapColorIngredient::new).toList());
                case NOT_YELLOW -> ingredients
                        .addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.YELLOW)
                                .map(MapColorIngredient::new).toList());
                case NOT_LIME -> ingredients.addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.LIME)
                        .map(MapColorIngredient::new).toList());
                case NOT_GREEN -> ingredients
                        .addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.GREEN)
                                .map(MapColorIngredient::new).toList());
                case NOT_TEAL -> ingredients.addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.TEAL)
                        .map(MapColorIngredient::new).toList());
                case NOT_CYAN -> ingredients.addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.CYAN)
                        .map(MapColorIngredient::new).toList());
                case NOT_AZURE -> ingredients
                        .addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.AZURE)
                                .map(MapColorIngredient::new).toList());
                case NOT_BLUE -> ingredients.addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.BLUE)
                        .map(MapColorIngredient::new).toList());
                case NOT_INDIGO -> ingredients
                        .addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.INDIGO)
                                .map(MapColorIngredient::new).toList());
                case NOT_MAGENTA -> ingredients
                        .addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.MAGENTA)
                                .map(MapColorIngredient::new).toList());
                case NOT_PINK -> ingredients.addAll(Stream.of(Color.ACTUAL_COLORS).filter(c -> c != Color.PINK)
                        .map(MapColorIngredient::new).toList());
                case ANY -> ingredients
                        .addAll(Stream.of(Color.ACTUAL_COLORS).map(MapColorIngredient::new).toList());
                case PRIMARY -> ingredients
                        .addAll(Stream.of(Color.PRIMARY_COLORS).map(MapColorIngredient::new).toList());
                case SECONDARY -> ingredients
                        .addAll(Stream.of(Color.SECONDARY_COLORS).map(MapColorIngredient::new).toList());
                case BASIC -> ingredients
                        .addAll(Stream.of(Color.BASIC_COLORS).map(MapColorIngredient::new).toList());
                case TERTIARY -> ingredients
                        .addAll(Stream.of(Color.TERTIARY_COLORS).map(MapColorIngredient::new).toList());
            }
        }
        return ingredients;
    }

    @Override
    public String toString() {
        return "MapColorIngredient{" +
                "color=" + color + "}";
    }
}

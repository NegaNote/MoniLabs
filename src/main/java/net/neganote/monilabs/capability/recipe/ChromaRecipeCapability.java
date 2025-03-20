package net.neganote.monilabs.capability.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;
import com.mojang.serialization.Codec;

import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

public class ChromaRecipeCapability extends RecipeCapability<Color> {
    public static final ChromaRecipeCapability CAP = new ChromaRecipeCapability();

    protected ChromaRecipeCapability() {
        super("Chroma", 0xFF00FFFF, true, 10, SerializerColor.INSTANCE);
    }

    @Override
    public boolean isRecipeSearchFilter() {
        return true;
    }

    @Override
    public List<AbstractMapIngredient> convertToMapIngredient(Object ingredient) {
        if (ingredient instanceof Color ingredientColor) {
            //TODO add the generic/special case "colors" ie primary colors
            return switch (ingredientColor) {
                case RED -> List.of(new MapColorIngredient(Color.RED));
                case ORANGE -> List.of(new MapColorIngredient(Color.ORANGE));
                case YELLOW -> List.of(new MapColorIngredient(Color.YELLOW));
                case LIME -> List.of(new MapColorIngredient(Color.LIME));
                case GREEN -> List.of(new MapColorIngredient(Color.GREEN));
                case TEAL -> List.of(new MapColorIngredient(Color.TEAL));
                case CYAN -> List.of(new MapColorIngredient(Color.CYAN));
                case AZURE -> List.of(new MapColorIngredient(Color.AZURE));
                case BLUE -> List.of(new MapColorIngredient(Color.BLUE));
                case INDIGO -> List.of(new MapColorIngredient(Color.INDIGO));
                case MAGENTA -> List.of(new MapColorIngredient(Color.MAGENTA));
                case PINK -> List.of(new MapColorIngredient(Color.PINK));
                case PRIMARY -> new ArrayList<>(Stream.of(Color.RED, Color.GREEN, Color.BLUE).map(MapColorIngredient::new).toList());
                case SECONDARY -> new ArrayList<>(Stream.of(Color.YELLOW, Color.CYAN, Color.MAGENTA).map(MapColorIngredient::new).toList());
                case BASIC -> new ArrayList<>(Stream.of(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA).map(MapColorIngredient::new).toList());
                case TERTIARY -> new ArrayList<>(Stream.of(Color.ORANGE, Color.LIME, Color.TEAL, Color.AZURE, Color.INDIGO, Color.PINK).map(MapColorIngredient::new).toList());
                case ANY -> new ArrayList<>(Stream.of(Color.COLORS).map(MapColorIngredient::new).toList());
            };
        } else {
            return super.convertToMapIngredient(ingredient);
        }
    }

    private static class SerializerColor implements IContentSerializer<Color> {
        public static SerializerColor INSTANCE = new SerializerColor();

        public static final Codec<Color> CODEC = Codec.INT.xmap(Color::getColorFromKey, color -> color.key);

        private SerializerColor() {}
        @Override
        public Color fromJson(JsonElement json) {
            return Color.getColorFromKey(json.getAsInt());
        }

        @Override
        public JsonElement toJson(Color content) {
            return new JsonPrimitive(content.key);
        }

        @Override
        public Color of(Object o) {
            if (o instanceof Color color) {
                return color;
            } else {
                return Color.RED;
            }
        }

        @Override
        public Color defaultValue() {
            return Color.RED;
        }

        @Override
        public Codec<Color> codec() {
            return CODEC;
        }
    }
}

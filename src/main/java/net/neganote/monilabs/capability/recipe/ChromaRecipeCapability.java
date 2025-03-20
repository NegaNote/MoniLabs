package net.neganote.monilabs.capability.recipe;

import java.util.ArrayList;
import java.util.List;

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
            List<AbstractMapIngredient> list = new ArrayList<>();
            list.add(new MapColorIngredient(ingredientColor));
            return list;
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

package net.neganote.monilabs.capability.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;
import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
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
            List<AbstractMapIngredient> ingredients = new ObjectArrayList<>();
            ingredients.add(new MapColorIngredient(ingredientColor));
            int key = ingredientColor.key;
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
            return ingredients;
        } else {
            return super.convertToMapIngredient(ingredient);
        }
    }

    @Override
    public Color copyInner(Color content) {
        return content;
    }

    @Override
    public List<Object> compressIngredients(Collection<Object> ingredients) {
        // this doesn't feel like compressing, but I guess if it works it works
        // ¯\_(ツ)_/¯

        List<Object> list = new ArrayList<>();

        for (Object object : ingredients) {
            if (object instanceof Color ingredientColor) {
                list.add(Color.getColorFromKey(ingredientColor.key));
            }
        }

        Set<Object> compressed = new ReferenceOpenHashSet<>();
        for (Object ingredient : ingredients) {
            if (ingredient instanceof Color ingredientColor) {

                list.add(ingredientColor);
                int key = ingredientColor.key;
                if (key % 4 == 0) {
                    list.add(Color.PRIMARY);
                    list.add(Color.BASIC);
                } else if ((key + 2) % 4 == 0) {
                    list.add(Color.SECONDARY);
                    list.add(Color.BASIC);
                } else {
                    list.add(Color.TERTIARY);
                }
                list.add(Color.ANY);
            } else {
                compressed.addAll(super.compressIngredients(list));
            }
        }
        return new ArrayList<>(compressed);
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

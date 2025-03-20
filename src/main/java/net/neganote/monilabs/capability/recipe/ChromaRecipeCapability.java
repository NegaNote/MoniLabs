package net.neganote.monilabs.capability.recipe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.content.ContentModifier;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;
import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceOpenHashSet;
import net.neganote.monilabs.capability.recipe.MapColorIngredient;
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
        if (ingredient instanceof Color color) {
            List<AbstractMapIngredient> ingredients = new ObjectArrayList<>();
            ingredients.add(new MapColorIngredient(color));
            int key = color.key;
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
        Set<Object> compressed = new ReferenceOpenHashSet<>();
        for (Object ingredient : ingredients) {
            if (ingredient instanceof Color color) {

                ingredients.add(color);
                int key = color.key;
                if (key % 4 == 0) {
                    ingredients.add(Color.PRIMARY);
                    ingredients.add(Color.BASIC);
                } else if ((key + 2) % 4 == 0) {
                    ingredients.add(Color.SECONDARY);
                    ingredients.add(Color.BASIC);
                } else {
                    ingredients.add(Color.TERTIARY);
                }
                ingredients.add(Color.ANY);
            } else {
                compressed.addAll(super.compressIngredients(ingredients));
            }
        }
        List<Object> tmp = new ArrayList<>();
        tmp.addAll(compressed);
        return tmp;
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

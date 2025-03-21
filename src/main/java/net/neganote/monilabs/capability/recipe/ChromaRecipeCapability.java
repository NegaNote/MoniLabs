package net.neganote.monilabs.capability.recipe;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import com.mojang.serialization.Codec;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;
import org.apache.commons.lang3.mutable.MutableInt;

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
    public void addXEIInfo(WidgetGroup group, int xOffset, GTRecipe recipe, List<Content> contents, boolean perTick, boolean isInput, MutableInt yOffset) {
        if (contents.size() != 1) {
            group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.mistake_input_colors")));
        } else {
            Color inputColor = (Color) contents.get(0).getContent();
            if (inputColor.isRealColor()) {
                group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.required_color",
                        LocalizationUtils.format(inputColor.nameKey))));
            } else {
                if (inputColor == Color.PRIMARY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.primary_input")));
                } else if (inputColor == Color.SECONDARY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.secondary_input")));
                } else if (inputColor == Color.BASIC) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.basic_input")));
                } else if (inputColor == Color.TERTIARY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.tertiary_input")));
                } else if (inputColor == Color.ANY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.any_input_color")));
                }
            }

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

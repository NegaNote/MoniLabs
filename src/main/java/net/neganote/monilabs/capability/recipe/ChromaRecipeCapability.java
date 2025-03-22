package net.neganote.monilabs.capability.recipe;

import java.util.List;

import org.apache.commons.lang3.mutable.MutableInt;

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

public class ChromaRecipeCapability extends RecipeCapability<ChromaIngredient> {
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
        List<AbstractMapIngredient> ingredients = new ObjectArrayList<>();
        if (ingredient instanceof ChromaIngredient chroma) {
            ingredients.add(new MapColorIngredient(chroma.color()));
            return ingredients;
        } else {
            if (ingredient instanceof Color ingredientColor) {

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
                }
                return ingredients;
            }

        }
        return super.convertToMapIngredient(ingredient);
    }

    @Override
    public ChromaIngredient copyInner(ChromaIngredient content) {
        return content;
    }

    @Override
    public void addXEIInfo(WidgetGroup group, int xOffset, GTRecipe recipe, List<Content> contents, boolean perTick, boolean isInput, MutableInt yOffset) {
        if (contents.size() != 1) {
            group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10), LocalizationUtils.format("monilabs.recipe.mistake_input_colors")));
        } else {
            Color inputColor = ((ChromaIngredient) contents.get(0).getContent()).color();
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

    private static class SerializerColor implements IContentSerializer<ChromaIngredient> {
        public static SerializerColor INSTANCE = new SerializerColor();

        public static final Codec<ChromaIngredient> CODEC = Codec.INT.xmap(i -> ChromaIngredient.of(Color.getColorFromKey(i)), color -> color.color().key);

        private SerializerColor() {}
        @Override
        public ChromaIngredient fromJson(JsonElement json) {
            return ChromaIngredient.of(Color.getColorFromKey(json.getAsInt()));
        }

        @Override
        public JsonElement toJson(ChromaIngredient content) {
            return new JsonPrimitive(content.color().key);
        }

        @Override
        public ChromaIngredient of(Object o) {
            if (o instanceof Color color) {
                return ChromaIngredient.of(color);
            } else if (o instanceof ChromaIngredient chroma) {
                return chroma;
            }
            return ChromaIngredient.of(Color.RED);
        }

        @Override
        public ChromaIngredient defaultValue() {
            return ChromaIngredient.of(Color.RED);
        }

        @Override
        public Codec<ChromaIngredient> codec() {
            return CODEC;
        }
    }
}

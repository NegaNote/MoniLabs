package net.neganote.monilabs.capability.recipe;

import com.gregtechceu.gtceu.api.capability.recipe.RecipeCapability;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.api.recipe.content.Content;
import com.gregtechceu.gtceu.api.recipe.content.IContentSerializer;
import com.gregtechceu.gtceu.api.recipe.lookup.AbstractMapIngredient;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.utils.LocalizationUtils;

import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.stream.Stream;

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
    public void addXEIInfo(WidgetGroup group, int xOffset, GTRecipe recipe, List<Content> contents, boolean perTick,
                           boolean isInput, MutableInt yOffset) {
        if (contents.size() != 1) {
            group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                    LocalizationUtils.format("monilabs.recipe.mistake_input_colors")));
        } else {
            Color inputColor = ((ChromaIngredient) contents.get(0).getContent()).color();
            if (inputColor.isRealColor()) {
                group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                        LocalizationUtils.format("monilabs.recipe.required_color",
                                LocalizationUtils.format(inputColor.nameKey))));
            } else {
                if (inputColor == Color.PRIMARY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                            LocalizationUtils.format("monilabs.recipe.primary_input")));
                } else if (inputColor == Color.SECONDARY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                            LocalizationUtils.format("monilabs.recipe.secondary_input")));
                } else if (inputColor == Color.BASIC) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                            LocalizationUtils.format("monilabs.recipe.basic_input")));
                } else if (inputColor == Color.TERTIARY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                            LocalizationUtils.format("monilabs.recipe.tertiary_input")));
                } else if (inputColor == Color.ANY) {
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                            LocalizationUtils.format("monilabs.recipe.any_input_color")));
                } else if (inputColor.isTypeNotColor()) {
                    String formatted = "";
                    switch (inputColor) {
                        case NOT_RED -> formatted = LocalizationUtils.format(Color.RED.nameKey);
                        case NOT_ORANGE -> formatted = LocalizationUtils.format(Color.ORANGE.nameKey);
                        case NOT_YELLOW -> formatted = LocalizationUtils.format(Color.YELLOW.nameKey);
                        case NOT_LIME -> formatted = LocalizationUtils.format(Color.LIME.nameKey);
                        case NOT_GREEN -> formatted = LocalizationUtils.format(Color.GREEN.nameKey);
                        case NOT_TEAL -> formatted = LocalizationUtils.format(Color.TEAL.nameKey);
                        case NOT_CYAN -> formatted = LocalizationUtils.format(Color.CYAN.nameKey);
                        case NOT_AZURE -> formatted = LocalizationUtils.format(Color.AZURE.nameKey);
                        case NOT_BLUE -> formatted = LocalizationUtils.format(Color.BLUE.nameKey);
                        case NOT_INDIGO -> formatted = LocalizationUtils.format(Color.INDIGO.nameKey);
                        case NOT_MAGENTA -> formatted = LocalizationUtils.format(Color.MAGENTA.nameKey);
                        case NOT_PINK -> formatted = LocalizationUtils.format(Color.PINK.nameKey);
                    }
                    group.addWidget(new LabelWidget(xOffset + 3, yOffset.addAndGet(10),
                            LocalizationUtils.format("monilabs.recipe.input_color_not",
                                    formatted)));
                }
            }

        }
    }

    private static class SerializerColor implements IContentSerializer<ChromaIngredient> {

        public static SerializerColor INSTANCE = new SerializerColor();

        public static final Codec<ChromaIngredient> CODEC = Codec.INT
                .xmap(i -> ChromaIngredient.of(Color.getColorFromKey(i)), color -> color.color().key);

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

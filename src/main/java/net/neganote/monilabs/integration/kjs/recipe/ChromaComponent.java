package net.neganote.monilabs.integration.kjs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import net.neganote.monilabs.capability.recipe.ChromaIngredient;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.Color;

public class ChromaComponent implements RecipeComponent<Color> {
    @Override
    public Class<?> componentClass() {
        return ChromaIngredient.class;
    }

    @Override
    public JsonElement write(RecipeJS recipeJS, Color color) {
        return new JsonPrimitive(color.key);
    }

    @Override
    public Color read(RecipeJS recipeJS, Object o) {
        return ((ChromaIngredient) o).color;
    }
}

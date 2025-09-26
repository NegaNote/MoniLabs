package net.neganote.monilabs.integration.kjs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import net.neganote.monilabs.common.machine.multiblock.Microverse;

public class MicroverseComponent implements RecipeComponent<Microverse> {
    @Override
    public Class<?> componentClass() {
        return Microverse.class;
    }

    @Override
    public JsonElement write(RecipeJS recipeJS, Microverse microverse) {
        return new JsonPrimitive(microverse.key);
    }

    @Override
    public Microverse read(RecipeJS recipeJS, Object o) {
        return (Microverse) o;
    }
}

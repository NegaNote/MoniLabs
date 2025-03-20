package net.neganote.monilabs.integration.kjs.recipe;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

public class ChromaComponent implements RecipeComponent<PrismaticCrucibleMachine.Color> {
    @Override
    public Class<?> componentClass() {
        return PrismaticCrucibleMachine.Color.class;
    }

    @Override
    public JsonElement write(RecipeJS recipeJS, PrismaticCrucibleMachine.Color color) {
        return new JsonPrimitive(color.key);
    }

    @Override
    public PrismaticCrucibleMachine.Color read(RecipeJS recipeJS, Object o) {
        return (PrismaticCrucibleMachine.Color) o;
    }
}

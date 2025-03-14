package net.neganote.monilabs.integration.kjs;

import com.gregtechceu.gtceu.api.registry.GTRegistries;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.recipe.schema.RegisterRecipeSchemasEvent;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.util.ClassFilter;
import net.neganote.monilabs.integration.kjs.recipe.MoniRecipeSchema;

public class MoniKubeJSPlugin extends KubeJSPlugin {

    @Override
    public void initStartup() {
        super.initStartup();
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void registerClasses(ScriptType type, ClassFilter filter) {
        super.registerClasses(type, filter);
        // allow user to access all monilabs classes by importing them.
        filter.allow("net.neganote.monilabs");
    }

    @Override
    public void registerRecipeSchemas(RegisterRecipeSchemasEvent event) {
        for (var entry : GTRegistries.RECIPE_TYPES.entries()) {
            event.register(entry.getKey(), MoniRecipeSchema.SCHEMA);
        }
    }

    /*@Override
    public void registerBindings(BindingsEvent event) {
        super.registerBindings(event);
        event.add("MysteriousString", MysteriousClass.class);
    }*/
}


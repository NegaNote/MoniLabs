package net.neganote.monilabs.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class MoniLangHandler {
    public static void init(RegistrateLangProvider provider) {
        provider.add("monilabs.prismatic.current_color", "Currently has a color of %s");
        provider.add("monilabs.prismatic.color_name.red", "Red");
        provider.add("monilabs.prismatic.color_name.orange", "Orange");
        provider.add("monilabs.prismatic.color_name.yellow", "Yellow");
        provider.add("monilabs.prismatic.color_name.lime", "Lime");
        provider.add("monilabs.prismatic.color_name.green", "Green");
        provider.add("monilabs.prismatic.color_name.turquoise", "Turquoise");
        provider.add("monilabs.prismatic.color_name.cyan", "Cyan");
        provider.add("monilabs.prismatic.color_name.azure", "Azure");
        provider.add("monilabs.prismatic.color_name.blue", "Blue");
        provider.add("monilabs.prismatic.color_name.indigo", "Indigo");
        provider.add("monilabs.prismatic.color_name.magenta", "Magenta");
        provider.add("monilabs.prismatic.color_name.pink", "Pink");

        provider.add("monilabs.prismatic.current_mode", "Changes colors %s");
        provider.add("monilabs.prismatic.mode_name.deterministic", "deterministically");
        provider.add("monilabs.prismatic.mode_name.random", "randomly");

        provider.add("monilabs.recipe.color", "Requires machine to be %s");
    }
}

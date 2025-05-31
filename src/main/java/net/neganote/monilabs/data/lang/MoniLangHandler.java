package net.neganote.monilabs.data.lang;

import com.tterrag.registrate.providers.RegistrateLangProvider;

public class MoniLangHandler {

    public static void init(RegistrateLangProvider provider) {
        provider.add("monilabs.prismatic.current_color", "Current color: %s");
        provider.add("monilabs.prismatic.color_name.red", "§4Red§r");
        provider.add("monilabs.prismatic.color_name.orange", "§6Orange§r");
        provider.add("monilabs.prismatic.color_name.yellow", "§eYellow§r");
        provider.add("monilabs.prismatic.color_name.lime", "§aLime§r");
        provider.add("monilabs.prismatic.color_name.green", "§2Green§r");
        provider.add("monilabs.prismatic.color_name.teal", "§3Teal§r");
        provider.add("monilabs.prismatic.color_name.cyan", "§bCyan§r");
        provider.add("monilabs.prismatic.color_name.azure", "§9Azure§r");
        provider.add("monilabs.prismatic.color_name.blue", "§1Blue§r");
        provider.add("monilabs.prismatic.color_name.indigo", "§5Indigo§r");
        provider.add("monilabs.prismatic.color_name.magenta", "§dMagenta§r");
        provider.add("monilabs.prismatic.color_name.pink", "§cPink§r");

        provider.add("monilabs.omnic.current_diversity_points", "Current diversity points: %s");

        provider.add("monilabs.recipe.required_color", "Required Initial Color:\n%s");
        provider.add("monilabs.recipe.result_color", "Resulting Color: \n%s");
        provider.add("monilabs.recipe.result_color_relative", "Resulting Color Increment: \n%s");
        provider.add("monilabs.recipe.color_list_random_start", "Possible Resulting Colors: \n");
        provider.add("monilabs.recipe.color_list_random_start_relative", "Possible Color Increments: \n");
        provider.add("monilabs.recipe.color_list_separator", ", ");
        provider.add("monilabs.recipe.fully_random_color",
                "Resulting Color State: \n§5R§dA§4N§cD§eO§aM §bC§3O§7L§1O§5R§r!");
        provider.add("monilabs.recipe.accepted_colors", "Accepted Initial Colors: ");
        provider.add("monilabs.recipe.primary_input", "Primary");
        provider.add("monilabs.recipe.secondary_input", "Secondary");
        provider.add("monilabs.recipe.basic_input", "Primary | Secondary");
        provider.add("monilabs.recipe.tertiary_input", "Tertiary");
        provider.add("monilabs.recipe.any_input_color", "ANY");
        provider.add("monilabs.recipe.input_color_not", "NOT %s");

        provider.add("monilabs.recipe.mistake_input_colors", "You made a mistake defining this recipe's input colors.");
        provider.add("monilabs.recipe.mistake_output_colors",
                "You made a mistake defining this recipe's output colors.");

        provider.add("monilabs.recipe.diversity_info",
                "Output is variable based on\nthe material diversity\nbefore each use of\nthe synthesizer.");

        provider.add("monilabs.recipe.infinite_power", "Imbues your factory with\nInfinite Power");
        provider.add("monilabs.recipe.infinite_research", "Imbues your factory with\nthe Wisdom of the Universe");

        provider.add("gtceu.omnic_synthesis", "Omnic Synthesis");
        provider.add("gtceu.omnidimensional_power_singularity", "Omnidimensional Power Singularity");
        provider.add("gtceu.omniscience_research_beacon", "Omniscience Research Beacon");

        provider.add("monilabs.tooltip.prismatic.rainbow", "the rainbow");
        provider.add("monilabs.tooltip.prismatic.0", "Use the power of %s to transform items!");
        provider.add("monilabs.tooltip.prismatic.1", "Recipes MUST be input in the correct color state!");
        provider.add("monilabs.tooltip.prismatic.2", "They can even change the crucible's color randomly!");
        provider.add("monilabs.tooltip.prismatic_core", "The heart of the Prismatic Crucible!");

        provider.add("config.jade.plugin_monilabs.color_info", "Prismatic Crucible Color Info");
    }
}

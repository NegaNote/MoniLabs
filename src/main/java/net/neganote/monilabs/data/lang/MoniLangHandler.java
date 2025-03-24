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
        provider.add("monilabs.prismatic.color_name.turquoise", "§3Turquoise§r");
        provider.add("monilabs.prismatic.color_name.cyan", "§bCyan§r");
        provider.add("monilabs.prismatic.color_name.azure", "§9Azure§r");
        provider.add("monilabs.prismatic.color_name.blue", "§1Blue§r");
        provider.add("monilabs.prismatic.color_name.indigo", "§5Indigo§r");
        provider.add("monilabs.prismatic.color_name.magenta", "§dMagenta§r");
        provider.add("monilabs.prismatic.color_name.pink", "§cPink§r");

        provider.add("monilabs.recipe.required_color", "Required Initial Color: %s");
        provider.add("monilabs.recipe.result_color", "Resulting Color: \n%s");
        provider.add("monilabs.recipe.result_color_relative", "Resulting Color Increment: \n%s");
        provider.add("monilabs.recipe.color_list_random_start", "Possible Resulting Colors: \n");
        provider.add("monilabs.recipe.color_list_random_start_relative", "Possible Color Increments: \n");
        provider.add("monilabs.recipe.color_list_separator", ", ");
        provider.add("monilabs.recipe.fully_random_color",
                "Resulting Color State: \n§5R§dA§4N§cD§eO§aM §bC§3O§7L§1O§5R§r!");
        provider.add("monilabs.recipe.primary_input", "Accepted Initial Colors: Primary");
        provider.add("monilabs.recipe.secondary_input", "Accepted Initial Colors: Secondary");
        provider.add("monilabs.recipe.basic_input", "Accepted Initial Colors: Primary | Secondary");
        provider.add("monilabs.recipe.tertiary_input", "Accepted Initial Colors: Tertiary");
        provider.add("monilabs.recipe.any_input_color", "Accepted Initial Colors: ANY");

        provider.add("monilabs.recipe.mistake_input_colors", "You made a mistake defining this recipe's input colors.");
        provider.add("monilabs.recipe.mistake_output_colors",
                "You made a mistake defining this recipe's output colors.");

        provider.add("gtceu.prismatic_crucible", "Prismatic Crucible");

        provider.add("monilabs.tooltip.prismatic.rainbow", "the rainbow");
        provider.add("monilabs.tooltip.prismatic.0", "Use the power of %s to transform items!");
        provider.add("monilabs.tooltip.prismatic.1", "Recipes MUST be input in the correct color state!");
        provider.add("monilabs.tooltip.prismatic.2", "They can even change the crucible's color randomly!");
        provider.add("monilabs.tooltip.prismatic_core", "The heart of the Prismatic Crucible!");
    }
}

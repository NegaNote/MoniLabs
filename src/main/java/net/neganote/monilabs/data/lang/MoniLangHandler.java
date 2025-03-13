package net.neganote.monilabs.data.lang;

import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Style;

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
        provider.add("monilabs.recipe.required_colors_start", "Accepted Initial Colors: %s");
        provider.add("monilabs.recipe.result_color", "Resulting Color: %s");
        provider.add("monilabs.recipe.result_color_relative", "Resulting Color Increment: %s");
        provider.add("monilabs.recipe.color_list_random_start", "Possible Resulting Colors: ");
        provider.add("monilabs.recipe.color_list_random_start_relative", "Possible Resulting Color Increments: ");
        provider.add("monilabs.recipe.color_list_separator", ", ");
        provider.add("monilabs.recipe.fully_random_color", "Resulting Color State:  " + TooltipHelper.RAINBOW_HSL.apply(Style.EMPTY) + "random color§r!");
    }
}

package net.neganote.monilabs.data.lang;

import com.gregtechceu.gtceu.client.util.TooltipHelper;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import net.minecraft.network.chat.Style;

public class MoniLangHandler {
    public static void init(RegistrateLangProvider provider) {
        provider.add("monilabs.prismatic.current_color", "Currently has a color of %s");
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

        provider.add("monilabs.recipe.required_color", "Requires Crucible to be %s");
        provider.add("monilabs.recipe.result_color", "Crucible will become %s");
        provider.add("monilabs.recipe.two_possible_colors", "Crucible will become either %s or %s");
        provider.add("monilabs.recipe.color_list_random_start", "Crucible will become any of ");
        provider.add("monilabs.recipe.color_list_random_separator", ", ");
        provider.add("monilabs.recipe.color_list_random_end", ", or ");
        provider.add("monilabs.recipe.fully_random_color", "Crucible will become a " + TooltipHelper.RAINBOW_HSL.apply(Style.EMPTY) + "random color§r!");
    }
}

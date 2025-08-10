package net.neganote.monilabs.data.lang;

import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.config.MoniConfig;

import com.tterrag.registrate.providers.RegistrateLangProvider;
import dev.toma.configuration.config.value.ConfigValue;
import dev.toma.configuration.config.value.ObjectValue;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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

        provider.add("monilabs.multiblock.duplicate.1", "This multiblock is a duplicate");
        provider.add("monilabs.multiblock.duplicate.2", "Only one can exist");

        provider.add("monilabs.recipe.mistake_input_colors", "You made a mistake defining this recipe's input colors.");
        provider.add("monilabs.recipe.mistake_output_colors",
                "You made a mistake defining this recipe's output colors.");

        provider.add("monilabs.recipe.antimatter_random_range", "Input and outputs\nvary by %sx-%sx");

        provider.add("gtceu.antimatter_manipulator", "Antimatter Manipulation");
        provider.add("gtceu.chromatic_processing", "Chromatic Processing");
        provider.add("gtceu.chromatic_transcendence", "Chromatic Transcendence");
        provider.add("gtceu.microverse", "Microverse Mission");

        provider.add("monilabs.tooltip.prismatic.rainbow", "the rainbow");
        provider.add("monilabs.tooltip.prismatic.0", "Use the power of %s to transform items!");
        provider.add("monilabs.tooltip.prismatic.1", "Recipes MUST be input in the correct color state!");
        provider.add("monilabs.tooltip.prismatic.2", "They can even change the crucible's color randomly!");
        provider.add("monilabs.tooltip.prismatic_core", "The heart of the Prismatic Crucible!");

        provider.add("tooltip.monilabs.basic_microverse_projector.description",
                "§7A machine capable of stabilizing projection into microverses for basic miner missions.");
        provider.add("tooltip.monilabs.advanced_microverse_projector.description",
                "§7An advanced machine capable of stabilizing projection into microverses for complex missions into the deepest parts of space.");
        provider.add("tooltip.monilabs.elite_microverse_projector.description",
                "§7An elite machine capable of stabilizing projection into microverses for exotic missions, including collapsing the microverse whole.");
        provider.add("tooltip.monilabs.hyperbolic_microverse_projector.description.0",
                "§7An epic machine capable of stabilizing projection into hundreds of microverses at once.");
        provider.add("tooltip.monilabs.hyperbolic_microverse_projector.description.1",
                "Can perform all Microverse Missions.");

        provider.add("emi_info.monilabs.projector_info", "Projector tier: %d");
        provider.add("emi_info.monilabs.required_microverse", "Required type: %s");
        provider.add("emi_info.monilabs.new_microverse", "New type: %s");
        provider.add("emi_info.monilabs.integrity_drained", "Integrity drained: %.2f");

        provider.add("monilabs.tooltip.chroma_sensor_hatch.0", "Outputs color state of Prismatic Crucible");
        provider.add("monilabs.tooltip.chroma_sensor_hatch.1", "as a redstone signal. 0 for unformed,");
        provider.add("monilabs.tooltip.chroma_sensor_hatch.2", "1-12 for each color the PrismaC can be in.");

        provider.add("tooltip.monilabs.xp_draining_hatch.0", "Drains all XP to the Sculk Vat's internal");
        provider.add("tooltip.monilabs.xp_draining_hatch.1", "buffer for its processing.");
        provider.add("tooltip.monilabs.xp_draining_hatch.2", "Buffer continuously decays slightly.");

        provider.add("tooltip.monilabs.xp_sensor_hatch.0", "Outputs xp buffer status of sculk vat");
        provider.add("tooltip.monilabs.xp_sensor_hatch.1", "as a redstone signal.");

        provider.add("tooltip.monilabs.microverse_stability_hatch.0", "Outputs microverse stability");
        provider.add("tooltip.monilabs.microverse_stability_hatch.1", "as a redstone signal.");

        provider.add("microverse.monilabs.type.none", "None");
        provider.add("microverse.monilabs.type.normal", "Normal");
        provider.add("microverse.monilabs.type.hostile", "Hostile");
        provider.add("microverse.monilabs.type.shattered", "Shattered");
        provider.add("microverse.monilabs.type.corrupted", "Corrupted");

        provider.add("microverse.monilabs.current_microverse", "Microverse type: %s");
        provider.add("microverse.monilabs.integrity", "Microverse integrity: %.2f%%");

        provider.add("sculk_vat.monilabs.current_xp_buffer", "XP Stored: %d/%d");

        provider.add("config.jade.plugin_monilabs.color_info", "Prismatic Crucible Color Info");
        provider.add("config.jade.plugin_monilabs.microverse_info", "Microverse Projector Info");
        provider.add("config.jade.plugin_monilabs.sculk_vat_xp_info", "Sculk Vat XP Buffer Info");

        dfs(provider, new HashSet<>(), MoniConfig.CONFIG_HOLDER.getValueMap());
    }

    private static void dfs(RegistrateLangProvider provider, Set<String> added, Map<String, ConfigValue<?>> map) {
        for (var entry : map.entrySet()) {
            var id = entry.getValue().getId();
            if (added.add(id)) {
                provider.add(String.format("config.%s.option.%s", MoniLabs.MOD_ID, id), id);
            }
            if (entry.getValue() instanceof ObjectValue objectValue) {
                dfs(provider, added, objectValue.get());
            }
        }
    }
}

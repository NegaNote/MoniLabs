package net.neganote.monilabs.integration.kjs.recipe;


import com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema;

import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;

import static com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema.*;
import static net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.*;

import lombok.experimental.Accessors;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;

public interface MoniRecipeSchema {



    enum SpecialCase {
        PRIMARY,
        SECONDARY,
        BASIC,
        TERTIARY,
        ANY
    }

    @SuppressWarnings({ "unused", "UnusedReturnValue" })
    @Accessors(chain = true, fluent = true)
    class MoniRecipeJS extends GTRecipeSchema.GTRecipeJS {
        public GTRecipeSchema.GTRecipeJS inputColor(Color color) {
            this.input(MoniRecipeCapabilities.CHROMA, color);

            return this;
        }


        public GTRecipeSchema.GTRecipeJS outputStates(boolean relative, Color... states) {

            assert states.length > 0;                    // Should never happen anyway

            this.addData("output_states", states.length);
            this.addDataBool("color_change_relative", relative);
            if (states.length == Color.COLOR_COUNT) {
                return this;
            }

            for (int i = 0; i < states.length; i++) {
                this.addData("output_states_" + i, states[i].key);
            }
            return this;
        }

        public GTRecipeSchema.GTRecipeJS outputStates(Color... states) {
            return this.outputStates(false, states);
        }


        // Used to have a shorthand for special cases in recipe definitions
        public GTRecipeSchema.GTRecipeJS outputStatesSpecial(SpecialCase specialCase) {
            return switch (specialCase) {
                case PRIMARY -> this.outputStates(Color.RED, Color.GREEN, Color.BLUE); // Red, Green, Blue
                case SECONDARY -> this.outputStates(Color.YELLOW, Color.CYAN, Color.MAGENTA); // Yellow, Cyan, Magenta
                case BASIC -> this.outputStates(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE, Color.MAGENTA); // Primary + Secondary Colors
                case TERTIARY -> this.outputStates(Color.ORANGE, Color.LIME, Color.TEAL, Color.AZURE, Color.INDIGO, Color.PINK); // Non-Basic Colors

                // Saving computation by skipping unnecessary steps
                case ANY -> this.addData("output_states", Color.COLOR_COUNT);
            };
        }

    }

    RecipeSchema SCHEMA = new RecipeSchema(MoniRecipeJS.class, MoniRecipeJS::new, DURATION, DATA, CONDITIONS,
            ALL_INPUTS, ALL_TICK_INPUTS, ALL_OUTPUTS, ALL_TICK_OUTPUTS,
            INPUT_CHANCE_LOGICS, OUTPUT_CHANCE_LOGICS, TICK_INPUT_CHANCE_LOGICS, TICK_OUTPUT_CHANCE_LOGICS,
            IS_FUEL, CATEGORY)
            .constructor((recipe, schemaType, keys, from) -> recipe.id(from.getValue(recipe, ID)), ID)
            .constructor(DURATION, CONDITIONS, ALL_INPUTS, ALL_OUTPUTS, ALL_TICK_INPUTS, ALL_TICK_OUTPUTS);
}
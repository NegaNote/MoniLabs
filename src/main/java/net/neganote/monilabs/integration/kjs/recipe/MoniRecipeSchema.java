package net.neganote.monilabs.integration.kjs.recipe;


import com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema;

import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import lombok.experimental.Accessors;

import static com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema.*;
import static net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine.*;

import lombok.experimental.Accessors;

import java.util.stream.IntStream;

public interface MoniRecipeSchema {



    public enum SpecialCase {
        PRIMARY,
        SECONDARY,
        BASIC,
        TERTIARY,
        ANY;
    }

    @SuppressWarnings({ "unused", "UnusedReturnValue" })
    class MoniRecipeJS extends GTRecipeSchema.GTRecipeJS {

        public GTRecipeSchema.GTRecipeJS inputStates(int... states) {

            assert states.length > 0;                    // Should never happen anyway
            states = IntStream.of(states)
                    .map(s -> Math.floorMod(s, Color.COLOR_COUNT))  // Keep all states within range.
                    .sorted()                                           // Sort states
                    .distinct()                                         // Remove duplicates
                    .toArray();

            this.addData("input_states", states.length);
            if (states.length == Color.COLOR_COUNT) { // Saves adding and reading unnecessary data if ANY
                return this;
            }

            for (int i = 0; i < states.length; i++) {
                this.addData("input_states_" + i, states[i]);
            }
            return this;
        }

        // Used to have a shorthand for special cases in recipe definitions
        public GTRecipeSchema.GTRecipeJS inputStates(SpecialCase specialCase) {
            return switch (specialCase) {
                case PRIMARY -> this.inputStates(0, 4, 8); // Red, Green, Blue
                case SECONDARY -> this.inputStates(2, 6, 10); // Yellow, Cyan, Magenta
                case BASIC -> this.inputStates(0, 2, 4, 6, 8, 10); // Primary + Secondary Colors
                case TERTIARY -> this.inputStates(1, 3, 5, 7, 9, 11); // Non-Basic Colors

                // Saving computation by skipping unnecessary steps
                case ANY -> this.addData("input_states", Color.COLOR_COUNT);
            };
        }


        public GTRecipeSchema.GTRecipeJS outputStates(boolean relative, int... states) {

            assert states.length > 0;                    // Should never happen anyway
            states = IntStream.of(states)
                    .map(s -> Math.floorMod(s, Color.COLOR_COUNT))  // Keep all states within range.
                    .sorted()                                           // Sort states
                    .distinct()                                         // Remove duplicates
                    .toArray();

            this.addData("output_states", states.length);
            this.addDataBool("color_change_relative", relative);
            if (states.length == Color.COLOR_COUNT) {
                return this;
            }

            for (int i = 0; i < states.length; i++) {
                this.addData("output_states_" + i, states[i]);
            }
            return this;
        }

        public GTRecipeSchema.GTRecipeJS outputStates(int... states) {
            return this.outputStates(false, states);
        }


        // Used to have a shorthand for special cases in recipe definitions
        public GTRecipeSchema.GTRecipeJS outputStates(SpecialCase specialCase) {
            return switch (specialCase) {
                case PRIMARY -> this.outputStates(0, 4, 8); // Red, Green, Blue
                case SECONDARY -> this.outputStates(2, 6, 10); // Yellow, Cyan, Magenta
                case BASIC -> this.outputStates(0, 2, 4, 6, 8, 10); // Primary + Secondary Colors
                case TERTIARY -> this.outputStates(1, 3, 5, 7, 9, 11); // Non-Basic Colors

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
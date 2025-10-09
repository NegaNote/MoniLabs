package net.neganote.monilabs.integration.kjs.recipe;

import com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema;

import net.neganote.monilabs.capability.recipe.ChromaIngredient;
import net.neganote.monilabs.capability.recipe.MoniRecipeCapabilities;
import net.neganote.monilabs.common.machine.multiblock.Color;
import net.neganote.monilabs.common.machine.multiblock.Microverse;

import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import lombok.experimental.Accessors;

import static com.gregtechceu.gtceu.integration.kjs.recipe.GTRecipeSchema.*;

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
            this.input(MoniRecipeCapabilities.CHROMA, ChromaIngredient.of(color));

            return this;
        }

        public GTRecipeSchema.GTRecipeJS outputStatesRelative(int... increments) {
            assert increments.length > 0;                    // Should never happen anyway

            this.addData("output_states", increments.length);
            this.addDataBool("color_change_relative", true);

            for (int i = 0; i < increments.length; i++) {
                this.addData("output_states_" + i, increments[i]);
            }
            return this;
        }

        public GTRecipeSchema.GTRecipeJS outputStatesNormal(Color... states) {
            assert states.length > 0;                    // Should never happen anyway

            this.addData("output_states", states.length);
            this.addDataBool("color_change_relative", false);
            if (states.length == Color.COLOR_COUNT) {
                return this;
            }

            for (int i = 0; i < states.length; i++) {
                this.addData("output_states_" + i, states[i].key);
            }
            return this;
        }

        // Used to have a shorthand for special cases in recipe definitions
        public GTRecipeSchema.GTRecipeJS outputStatesSpecial(SpecialCase specialCase) {
            return switch (specialCase) {
                case PRIMARY -> this.outputStatesNormal(Color.RED, Color.GREEN, Color.BLUE); // Red, Green, Blue
                case SECONDARY -> this.outputStatesNormal(Color.YELLOW, Color.CYAN, Color.MAGENTA); // Yellow, Cyan,
                                                                                                    // Magenta
                case BASIC -> this.outputStatesNormal(Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN, Color.BLUE,
                        Color.MAGENTA); // Primary + Secondary Colors
                case TERTIARY -> this.outputStatesNormal(Color.ORANGE, Color.LIME, Color.TEAL, Color.AZURE,
                        Color.INDIGO, Color.PINK); // Non-Basic Colors

                // Saving computation by skipping unnecessary steps
                case ANY -> this.addData("output_states", Color.COLOR_COUNT);
            };
        }

        public GTRecipeSchema.GTRecipeJS activateAntimatterRandomness() {
            this.addDataBool("antimatterRandom", true);
            return this;
        }

        public GTRecipeSchema.GTRecipeJS xpRange(int min, int max) {
            this.addData("minimumXp", min);
            this.addData("maximumXp", max);
            return this;
        }

        public GTRecipeSchema.GTRecipeJS requiredMicroverse(int i) {
            this.input(MoniRecipeCapabilities.MICROVERSE, Microverse.getMicroverseFromKey(i));
            return this;
        }

        public GTRecipeSchema.GTRecipeJS damageRate(int rate) {
            this.addData("damage_rate", rate);
            return this;
        }

        public GTRecipeSchema.GTRecipeJS updateMicroverse(int i) {
            return this.updateMicroverse(i, false);
        }

        public GTRecipeSchema.GTRecipeJS updateMicroverse(int i, boolean keepIntegrity) {
            int updatedMicroverse = Math.abs(i);
            this.addData("updated_microverse", updatedMicroverse);
            this.addDataBool("keep_integrity", keepIntegrity);
            return this;
        }

        public GTRecipeSchema.GTRecipeJS blacklistMicroverseParallels() {
            this.addDataBool("blacklistParallel", true);
            return this;
        }
    }

    RecipeSchema SCHEMA = new RecipeSchema(MoniRecipeJS.class, MoniRecipeJS::new, DURATION, DATA, CONDITIONS,
            ALL_INPUTS, ALL_TICK_INPUTS, ALL_OUTPUTS, ALL_TICK_OUTPUTS,
            INPUT_CHANCE_LOGICS, OUTPUT_CHANCE_LOGICS, TICK_INPUT_CHANCE_LOGICS, TICK_OUTPUT_CHANCE_LOGICS,
            CATEGORY)
            .constructor((recipe, schemaType, keys, from) -> recipe.id(from.getValue(recipe, ID)), ID)
            .constructor(DURATION, CONDITIONS, ALL_INPUTS, ALL_OUTPUTS, ALL_TICK_INPUTS, ALL_TICK_OUTPUTS);
}

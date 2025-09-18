ServerEvents.recipes(event => {
    event.recipes.gtceu.sculk_vat("sculk_vat_test")
        .itemInputs("minecraft:apple")
        .outputFluids("minecraft:lava 3000")
        .duration(10)
})
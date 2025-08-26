ServerEvents.recipes(event => {
    event.recipes.gtceu.creative_energy_multi("creative_energy_test")
        .inputFluids("minecraft:lava 4")
        .duration(200)

    event.recipes.gtceu.creative_data_multi("creative_energy_test")
            .inputFluids("minecraft:lava 4")
            .EUt(40000)
            .CWUt(30)
            .duration(200)


})
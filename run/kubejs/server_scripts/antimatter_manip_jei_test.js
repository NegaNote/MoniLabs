ServerEvents.recipes(event =>{
    event.recipes.gtceu.antimatter_manipulator("antimatter_manip_test")
        .itemInputs("8x minecraft:iron_ingot")
        .itemOutputs("8x minecraft:gold_ingot")
        .activateAntimatterRandomness()
})

ServerEvents.recipes(event =>{
    event.recipes.gtceu.chromatic_processing("antimatter_manip_test")
        .itemInputs("8x minecraft:iron_ingot")
        .itemOutputs("8x minecraft:gold_ingot")
        .inputColor(PrismaticColor.RED)
        .outputStatesNormal(PrismaticColor.GREEN)
        .EUt(GTValues.VA[GTValues.ZPM])
        .duration(40)
})

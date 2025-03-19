ServerEvents.recipes(event =>{
    event.recipes.gtceu.prismatic_crucible("test1")
        .itemInputs("minecraft:blue_dye")
        .itemOutputs("minecraft:yellow_dye")
        .EUt(128)
        .duration(100)

    event.recipes.gtceu.prismatic_crucible("test2")
        .inputStates(0)
        .itemInputs("minecraft:red_dye")
        .itemOutputs("minecraft:green_dye")
        .EUt(128)
        .duration(100)
        .outputStates(4)

    event.recipes.gtceu.prismatic_crucible("test3")
        .inputStates(4)
        .itemInputs("minecraft:green_dye")
        .itemOutputs("minecraft:red_dye")
        .EUt(128)
        .duration(100)
        .outputStates(0)

    event.recipes.gtceu.prismatic_crucible("test4")
        .inputStates(4, 6, 7)
        .itemInputs("minecraft:stone")
        .itemOutputs("minecraft:cobblestone")
        .EUt(128)
        .duration(100)
        .outputStates(0, 1, 2)

    event.recipes.gtceu.prismatic_crucible("test5")
        .inputStatesSpecial(0)
        .itemInputs("minecraft:white_dye")
        .itemOutputs("minecraft:black_dye")
        .EUt(128)
        .duration(100)
        .outputStates(true, 4, -4)

    event.recipes.gtceu.prismatic_crucible("test6")
        .inputStates(PrismaticColor.RED.key)
        .itemInputs("minecraft:stone")
        .itemOutputs("minecraft:diamond", 4)
        .EUt(128)
        .duration(100)
        .outputStates(4, 6, 7)
})
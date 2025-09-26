ServerEvents.recipes(event => {
    event.recipes.gtceu.microverse("normal_microverse_projection")
        .itemInputs("minecraft:iron_ingot")
        .updateMicroverse(1) // Normal
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(20)

    event.recipes.gtceu.microverse("using_normal_microverse")
        .itemInputs("minecraft:gold_ingot")
        .itemOutputs("minecraft:gold_ingot", "minecraft:diamond")
        .requiredMicroverse(1)
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(50) // 100s, like a T1 mission
})
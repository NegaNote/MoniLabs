ServerEvents.recipes(event => {
    event.recipes.gtceu.microverse("normal_microverse_projection")
        .itemInputs("minecraft:iron_ingot")
        .updateMicroverse(1) // Normal
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(20)

    event.recipes.gtceu.microverse("nomnom")
        .itemInputs("minecraft:copper_ingot")
        .damageRate(40)
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(200)

    event.recipes.gtceu.microverse("nomnomnom")
        .itemInputs("minecraft:diamond")
        .damageRate(400)
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(200)

    event.recipes.gtceu.microverse("nya")
        .itemInputs("minecraft:emerald")
        .damageRate(-40)
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(200)

    event.recipes.gtceu.microverse("nyanya")
        .itemInputs("minecraft:netherite_ingot")
        .damageRate(-400)
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(200)

    event.recipes.gtceu.microverse("hostile")
        .itemInputs("minecraft:gold_ingot")
        .updateMicroverse(2) // Normal
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(20)

    event.recipes.gtceu.microverse("supercharged")
        .itemInputs("minecraft:stick")
        .updateMicroverse(7, true) // Normal
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(20)
})
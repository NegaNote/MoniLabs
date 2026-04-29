ServerEvents.recipes(event => {
    event.recipes.gtceu.microverse("normal_microverse_projection")
        .itemInputs("minecraft:iron_ingot")
        .updateMicroverse(1) // Normal
        .addData("projector_tier", 1)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(20)

    event.recipes.gtceu.virtual_particle_synthesis("nomnom")
        .itemInputs("minecraft:iron_ingot")
        .itemOutputs("minecraft:gold_ingot", "minecraft:diamond")
        .quantumRule(QuantumRule.QUANTUM_ENTANGLEMENT, IO.OUT)
        .EUt(GTValues.VHA[GTValues.HV])
        .duration(40)
})
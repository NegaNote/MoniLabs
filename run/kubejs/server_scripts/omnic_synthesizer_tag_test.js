const omnic_synthesis = (event, item) => {
    event.recipes.gtceu.omnic_synthesis("omnic_synthesis_from_" + item)
        .itemInputs(item)
        .itemOutputs("monilabs:mote_of_omnium")
        .EUt(120000)
        .duration(2)
}

ServerEvents.recipes(event => {
    omnic_synthesis(event, "minecraft:iron_ingot");
    omnic_synthesis(event, "minecraft:gold_ingot");
    omnic_synthesis(event, "minecraft:copper_ingot");
    omnic_synthesis(event, "minecraft:coal");
    omnic_synthesis(event, "minecraft:nether_star");
    omnic_synthesis(event, "minecraft:diamond");
})

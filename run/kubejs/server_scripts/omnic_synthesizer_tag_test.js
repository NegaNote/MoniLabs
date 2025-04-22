const omnic_synthesis = (event, item) => {
    event.add("monilabs:omnic_synthesizer_input", item);
}

ServerEvents.tags("item", event => {
    omnic_synthesis(event, "minecraft:iron_ingot");
    omnic_synthesis(event, "minecraft:gold_ingot");
    omnic_synthesis(event, "minecraft:copper_ingot");
    omnic_synthesis(event, "minecraft:coal");
    omnic_synthesis(event, "minecraft:nether_star");
    omnic_synthesis(event, "minecraft:diamond");
})
// priority: 0

// Visit the wiki for more info - https://kubejs.com/

console.info('Hello, World! (Loaded startup scripts)')

StartupEvents.registry("block",  event => {
    event.create("prism_glass")
            .displayName("P.R.I.S.M. Glass")
            .soundType("glass")
            .renderType("cutout")
            .resistance(6).hardness(5)
            .tagBlock("mineable/pickaxe")
            .defaultCutout()
})
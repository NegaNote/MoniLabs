package net.neganote.monilabs;

import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.gregtechceu.gtceu.common.data.GTItems;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neganote.monilabs.common.item.MoniItems;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.data.MoniDataGen;
import net.neganote.monilabs.common.machine.MoniMachines;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;

import com.tterrag.registrate.util.entry.RegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(MoniLabs.MOD_ID)
public class MoniLabs {

    public static final String MOD_ID = "monilabs";
    public static final Logger LOGGER = LogManager.getLogger();
    public static GTRegistrate REGISTRATE = GTRegistrate.create(MoniLabs.MOD_ID);

    public static RegistryEntry<CreativeModeTab> MONI_CREATIVE_TAB = REGISTRATE.defaultCreativeTab(MoniLabs.MOD_ID,
            builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(MoniLabs.MOD_ID, REGISTRATE))
                    .title(REGISTRATE.addLang("itemGroup", MoniLabs.id("creative_tab"), "Moni Labs"))
                    .icon(GTItems.ADVANCED_CIRCUIT_BOARD::asStack) // TODO: come up with own icon
                    .build())
            .register();

    public MoniLabs() {
        MoniLabs.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::addMaterialRegistries);
        modEventBus.addListener(this::addMaterials);
        modEventBus.addListener(this::modifyMaterials);
        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);

        // Most other events are fired on Forge's bus.
        // If we want to use annotations to register event listeners,
        // we need to register our object like this!
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void init() {
        REGISTRATE.registerRegistrate();
        MoniBlocks.init();
        MoniItems.init();
        MoniDataGen.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            LOGGER.info("Hello from common setup! This is *after* registries are done, so we can do this:");
            LOGGER.info("Look, I found a {}!", Items.DIAMOND);
        });
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Hey, we're on Minecraft version {}!", Minecraft.getInstance().getLaunchedVersion());
    }

    // You MUST have this for custom materials.
    // Remember to register them not to GT's namespace, but your own.
    private void addMaterialRegistries(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MoniLabs.MOD_ID);
    }

    // As well as this.
    private void addMaterials(MaterialEvent event) {
        // CustomMaterials.init();
    }

    // This is optional, though.
    private void modifyMaterials(PostMaterialEvent event) {
        // CustomMaterials.modify();
    }

    private void registerRecipeTypes(GTCEuAPI.RegisterEvent<ResourceLocation, GTRecipeType> event) {
        MoniRecipeTypes.init();
    }

    private void registerMachines(GTCEuAPI.RegisterEvent<ResourceLocation, MachineDefinition> event) {
        MoniMachines.init();
    }
}

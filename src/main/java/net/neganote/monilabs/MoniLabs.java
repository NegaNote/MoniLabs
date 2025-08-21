package net.neganote.monilabs;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.GTCEuAPI;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.MaterialRegistryEvent;
import com.gregtechceu.gtceu.api.data.chemical.material.event.PostMaterialEvent;
import com.gregtechceu.gtceu.api.machine.MachineDefinition;
import com.gregtechceu.gtceu.api.recipe.GTRecipeType;
import com.gregtechceu.gtceu.api.recipe.lookup.ingredient.MapIngredientTypeManager;
import com.gregtechceu.gtceu.api.registry.registrate.GTRegistrate;
import com.gregtechceu.gtceu.client.renderer.machine.DynamicRenderManager;
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neganote.monilabs.capability.recipe.ChromaIngredient;
import net.neganote.monilabs.capability.recipe.MapColorIngredient;
import net.neganote.monilabs.client.render.MicroverseProjectorRender;
import net.neganote.monilabs.client.render.MoniShaders;
import net.neganote.monilabs.client.render.PrismaticCrucibleRender;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.data.MoniPlaceholders;
import net.neganote.monilabs.common.data.materials.MoniMaterials;
import net.neganote.monilabs.common.item.MoniItems;
import net.neganote.monilabs.common.machine.MoniMachines;
import net.neganote.monilabs.config.MoniConfig;
import net.neganote.monilabs.data.MoniDataGen;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;

import com.tterrag.registrate.util.entry.RegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

@Mod(MoniLabs.MOD_ID)
public class MoniLabs {

    public static final String MOD_ID = "monilabs";

    @SuppressWarnings("unused")
    public static final Logger LOGGER = LogManager.getLogger();
    public static GTRegistrate REGISTRATE = GTRegistrate.create(MoniLabs.MOD_ID);

    public static RegistryEntry<CreativeModeTab> MONI_CREATIVE_TAB = REGISTRATE.defaultCreativeTab(MoniLabs.MOD_ID,
            builder -> builder
                    .displayItems(new GTCreativeModeTabs.RegistrateDisplayItemsGenerator(MoniLabs.MOD_ID, REGISTRATE))
                    .title(REGISTRATE.addLang("itemGroup", MoniLabs.id("creative_tab"), "Moni Labs (Coremod)"))
                    .icon(MoniMachines.CHROMA_SENSOR_HATCH::asStack)
                    .build())
            .register();

    @SuppressWarnings({ "unused", "removal" })
    private MoniLabs() {
        this(FMLJavaModLoadingContext.get());
    }

    public MoniLabs(FMLJavaModLoadingContext context) {
        MoniLabs.init();
        IEventBus modEventBus = context.getModEventBus();

        modEventBus.addListener(this::commonSetup);
        if (GTCEu.isClientSide()) {
            initializeDynamicRenders();
            modEventBus.register(MoniShaders.class);
        }
        modEventBus.addListener(this::addMaterialRegistries);
        modEventBus.addListener(this::addMaterials);
        modEventBus.addListener(this::modifyMaterials);
        modEventBus.addGenericListener(GTRecipeType.class, this::registerRecipeTypes);
        modEventBus.addGenericListener(MachineDefinition.class, this::registerMachines);
        modEventBus.addGenericListener(CoverDefinition.class, this::registerCovers);

        // Most other events are fired on Forge's bus.
        // If we want to use annotations to register event listeners,
        // we need to register our object like this!
        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void init() {
        MoniConfig.init();
        REGISTRATE.registerRegistrate();
        MoniBlocks.init();
        MoniItems.init();
        MoniDataGen.init();
        MoniPlaceholders.init();
    }

    @Contract("_ -> new")
    public static @NotNull ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    @SubscribeEvent
    public void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(
                () -> MapIngredientTypeManager.registerMapIngredient(ChromaIngredient.class, MapColorIngredient::from));
    }

    @SubscribeEvent
    @SuppressWarnings("removal")
    public void clientSetup(final FMLClientSetupEvent event) {
        ItemBlockRenderTypes.setRenderLayer(MoniBlocks.PRISM_GLASS.get(), RenderType.cutout());
    }

    private void initializeDynamicRenders() {
        DynamicRenderManager.register(MoniLabs.id("prismatic_crucible"), PrismaticCrucibleRender.TYPE);
        DynamicRenderManager.register(MoniLabs.id("microverse_projector"), MicroverseProjectorRender.TYPE);
    }

    // You MUST have this for custom materials.
    // Remember to register them not to GT's namespace, but your own.
    private void addMaterialRegistries(MaterialRegistryEvent event) {
        GTCEuAPI.materialManager.createRegistry(MoniLabs.MOD_ID);
    }

    // As well as this.
    private void addMaterials(MaterialEvent event) {
        MoniMaterials.register();
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

    @Contract("_ -> new")
    @SuppressWarnings("unused")
    public static @NotNull ResourceLocation kjsResLoc(String path) {
        return ResourceLocation.fromNamespaceAndPath("kubejs", path);
    }

    private void registerCovers(GTCEuAPI.RegisterEvent<ResourceLocation, CoverDefinition> event) {}
}

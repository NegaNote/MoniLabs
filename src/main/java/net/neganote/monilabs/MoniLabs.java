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
import com.gregtechceu.gtceu.common.data.GTCreativeModeTabs;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.neganote.monilabs.capability.recipe.ChromaIngredient;
import net.neganote.monilabs.capability.recipe.MapColorIngredient;
import net.neganote.monilabs.client.render.effects.MoniTrails;
import net.neganote.monilabs.client.render.effects.ParticleTypes;
import net.neganote.monilabs.client.render.effects.PrismFX;
import net.neganote.monilabs.client.renderer.MoniShaders;
import net.neganote.monilabs.common.block.MoniBlocks;
import net.neganote.monilabs.common.item.MoniItems;
import net.neganote.monilabs.common.machine.MoniMachines;
import net.neganote.monilabs.config.MoniConfig;
import net.neganote.monilabs.data.MoniDataGen;
import net.neganote.monilabs.gtbridge.MoniRecipeTypes;
import net.neganote.monilabs.utils.CalendarUtil;
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
                    .title(REGISTRATE.addLang("itemGroup", MoniLabs.id("creative_tab"), "Moni Labs (Coremod)"))
                    .icon(MoniMachines.CHROMA_SENSOR_HATCH::asStack)
                    .build())
            .register();

    public MoniLabs() {
        MoniLabs.init();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        if (GTCEu.isClientSide()) {
            modEventBus.addListener(this::clientSetup);
            modEventBus.register(MoniShaders.class);
        }
        modEventBus.addListener(this::addMaterialRegistries);
        modEventBus.addListener(this::addMaterials);
        modEventBus.addListener(this::modifyMaterials);
        modEventBus.addListener((RegisterEvent event) -> {
            event
                .register(ForgeRegistries.PARTICLE_TYPES.getRegistryKey(),
                    MoniLabs.id("chroma_background"),
                    () -> ParticleTypes.CHROMA_BACKGROUND);
            event
                .register(ForgeRegistries.PARTICLE_TYPES.getRegistryKey(),
                    MoniLabs.id("chroma_beta"),
                    () -> ParticleTypes.CHROMA_BETA);
            event
                .register(ForgeRegistries.PARTICLE_TYPES.getRegistryKey(),
                    MoniLabs.id("chroma_set"),
                    () -> ParticleTypes.CHROMA_SET);
        });
        modEventBus.addListener((RegisterParticleProvidersEvent event) -> {
            event
                .registerSpriteSet(ParticleTypes.CHROMA_BACKGROUND,
                    PrismFX.PositionalColor::new);
            event
                .registerSpriteSet(ParticleTypes.CHROMA_BETA,
                    PrismFX.PositionalColor::new);
            event
                .registerSpriteSet(ParticleTypes.CHROMA_SET,
                    PrismFX.SetColor::new);
        });
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
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            MapIngredientTypeManager.registerMapIngredient(ChromaIngredient.class, MapColorIngredient::from);
        });
        CalendarUtil.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        MoniTrails.init();
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

    @SuppressWarnings("unused")
    public static ResourceLocation kjsResLoc(String path) {
        return new ResourceLocation("kubejs", path);
    }

    private void registerCovers(GTCEuAPI.RegisterEvent<ResourceLocation, CoverDefinition> event) {}
}

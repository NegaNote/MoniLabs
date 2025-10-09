package net.neganote.monilabs.data.models;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.part.FillLevel;
import net.neganote.monilabs.common.machine.part.RenderColor;

public class MoniMachineModels {

    public static MachineBuilder.ModelInitializer createOverlayChromaCasingMachineModel(String overlayName,
                                                                                        String casingTexturePath) {
        return (ctx, prov, builder) -> {
            builder.forAllStatesModels(state -> {
                BlockModelBuilder model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GTCEu.id("block/overlay/front_emissive")));
                casingTextures(model, casingTexturePath);

                var prop = state.getValue(RenderColor.COLOR_PROPERTY);
                var key = prop.key;
                model.texture("overlay_emissive",
                        MoniLabs.id("block/overlay/machine/overlay_" + overlayName + "_" + (key + 1)));
                return model;
            });

            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

    public static MachineBuilder.ModelInitializer createOverlayFillLevelCasingMachineModel(String overlayName,
                                                                                           String casingTexturePath) {
        return (ctx, prov, builder) -> {
            builder.forAllStatesModels(state -> {
                BlockModelBuilder model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GTCEu.id("block/overlay/2_layer/front_emissive")));
                casingTextures(model, casingTexturePath);

                var prop = state.getValue(FillLevel.FILL_PROPERTY);
                var key = prop.key;
                model.texture("overlay", MoniLabs.id("block/overlay/machine/" + overlayName + "_base"));
                model.texture("overlay_emissive",
                        MoniLabs.id("block/overlay/machine/" + overlayName + "_overlay_" + key));
                return model;
            });

            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

    public static MachineBuilder.ModelInitializer createOverlayCasingMachineModel(String overlayName,
                                                                                  String casingTexturePath) {
        return (ctx, prov, builder) -> {
            builder.forAllStatesModels(state -> {
                BlockModelBuilder model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GTCEu.id("block/overlay/2_layer/front_emissive")));
                casingTextures(model, casingTexturePath);

                model.texture("overlay", MoniLabs.id("block/overlay/machine/" + overlayName + "_base"));
                model.texture("overlay_emissive",
                        MoniLabs.id("block/overlay/machine/" + overlayName + "_emissive"));
                return model;
            });

            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

    public static void casingTextures(BlockModelBuilder model, String casingTexturePath) {
        ResourceLocation casingTexture = MoniLabs.id("block/" + casingTexturePath);
        model.texture("bottom", casingTexture);
        model.texture("top", casingTexture);
        model.texture("side", casingTexture);
    }
}

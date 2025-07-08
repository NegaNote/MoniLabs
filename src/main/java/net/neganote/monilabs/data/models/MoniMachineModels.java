package net.neganote.monilabs.data.models;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.registry.registrate.MachineBuilder;

import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.part.FillLevel;
import net.neganote.monilabs.common.machine.part.RenderColor;

import static com.gregtechceu.gtceu.common.data.models.GTMachineModels.tieredHullTextures;

public class MoniMachineModels {

    public static MachineBuilder.ModelInitializer createOverlayChromaMachineModel(String overlayName) {
        return (ctx, prov, builder) -> {
            builder.forAllStatesModels(state -> {
                BlockModelBuilder model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GTCEu.id("block/overlay/front_emissive")));
                tieredHullTextures(model, builder.getOwner().getTier());

                var prop = state.getValue(RenderColor.COLOR_PROPERTY);
                var key = prop.key;
                model.texture("overlay_emissive",
                        MoniLabs.id("block/overlay/machine/overlay_" + overlayName + "_" + (key + 1)));
                return model;
            });

            builder.addReplaceableTextures("bottom", "top", "side");
        };
    }

    public static MachineBuilder.ModelInitializer createOverlayFillLevelMachineModel(String overlayName) {
        return (ctx, prov, builder) -> {
            builder.forAllStatesModels(state -> {
                BlockModelBuilder model = prov.models().nested()
                        .parent(prov.models().getExistingFile(GTCEu.id("block/overlay/2_layer/front_emissive")));
                tieredHullTextures(model, builder.getOwner().getTier());

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
}

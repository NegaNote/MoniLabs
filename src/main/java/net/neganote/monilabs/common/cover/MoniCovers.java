package net.neganote.monilabs.common.cover;

import com.gregtechceu.gtceu.GTCEu;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.api.item.ComponentItem;
import com.gregtechceu.gtceu.api.registry.GTRegistries;
import com.gregtechceu.gtceu.client.renderer.cover.ICoverRenderer;
import com.gregtechceu.gtceu.client.renderer.cover.SimpleCoverRenderer;
import com.gregtechceu.gtceu.common.item.CoverPlaceBehavior;

import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.cover.detector.ChromaDetectorCover;
import net.neganote.monilabs.common.item.MoniItems;

import com.tterrag.registrate.util.entry.ItemEntry;

import static net.neganote.monilabs.MoniLabs.REGISTRATE;

@SuppressWarnings("unused")
public class MoniCovers {

    public final static CoverDefinition CHROMA_DETECTOR_COVER = register("chroma_detector", ChromaDetectorCover::new,
            new SimpleCoverRenderer(GTCEu.id("block/cover/overlay_energy_detector")));
    // TODO: replace with our own overlay

    public static ItemEntry<ComponentItem> CHROMA_DETECTOR_ITEM = REGISTRATE
            .item("chroma_detector_cover", ComponentItem::create)
            .lang("Chroma Detector Cover")
            .onRegister(MoniItems.attach(new CoverPlaceBehavior(CHROMA_DETECTOR_COVER)))
            .register();

    public static CoverDefinition register(String id, CoverDefinition.CoverBehaviourProvider behaviorCreator,
                                           ICoverRenderer coverRenderer) {
        var definition = new CoverDefinition(MoniLabs.id(id), behaviorCreator, coverRenderer);
        GTRegistries.COVERS.register(MoniLabs.id(id), definition);
        return definition;
    }

    public static void init() {}
}

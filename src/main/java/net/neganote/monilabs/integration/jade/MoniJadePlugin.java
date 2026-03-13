package net.neganote.monilabs.integration.jade;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.monilabs.integration.jade.provider.*;

import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@SuppressWarnings("unused")
@WailaPlugin
public class MoniJadePlugin implements IWailaPlugin {

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerBlockDataProvider(new PrismaticColorBlockProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new MicroverseInfoBlockProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new SculkVatXPProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new OmnicSynthProgressProvider(), BlockEntity.class);
        registration.registerBlockDataProvider(new VirtualParticleSynthQuantumProvider(), BlockEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new PrismaticColorBlockProvider(), Block.class);
        registration.registerBlockComponent(new MicroverseInfoBlockProvider(), Block.class);
        registration.registerBlockComponent(new SculkVatXPProvider(), Block.class);
        registration.registerBlockComponent(new OmnicSynthProgressProvider(), Block.class);
        registration.registerBlockComponent(new VirtualParticleSynthQuantumProvider(), Block.class);
    }
}

package net.neganote.monilabs.integration.jade;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.monilabs.integration.jade.provider.MicroverseInfoBlockProvider;
import net.neganote.monilabs.integration.jade.provider.PrismaticColorBlockProvider;

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
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new PrismaticColorBlockProvider(), Block.class);
        registration.registerBlockComponent(new MicroverseInfoBlockProvider(), Block.class);
    }
}

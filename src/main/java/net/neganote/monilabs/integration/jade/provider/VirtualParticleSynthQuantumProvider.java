package net.neganote.monilabs.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.VirtualParticleSynthesizerMachine;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class VirtualParticleSynthQuantumProvider implements IBlockComponentProvider,
                                                 IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity be = blockAccessor.getBlockEntity();
        if (be instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof VirtualParticleSynthesizerMachine) {
            CompoundTag data = blockAccessor.getServerData();
            if (data.contains("quantumNoise")) {
                int noise = data.getInt("quantumNoise");
                iTooltip.add(Component.translatable("virtual_particle_synthesis.monilabs.noise", noise));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof VirtualParticleSynthesizerMachine vps) {
            compoundTag.putInt("quantumNoise", vps.getQuantumNoise());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MoniLabs.id("virtual_particle_synth_info");
    }
}

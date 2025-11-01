package net.neganote.monilabs.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.OmnicSynthesizerMachine;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class OmnicSynthProgressProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof OmnicSynthesizerMachine) {
            CompoundTag data = blockAccessor.getServerData();
            if (data.contains("currentDiversityPoints")) {
                var currentDiversityPoints = data.getInt("currentDiversityPoints");
                iTooltip.add(Component.translatable("monilabs.omnic.current_diversity_points",
                        currentDiversityPoints)
                        .append(Component.literal("%")));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof OmnicSynthesizerMachine machine && machine.isFormed()) {
            compoundTag.putInt("currentDiversityPoints", machine.diversityPoints);
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MoniLabs.id("omnic_synth_info");
    }
}

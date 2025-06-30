package net.neganote.monilabs.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine.Microverse;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public class MicroverseInfoBlockProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof MicroverseProjectorMachine) {
            CompoundTag data = blockAccessor.getServerData().getCompound(getUid().toString());
            if (data.contains("currentMicroverse")) {
                int currentMicroverse = data.getInt("currentMicroverse");
                iTooltip.add(Component.translatable("microverse.monilabs.current_microverse",
                        Component.translatable(Microverse.MICROVERSES[currentMicroverse].langKey)));
            }
            if (data.contains("microverseIntegrity")) {
                int integrity = data.getInt("microverseIntegrity");
                iTooltip.add(Component.translatable("microverse.monilabs.integrity",
                        (float) integrity / MicroverseProjectorMachine.FLUX_REPAIR_AMOUNT));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        CompoundTag data = compoundTag.getCompound(getUid().toString());
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof MicroverseProjectorMachine machine && machine.isFormed()) {
            data.putInt("currentMicroverse", machine.getMicroverse().key);
            if (machine.getMicroverse() != Microverse.NONE) {
                data.putInt("microverseIntegrity", machine.getMicroverseIntegrity());
            }
        }
        compoundTag.put(getUid().toString(), data);
    }

    @Override
    public ResourceLocation getUid() {
        return MoniLabs.id("microverse_info");
    }
}

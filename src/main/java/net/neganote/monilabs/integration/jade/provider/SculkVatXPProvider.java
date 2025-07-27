package net.neganote.monilabs.integration.jade.provider;

import com.gregtechceu.gtceu.api.blockentity.MetaMachineBlockEntity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import static net.neganote.monilabs.common.machine.multiblock.SculkVatMachine.XP_BUFFER_MAX;

public class SculkVatXPProvider implements IBlockComponentProvider, IServerDataProvider<BlockAccessor> {

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        BlockEntity be = blockAccessor.getBlockEntity();
        if (be instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof SculkVatMachine) {
            CompoundTag data = blockAccessor.getServerData();
            if (data.contains("currentXPAmount")) {
                var xpBuffer = data.getInt("currentXPAmount");
                iTooltip.add(Component.translatable("sculk_vat.monilabs.current_xp_buffer", xpBuffer, XP_BUFFER_MAX));
            }
        }
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, BlockAccessor blockAccessor) {
        if (blockAccessor.getBlockEntity() instanceof MetaMachineBlockEntity meta_machine_be &&
                meta_machine_be.getMetaMachine() instanceof SculkVatMachine vat) {
            compoundTag.putInt("currentXPAmount", vat.getXpBuffer());
        }
    }

    @Override
    public ResourceLocation getUid() {
        return MoniLabs.id("sculk_vat_xp_info");
    }
}

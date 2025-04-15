package net.neganote.monilabs.common.cover.detector;

import com.gregtechceu.gtceu.api.capability.GTCapabilityHelper;
import com.gregtechceu.gtceu.api.capability.IControllable;
import com.gregtechceu.gtceu.api.capability.ICoverable;
import com.gregtechceu.gtceu.api.cover.CoverDefinition;
import com.gregtechceu.gtceu.common.cover.detector.DetectorCover;

import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neganote.monilabs.common.machine.multiblock.PrismaticCrucibleMachine;

import org.jetbrains.annotations.NotNull;

public class ChromaDetectorCover extends DetectorCover {

    public ChromaDetectorCover(CoverDefinition definition, ICoverable coverHolder, Direction attachedSide) {
        super(definition, coverHolder, attachedSide);
    }

    @Override
    public boolean canAttach() {
        IControllable controllable = GTCapabilityHelper.getControllable(coverHolder.getLevel(), coverHolder.getPos(),
                attachedSide);
        return super.canAttach() && controllable instanceof PrismaticCrucibleMachine;
    }

    @Override
    protected void update() {
        IControllable controllable = GTCapabilityHelper.getControllable(coverHolder.getLevel(), coverHolder.getPos(),
                attachedSide);
        if (controllable instanceof PrismaticCrucibleMachine machine) {
            setRedstoneSignalOutput(machine.isFormed() ? machine.getColorState().key + 1 : 0);
        }
    }

    @Override
    public void onAttached(@NotNull ItemStack itemStack, @NotNull ServerPlayer player) {
        super.onAttached(itemStack, player);
        IControllable controllable = GTCapabilityHelper.getControllable(coverHolder.getLevel(), coverHolder.getPos(),
                attachedSide);
        if (controllable instanceof PrismaticCrucibleMachine machine) {
            setRedstoneSignalOutput(machine.isFormed() ? machine.getColorState().key + 1 : 0);
        }
    }
}

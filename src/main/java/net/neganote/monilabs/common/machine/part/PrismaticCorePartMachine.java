package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.part.MultiblockPartMachine;

// Despite the name, this only exists for the multi to attach a renderer to it.
public class PrismaticCorePartMachine extends MultiblockPartMachine {
    public PrismaticCorePartMachine(IMachineBlockEntity holder) {
        super(holder);
    }
}

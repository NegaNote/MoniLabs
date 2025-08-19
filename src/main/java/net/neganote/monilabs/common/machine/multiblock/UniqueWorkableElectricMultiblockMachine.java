package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.RecipeLogic;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.neganote.monilabs.saveddata.UniqueMultiblockSavedData;

import org.jetbrains.annotations.NotNull;

import java.util.List;

// Copied from CosmicCore with some minor changes (thank you Caitlynn!)
public class UniqueWorkableElectricMultiblockMachine extends WorkableElectricMultiblockMachine {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            UniqueWorkableElectricMultiblockMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    public UniqueWorkableElectricMultiblockMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    // Used to make sure you cannot have more than one of this multiblock per player / team
    @Persisted
    public boolean isDuplicate = false;

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();

        if (getLevel() instanceof ServerLevel serverLevel) {
            var owner = getOwnerUUID();
            var multiblockId = getDefinition().getId().toString();
            var uniqueMultiblockMapping = UniqueMultiblockSavedData.getOrCreate(serverLevel);

            if (uniqueMultiblockMapping.hasData(owner, multiblockId)) {
                this.isDuplicate = !uniqueMultiblockMapping.isUnique(owner, multiblockId, getPos());
                if (isDuplicate) recipeLogic.setStatus(RecipeLogic.Status.SUSPEND);
            } else uniqueMultiblockMapping.addMultiblock(owner, getDefinition().getId().toString(),
                    getPos());

        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        if (getLevel() instanceof ServerLevel serverLevel) {
            var owner = getOwnerUUID();
            var uniqueMultiblockMapping = UniqueMultiblockSavedData.getOrCreate(serverLevel);
            uniqueMultiblockMapping.removeMultiblock(owner, getDefinition().getId().toString(),
                    getPos());
        }
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        if (this.isDuplicate) {
            textList.add(Component.translatable("monilabs.multiblock.duplicate.0")
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
            textList.add(Component.translatable("monilabs.multiblock.duplicate.1")
                    .setStyle(Style.EMPTY.withColor(ChatFormatting.DARK_RED)));
        } else super.addDisplayText(textList);
    }
}

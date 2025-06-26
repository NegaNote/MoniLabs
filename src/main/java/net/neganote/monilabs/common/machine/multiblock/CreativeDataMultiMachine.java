package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.capability.recipe.ItemRecipeCapability;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;
import net.neganote.monilabs.saveddata.CreativeDataAccessSavedData;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class CreativeDataMultiMachine extends UniqueWorkableElectricMultiblockMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(CreativeDataMultiMachine.class,
            UniqueWorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    private final ConditionalSubscriptionHandler creativeDataSubscription;

    @Getter
    @Persisted
    private int transcendentConversionTimer;

    private final Item crystalMatrixIngotItem;
    private final Item transMatrixIngotItem;

    private List<NotifiableItemStackHandler> inputBuses = null;
    private List<NotifiableItemStackHandler> outputBuses = null;

    public CreativeDataMultiMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);

        this.creativeDataSubscription = new ConditionalSubscriptionHandler(this, this::tickEnableCreativeData,
                this::isSubscriptionActive);
        crystalMatrixIngotItem = ForgeRegistries.ITEMS
                .getValue(ResourceLocation.of("gtceu:crystal_matrix_ingot", ':'));
        transMatrixIngotItem = ForgeRegistries.ITEMS
                .getValue(ResourceLocation.of("gtceu:transcendental_matrix_ingot", ':'));

        transcendentConversionTimer = 0;

        assert crystalMatrixIngotItem != null && transMatrixIngotItem != null;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        creativeDataSubscription.updateSubscription();
        if (inputBuses == null) {
            inputBuses = getCapabilitiesFlat(IO.IN, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
            outputBuses = getCapabilitiesFlat(IO.OUT, ItemRecipeCapability.CAP).stream()
                    .filter(NotifiableItemStackHandler.class::isInstance)
                    .map(NotifiableItemStackHandler.class::cast)
                    .toList();
        }
    }

    public void enableCreativeData(boolean enabled) {
        UUID ownerUUID = getOwnerUUID();
        if (ownerUUID == null) {
            ownerUUID = new UUID(0L, 0L);
        }
        if (getLevel() instanceof ServerLevel serverLevel) {
            CreativeDataAccessSavedData savedData = CreativeDataAccessSavedData
                    .getOrCreate(serverLevel.getServer().overworld());
            savedData.setEnabled(ownerUUID, enabled);
        }
    }

    @Override
    public void onUnload() {
        super.onUnload();
        enableCreativeData(false);
    }

    private void tickEnableCreativeData() {
        enableCreativeData(isActive() && getRecipeLogic().isWorkingEnabled());
        transcendentConversion();
    }

    private void transcendentConversion() {
        if (transcendentConversionTimer == 0 && inputBuses != null && isActive() && getRecipeLogic().isWorkingEnabled()) {
            // An unfortunate side effect of this method of checking is that the ORB will only do its thing if
            // the output has an *entire empty output slot's worth* available.
            // If you have any idea how to sidestep this, feel free to suggest something.
            if (outputBuses.stream().anyMatch(itemHandler -> itemHandler.handleRecipeInner(IO.OUT, null,
                    List.of(Ingredient.of(new ItemStack(transMatrixIngotItem, 64))), true) == null)) {
                List<Ingredient> crystalList = List.of(Ingredient.of(new ItemStack(crystalMatrixIngotItem, 64)));
                for (var inputBus : inputBuses) {
                    crystalList = inputBus.handleRecipe(IO.IN, null, crystalList, false);
                    if (crystalList == null) {
                        break;
                    }
                }

                int processed = 64;
                if (crystalList != null) {
                    for (Ingredient ingredient : crystalList) {
                        var items = ingredient.getItems();
                        for (ItemStack stack : items) {
                            processed -= stack.getCount();
                        }
                    }
                }

                List<Ingredient> transList = List.of(Ingredient.of(new ItemStack(transMatrixIngotItem, processed)));
                for (var outputBus : outputBuses) {
                    crystalList = outputBus.handleRecipe(IO.OUT, null, crystalList, false);
                    if (crystalList == null) {
                        break;
                    }
                }
            }
        }
        transcendentConversionTimer = (transcendentConversionTimer + 1) % 20;
    }

    private Boolean isSubscriptionActive() {
        return isFormed();
    }

    @Override
    public void setWorkingEnabled(boolean isWorkingAllowed) {
        super.setWorkingEnabled(isWorkingAllowed);
        if (!isWorkingAllowed) {
            enableCreativeData(false);
        }
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        enableCreativeData(false);
        creativeDataSubscription.unsubscribe();
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

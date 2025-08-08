package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;

import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.neganote.monilabs.common.machine.part.SculkExperienceDrainingHatchPartMachine;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class SculkVatMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler xpHatchSubscription;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SculkVatMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @Getter
    private int xpBuffer = 0;

    @Persisted
    @Getter
    private int timer = 0;

    public static int XP_BUFFER_MAX = FluidType.BUCKET_VOLUME << GTValues.ZPM;

    public SculkVatMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.xpHatchSubscription = new ConditionalSubscriptionHandler(this, this::xpHatchTick, this::isFormed);
    }

    private void xpHatchTick() {
        if (timer == 0 && isWorkingEnabled()) {
            var array = getParts().stream()
                    .filter(SculkExperienceDrainingHatchPartMachine.class::isInstance)
                    .map(SculkExperienceDrainingHatchPartMachine.class::cast)
                    .toArray(SculkExperienceDrainingHatchPartMachine[]::new);

            if (array.length != 1) {
                // Don't do this work if there isn't an xp hatch
                return;
            }

            var xpHatch = array[0];

            var xpTank = (NotifiableFluidTank) xpHatch.getRecipeHandlers().get(0)
                    .getCapability(FluidRecipeCapability.CAP).get(0);
            int stored = 0;
            if (!xpTank.isEmpty()) {
                stored = ((FluidStack) xpTank.getContents().get(0)).getAmount();
            }

            if (xpBuffer != 0) {
                xpBuffer -= Math.max(xpBuffer >> 5, 1);
            }

            xpBuffer = Math.min(XP_BUFFER_MAX, xpBuffer + stored);
            xpTank.setFluidInTank(0, FluidStack.EMPTY);
        }
        timer = (timer + 1) % 20;
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        xpHatchSubscription.updateSubscription();
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        xpHatchSubscription.updateSubscription();
        timer = 0;
        xpBuffer = 0;
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (!super.beforeWorking(recipe)) {
            return false;
        }
        if (recipe == null) {
            return false;
        }

        var data = recipe.data;
        if (!data.contains("minimumXp") || !data.contains("maximumXp")) {
            return true;
        }

        int minimumXp = data.getInt("minimumXp");
        int maximumXp = data.getInt("maximumXp");

        return xpBuffer >= minimumXp && xpBuffer <= maximumXp;
    }

    @Override
    public boolean onWorking() {
        var recipe = getRecipeLogic().getLastRecipe();
        if (recipe != null && recipe.data.contains("minimumXp") && recipe.data.contains("maximumXp")) {
            int minimumXp = recipe.data.getInt("minimumXp");
            int maximumXp = recipe.data.getInt("maximumXp");
            if (!(xpBuffer >= minimumXp && xpBuffer <= maximumXp)) {
                if (recipeLogic.getProgress() > 1) {
                    recipeLogic.setProgress(Math.max(1, recipeLogic.getProgress() - 2));
                }
            }
        }
        return true;
    }

    @Override
    public void addDisplayText(@NotNull List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("sculk_vat.monilabs.current_xp_buffer", xpBuffer, XP_BUFFER_MAX));
        }
    }

    @Override
    public @NotNull ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

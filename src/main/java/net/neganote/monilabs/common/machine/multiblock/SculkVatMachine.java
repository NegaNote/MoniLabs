package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.recipe.FluidRecipeCapability;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.multiblock.IFluidRenderMulti;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableFluidTank;
import com.gregtechceu.gtceu.api.pattern.util.RelativeDirection;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.common.data.GTRecipeCapabilities;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.neganote.monilabs.common.machine.part.SculkExperienceDrainingHatchPartMachine;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SculkVatMachine extends WorkableElectricMultiblockMachine implements IFluidRenderMulti {

    private final ConditionalSubscriptionHandler xpHatchSubscription;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(SculkVatMachine.class,
            WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    @Persisted
    @Getter
    private int xpBuffer = 0;

    @Persisted
    @Getter
    private int timer = 0;

    @Getter
    @Setter
    @DescSynced
    @RequireRerender
    private @NotNull Set<BlockPos> fluidBlockOffsets = new HashSet<>();

    @Getter
    private FluidStack outputTankFluid = FluidStack.EMPTY;

    private NotifiableFluidTank outputTank;

    @Getter
    private int outputTankCapacity = 0;

    @Getter
    @Persisted
    @DescSynced
    private GTRecipe lastSavedRecipe = null;

    public static int XP_BUFFER_MAX = FluidType.BUCKET_VOLUME << GTValues.ZPM;

    public SculkVatMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.xpHatchSubscription = new ConditionalSubscriptionHandler(this, this::xpHatchTick, this::isFormed);
    }

    private void xpHatchTick() {
        if (timer == 0) {
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
        outputTankFluid = outputTank.getFluidInTank(0);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        xpHatchSubscription.updateSubscription();
        IFluidRenderMulti.super.onStructureFormed();
        var tanks = getCapabilitiesFlat(IO.OUT, GTRecipeCapabilities.FLUID)
                .stream()
                .filter(NotifiableFluidTank.class::isInstance)
                .map(NotifiableFluidTank.class::cast)
                .toList();
        outputTank = tanks.get(0);
        outputTankCapacity = outputTank.getTankCapacity(0);
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        xpHatchSubscription.updateSubscription();
        timer = 0;
        xpBuffer = 0;
        IFluidRenderMulti.super.onStructureInvalid();
        lastSavedRecipe = null;
    }

    @Override
    public @NotNull Set<BlockPos> saveOffsets() {
        Direction up = RelativeDirection.UP.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction back = getFrontFacing().getOpposite();
        Direction right = RelativeDirection.RIGHT.getRelative(getFrontFacing(), getUpwardsFacing(), isFlipped());
        Direction left = RelativeDirection.LEFT.getRelative(getFrontFacing(), getUpwardsFacing(),
                isFlipped());

        BlockPos pos = getPos();

        ObjectOpenHashSet<BlockPos> offsets = new ObjectOpenHashSet<>();

        BlockPos loopPosFront = pos.relative(up).relative(back);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosFront.relative(up, i).subtract(pos));
        }

        BlockPos loopPosBack = loopPosFront.relative(back, 2);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosBack.relative(up, i).subtract(pos));
        }

        BlockPos loopPosLeft = loopPosFront.relative(back).relative(left);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosLeft.relative(up, i).subtract(pos));
        }

        BlockPos loopPosRight = loopPosFront.relative(back).relative(right);
        for (int i = 0; i < 3; i++) {
            offsets.add(loopPosRight.relative(up, i).subtract(pos));
        }

        return offsets;
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
            lastSavedRecipe = recipe;
            return true;
        }

        int minimumXp = data.getInt("minimumXp");
        int maximumXp = data.getInt("maximumXp");

        if (xpBuffer >= minimumXp && xpBuffer <= maximumXp) {
            lastSavedRecipe = recipe;
            return true;
        } else {
            return false;
        }
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
    public void afterWorking() {
        super.afterWorking();
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

package net.neganote.monilabs.common.machine.multiblock;

import com.gregtechceu.gtceu.api.machine.ConditionalSubscriptionHandler;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.MetaMachine;
import com.gregtechceu.gtceu.api.machine.multiblock.WorkableElectricMultiblockMachine;
import com.gregtechceu.gtceu.api.recipe.GTRecipe;
import com.gregtechceu.gtceu.config.ConfigHolder;

import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.neganote.monilabs.config.MoniConfig;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class VirtualParticleSynthesizerMachine extends WorkableElectricMultiblockMachine {

    private final ConditionalSubscriptionHandler quantumNoiseHandler;

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            VirtualParticleSynthesizerMachine.class, WorkableElectricMultiblockMachine.MANAGED_FIELD_HOLDER);

    // Current noise value
    @Persisted
    @DescSynced
    @Getter
    private int quantumNoise;

    // Current noise value
    @Persisted
    @DescSynced
    @Getter
    private int lastNoiseRunAt;

    public VirtualParticleSynthesizerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.quantumNoiseHandler = new ConditionalSubscriptionHandler(this, this::quantumNoiseTick, this::isFormed);
        quantumNoise = 0;
        resetNoise();
        quantumNoise = nextInt();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        quantumNoiseHandler.updateSubscription();

        resetNoise();
        quantumNoise = 0;
        tickCount = MoniConfig.INSTANCE.values.virtualParticleSynthesisRefreshDelay;
    }

    @Override
    public void onRotated(Direction oldFacing, Direction newFacing) {
        super.onRotated(oldFacing, newFacing);
    }

    @Override
    public void onStructureFormed() {
        super.onStructureFormed();
        quantumNoiseHandler.updateSubscription();
        if (offset == 0) quantumNoise = nextInt();
    }

    @Override
    public boolean beforeWorking(@Nullable GTRecipe recipe) {
        if (recipe == null) return false;

        // Special logic yadda yadda

        if(super.beforeWorking(recipe)) {
            lastNoiseRunAt = quantumNoise;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onWorking() {
        if (!super.onWorking()) {
            return false;
        }

        // Special logic yadda yadda

        return true;
    }

    @Override
    public void afterWorking() {
        super.afterWorking();

        recipeLogic.markLastRecipeDirty();
        // Special logic yadda yadda
    }

    @Persisted
    private int tickCount;

    public void quantumNoiseTick() {
        if (++tickCount >= MoniConfig.INSTANCE.values.virtualParticleSynthesisRefreshDelay) {
            quantumNoise = nextInt();
            tickCount = 0;
        }
    }

    @Override
    public void addDisplayText(List<Component> textList) {
        super.addDisplayText(textList);
        if (isFormed()) {
            textList.add(Component.translatable("virtual_particle_synthesis.monilabs.noise", quantumNoise));
            textList.add(Component.translatable(
                    "virtual_particle_synthesis.monilabs.debug.0",
                    startValue, endValue));
            if (lastNoiseRunAt >= 0) {
                textList.add(Component.translatable("virtual_particle_synthesis.monilabs.lastrun", lastNoiseRunAt));
            }
        }
    }

    // Noise Manager inside the class because fuck me I guess...

    @Persisted
    private int startValue;
    @Persisted
    private int endValue;
    @Persisted
    private int targetOffset;

    @Persisted
    private double offset = 0;
    @Persisted
    private byte arrayIndex;
    @Persisted
    private final int[] queue = new int[64];

    private int nextInt() {
        offset += (0.1 + Math.random() * 0.1);
        if (offset >= 1) {
            offset = offset - 1;
            startValue = endValue;
            endValue = randomCall();

            // Take the shortest path
            targetOffset = ((endValue-startValue) > 8 ? -(startValue-endValue) : endValue-startValue);
        }
        // Approach end
        double v = startValue + smoothStep(offset) * targetOffset + 0.5;
        v = (v>=16 ? v-16 : (v<0 ? v+16 : v));

        return (int) Math.floor(v);
    }

    /*
     * Gets the next element of the queue. If you reach the end of the queue, generate a new queue.
     */
    private int randomCall() {
        arrayIndex--;
        if(arrayIndex < 0) {
            arrayIndex = 63;

            // Populate and shuffle an array in the same loop.
            // The array contains 4x each number from 0 to 15.
            Random rand = new Random();
            for (int i = 0; i < 64; i++) {
                int index = rand.nextInt(i + 1);
                // Combining swapping and populating the array
                queue[i] = queue[index];
                queue[index] = i % 16;
            }
        }

        return queue[arrayIndex];
    }

    private double smoothStep(double x) {
        x = Mth.clamp(x, 0F, 1F);
        return 3 * Math.pow(x, 2) - 2 * Math.pow(x, 3);     // 3x^2-2x^3
    }

    private void resetNoise() {
        startValue = randomCall();
        targetOffset = randomCall();
        arrayIndex = 0;
        offset = 0;
        lastNoiseRunAt = -1;
    }
}

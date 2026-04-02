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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neganote.monilabs.config.MoniConfig;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

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

        return super.beforeWorking(recipe);
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
        }
    }

    public void explode(float explosionPower) {
        BlockPos pos = this.getPos();
        MetaMachine machine = this.self();
        Level level = machine.getLevel();
        level.removeBlock(pos, false);
        level.explode((Entity) null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5,
                explosionPower, ConfigHolder.INSTANCE.machines.doesExplosionDamagesTerrain ?
                        Level.ExplosionInteraction.BLOCK : Level.ExplosionInteraction.NONE);
    }

    // Noise Manager inside the class because fuck me I guess...

    @Persisted
    private double startValue;
    @Persisted
    private double endValue;

    @Persisted
    private double offset = 0;
    @Persisted
    private int counter0 = 0;
    @Persisted
    private int counter15 = 0;

    private int nextInt() {
        offset += (0.1 + Math.random() * 0.05);
        if (offset >= 1) {
            offset = offset - 1;
            startValue = endValue;
            endValue = randomCall();
        }
        double v = startValue + smoothStep(offset) * (endValue - startValue);

        return (int) Math.floor(v);
    }

    /*
     * A random method with slight weight towards extremes to ensure a uniform distribution after interpolation.
     * Takes a random number r, and returns (3.5*smoothStep(r) + r) / 4.5
     * Also contains bad luck protection (in the 0.0012% of cases where you're *really* unlucky)
     */
    private double randomCall() {
        double rand = Math.random();
        rand = 16 * ((smoothStep(rand) * 3.5 + rand) / 4.5);

        if (counter15 >= 75) {
            rand = 15.5;
            counter15 = 0;
        } else if (counter0 >= 75) {
            rand = 0.5;
            counter0 = 0;
        }

        if (rand >= 15) {
            counter15 = 0;
        } else {
            counter15++;
        }
        if (rand < 1) {
            counter0 = 0;
        } else {
            counter0++;
        }

        return rand;
    }

    private double smoothStep(double x) {
        x = Mth.clamp(x, 0F, 1F);
        return 3 * Math.pow(x, 2) - 2 * Math.pow(x, 3);     // 3x^2-2x^3
    }

    private void resetNoise() {
        counter0 = 0;
        counter15 = 0;
        startValue = randomCall();
        endValue = randomCall();
        offset = 0;
    }
}

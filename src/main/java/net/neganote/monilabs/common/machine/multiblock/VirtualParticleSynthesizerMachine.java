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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.neganote.monilabs.config.MoniConfig;
import net.neganote.monilabs.utils.QuantumNoiseManager;

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

    @Persisted
    private final QuantumNoiseManager noiseManager;

    // Current noise value
    @Persisted
    @DescSynced
    @Getter
    private int quantumNoise;

    public VirtualParticleSynthesizerMachine(IMachineBlockEntity holder, Object... args) {
        super(holder, args);
        this.quantumNoiseHandler = new ConditionalSubscriptionHandler(this, this::quantumNoiseTick, this::isFormed);
        this.noiseManager = new QuantumNoiseManager();
        quantumNoise = 0;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    public void onStructureInvalid() {
        super.onStructureInvalid();
        quantumNoiseHandler.updateSubscription();

        noiseManager.resetNoise();
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

        // Special logic yadda yadda
    }

    @Persisted
    private int tickCount;

    public void quantumNoiseTick() {
        if (++tickCount >= MoniConfig.INSTANCE.values.virtualParticleSynthesisRefreshDelay) {
            quantumNoise = noiseManager.nextInt();
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
}

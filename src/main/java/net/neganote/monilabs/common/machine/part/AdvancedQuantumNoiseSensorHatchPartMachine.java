package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.monilabs.common.machine.multiblock.VirtualParticleSynthesizerMachine;

import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedQuantumNoiseSensorHatchPartMachine extends QuantumNoiseSensorHatchPartMachine
                                                        implements IFancyUIMachine {

    @Setter
    @Getter
    @Persisted
    @DescSynced
    public int targetNoise;

    @Setter
    @Getter
    @Persisted
    @DescSynced
    public boolean inverted = false;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedQuantumNoiseSensorHatchPartMachine.class,
            QuantumNoiseSensorHatchPartMachine.MANAGED_FIELD_HOLDER);

    public AdvancedQuantumNoiseSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var controller = (VirtualParticleSynthesizerMachine) getController();
            if (controller == null) {
                return 0;
            }
            int actualNoise = controller.getQuantumNoise();
            if (inverted) {
                return actualNoise == targetNoise ? 0 : 15;
            } else {
                return actualNoise == targetNoise ? 15 : 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 170, 85);

        group.addWidget(new ToggleButtonWidget(
                10, 5, 18, 18,
                GuiTextures.INVERT_REDSTONE_BUTTON, this::isInverted, this::setInverted)
                .isMultiLang()
                .setTooltipText("gui.monilabs.quantum_noise_sensor.invert"));

        group.addWidget(new LabelWidget(10, 35, "gui.monilabs.quantum_noise.label"));
        group.addWidget(new IntInputWidget(45, 30, 100, 20,
                () -> targetNoise,
                val -> targetNoise = Mth.clamp(val, 0, 15))
                .setHoverTooltips("gui.monilabs.quantum_noise.target"));

        return group;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

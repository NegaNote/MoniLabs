package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedMicroverseStabilitySensorHatchPartMachine extends MicroverseStabilitySensorHatchPartMachine {

    public static int DEFAULT_MIN_PERCENT = 33;
    public static int DEFAULT_MAX_PERCENT = 66;

    @Persisted
    @Setter
    @Getter
    public int minPercent, maxPercent;

    @Persisted
    @Setter
    @Getter
    public boolean inverted;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedMicroverseStabilitySensorHatchPartMachine.class,
            MicroverseStabilitySensorHatchPartMachine.MANAGED_FIELD_HOLDER);

    public AdvancedMicroverseStabilitySensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
        minPercent = DEFAULT_MIN_PERCENT;
        maxPercent = DEFAULT_MAX_PERCENT;
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction != getFrontFacing().getOpposite()) {
            return 0;
        }

        var controller = (MicroverseProjectorMachine) getController();
        if (controller == null) {
            return 0;
        }

        var actualStability = controller.getMicroverseIntegrity();

        var minStability = minPercent * 100;
        var maxStability = maxPercent * 100;

        if (inverted) {
            if (actualStability >= minStability && actualStability <= maxStability) {
                return 0;
            } else {
                return 15;
            }
        } else {
            if (actualStability >= minStability && actualStability <= maxStability) {
                return 15;
            } else {
                return 0;
            }
        }
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 170, 85);

        group.addWidget(new ToggleButtonWidget(
                10, 5, 18, 18,
                GuiTextures.INVERT_REDSTONE_BUTTON, this::isInverted, this::setInverted)
                .isMultiLang()
                .setTooltipText("gui.monilabs.microverse_stability_sensor.invert"));

        group.addWidget(new LabelWidget(10, 35, "gui.monilabs.microverse_stability.min"));
        group.addWidget(new IntInputWidget(45, 30, 100, 20,
                () -> minPercent,
                val -> {
                    minPercent = Mth.clamp(val, 0, 100);
                })
                .setHoverTooltips("gui.monilabs.microverse_stability.min_threshold"));

        group.addWidget(new LabelWidget(10, 60, "gui.monilabs.microverse_stability.max"));
        group.addWidget(new IntInputWidget(45, 55, 100, 20,
                () -> maxPercent,
                val -> maxPercent = Mth.clamp(val, 0, 100))
                .setHoverTooltips("gui.monilabs.microverse_stability.max_threshold"));

        return group;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

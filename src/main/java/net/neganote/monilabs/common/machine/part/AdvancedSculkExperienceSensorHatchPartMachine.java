package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.monilabs.client.gui.MoniGuiTextures;
import net.neganote.monilabs.common.machine.multiblock.SculkVatMachine;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AdvancedSculkExperienceSensorHatchPartMachine extends SculkExperienceSensorHatchPartMachine {

    @Persisted
    @Setter
    @Getter
    @DescSynced
    public int minPercent = 33, maxPercent = 66;

    @Persisted
    @Setter
    @Getter
    @DescSynced
    public int minValue = 1000, maxValue = 10000;

    @Persisted
    @Setter
    @Getter
    @DescSynced
    public boolean inverted;

    @Persisted
    @Setter
    @Getter
    @DescSynced
    public boolean usesPercent = true;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedSculkExperienceSensorHatchPartMachine.class,
            SculkExperienceSensorHatchPartMachine.MANAGED_FIELD_HOLDER);

    public AdvancedSculkExperienceSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction != getFrontFacing().getOpposite()) return 0;

        var controller = (SculkVatMachine) getController();
        if (controller == null) return 0;

        int currentXP = controller.getXpBuffer();
        int min, max;

        if (usesPercent) {
            min = (int) (SculkVatMachine.XP_BUFFER_MAX * (minPercent / 100.0));
            max = (int) (SculkVatMachine.XP_BUFFER_MAX * (maxPercent / 100.0));
        } else {
            min = minValue;
            max = maxValue;
        }

        boolean inRange = (currentXP >= min && currentXP <= max);
        return (inverted != inRange) ? 15 : 0;
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 170, 85);

        group.addWidget(new ToggleButtonWidget(
                10, 5, 18, 18,
                GuiTextures.INVERT_REDSTONE_BUTTON, this::isInverted, this::setInverted)
                .isMultiLang()
                .setTooltipText("gui.monilabs.xp_sensor.invert"));

        group.addWidget(new ToggleButtonWidget(30, 5, 18, 18,
                MoniGuiTextures.XP_SENSOR_BUTTON, this::isUsesPercent, this::setUsesPercent)
                .isMultiLang()
                .setTooltipText("gui.monilabs.xp_sensor.mode_toggle"));

        group.addWidget(new LabelWidget(55, 12, () -> usesPercent ? "gui.monilabs.xp_sensor.mode_percentage" :
                "gui.monilabs.xp_sensor.mode_raw_amount"));

        group.addWidget(new LabelWidget(10, 35, "gui.monilabs.xp_sensor.min"));
        group.addWidget(new IntInputWidget(45, 30, 100, 20,
                () -> usesPercent ? minPercent : minValue,
                val -> {
                    if (usesPercent) minPercent = Mth.clamp(val, 0, 100);
                    else minValue = Mth.clamp(val, 0, SculkVatMachine.XP_BUFFER_MAX);
                })
                .setHoverTooltips("gui.monilabs.xp_sensor.min_threshold"));

        group.addWidget(new LabelWidget(10, 60, "gui.monilabs.xp_sensor.max"));
        group.addWidget(new IntInputWidget(45, 55, 100, 20,
                () -> usesPercent ? maxPercent : maxValue,
                val -> {
                    if (usesPercent) maxPercent = Mth.clamp(val, 0, 100);
                    else maxValue = Mth.clamp(val, 0, SculkVatMachine.XP_BUFFER_MAX);
                })
                .setHoverTooltips("gui.monilabs.xp_sensor.max_threshold"));

        return group;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

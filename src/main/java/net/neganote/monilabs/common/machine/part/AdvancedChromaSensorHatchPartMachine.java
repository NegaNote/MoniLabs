package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.IntInputWidget;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;

import com.lowdragmc.lowdraglib.gui.editor.ColorPattern;
import com.lowdragmc.lowdraglib.gui.factory.HeldItemUIFactory;
import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import com.lowdragmc.lowdraglib.utils.LocalizationUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.monilabs.common.machine.multiblock.Color;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;
import java.util.List;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedChromaSensorHatchPartMachine extends ChromaSensorHatchPartMachine {

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedChromaSensorHatchPartMachine.class,
            ChromaSensorHatchPartMachine.MANAGED_FIELD_HOLDER);

    @Setter
    @Getter
    @Persisted
    @DescSynced
    public Color detectorColor = Color.RED;

    @Setter
    @Getter
    @Persisted
    @DescSynced
    public boolean inverted = false;

    private static final List<String> COLOR_NAMES = Arrays.stream(Color.values())
            .map(Enum::name)
            .toList();

    private static final it.unimi.dsi.fastutil.objects.Object2ObjectMap<String, Color> NAME_TO_COLOR =
            new it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap<>();

    static {
        for (Color color : Color.values()) {
            NAME_TO_COLOR.put(color.name(), color);
        }
    }

    public AdvancedChromaSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    @Override
    public int getOutputSignal(Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var prismacColor = getPrismacColor();
            if (prismacColor == null) {
                return 0;
            }
            if (inverted) {
                return prismacColor == detectorColor ? 0 : 15;
            } else {
                return prismacColor == detectorColor ? 15 : 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 70, 70);

        // Selection Widget
        group.addWidget(new LabelWidget(5, 5, "Color:"));

        group.addWidget(new SelectorWidget(
                15, 10, 80, 20,
                COLOR_NAMES,
                COLOR_NAMES.indexOf(getDetectorColor().name()))
                .setOnChanged(selectedName -> {
                    // Safe retrieval using the fastutil map
                    Color newColor = NAME_TO_COLOR.getOrDefault(selectedName, Color.RED);
                    setDetectorColor(newColor);
                })
                .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                .setBackground(ColorPattern.BLACK.rectTexture())
                .setSupplier(() -> getDetectorColor().name()));

        // Invert Redstone Output Toggle:
        group.addWidget(new ToggleButtonWidget(
                9, 55, 20, 20,
                GuiTextures.INVERT_REDSTONE_BUTTON, this::isInverted, this::setInverted)
                .setTooltipText("gtceu.gui.button.invert_redstone"));

        return group;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

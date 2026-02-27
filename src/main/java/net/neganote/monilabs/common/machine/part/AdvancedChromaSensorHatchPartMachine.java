package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;

import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.*;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.monilabs.common.machine.multiblock.Color;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.neganote.monilabs.common.machine.multiblock.Color.ACTUAL_COLORS;
import static net.neganote.monilabs.common.machine.multiblock.Color.ACTUAL_COLOR_COUNT;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedChromaSensorHatchPartMachine extends ChromaSensorHatchPartMachine
                                                  implements IFancyUIMachine {

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

    public static final Object2ObjectMap<String, Color> NAME_TO_COLOR = new Object2ObjectOpenHashMap<>();
    public static final Object2ObjectMap<Color, String> COLOR_TO_NAME = new Object2ObjectOpenHashMap<>();

    private static final List<String> ACTUAL_COLOR_DISPLAY_NAMES = Arrays.stream(ACTUAL_COLORS)
            .map(Color::getColoredDisplayName)
            .toList();

    static {
        for (int i = 0; i < ACTUAL_COLOR_COUNT; i++) {
            NAME_TO_COLOR.put(ACTUAL_COLOR_DISPLAY_NAMES.get(i), ACTUAL_COLORS[i]);
            COLOR_TO_NAME.put(ACTUAL_COLORS[i], ACTUAL_COLOR_DISPLAY_NAMES.get(i));
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

        group.addWidget(new LabelWidget(-40, 15, "gui.monilabs.chroma.color.display"));

        group.addWidget(new SelectorWidget(
                -5, 11, 80, 20,
                ACTUAL_COLOR_DISPLAY_NAMES,
                0)
                .setMaxCount(3)
                .setOnChanged(selectedName -> setDetectorColor(NAME_TO_COLOR.get(selectedName)))
                .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                .setSupplier(() -> COLOR_TO_NAME.get(getDetectorColor())));

        group.addWidget(new ToggleButtonWidget(
                80, 11, 20, 20,
                GuiTextures.INVERT_REDSTONE_BUTTON, this::isInverted, this::setInverted)
                .isMultiLang()
                .setTooltipText("gui.advanced_chroma_sensor.invert"));

        return group;
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }
}

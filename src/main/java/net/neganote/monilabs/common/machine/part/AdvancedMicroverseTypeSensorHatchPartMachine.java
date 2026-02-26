package net.neganote.monilabs.common.machine.part;

import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.widget.ToggleButtonWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;

import com.lowdragmc.lowdraglib.gui.texture.ResourceBorderTexture;
import com.lowdragmc.lowdraglib.gui.widget.LabelWidget;
import com.lowdragmc.lowdraglib.gui.widget.SelectorWidget;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;

import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.neganote.monilabs.common.machine.multiblock.Microverse;
import net.neganote.monilabs.common.machine.multiblock.MicroverseProjectorMachine;

import com.mojang.blaze3d.MethodsReturnNonnullByDefault;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import static net.neganote.monilabs.common.machine.multiblock.Microverse.ACTUAL_MICROVERSES;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class AdvancedMicroverseTypeSensorHatchPartMachine extends MicroverseTypeSensorHatchPartMachine
                                                          implements IFancyUIMachine {

    @Setter
    @Getter
    @Persisted
    @DescSynced
    public Microverse detectorMicroverse = Microverse.NONE;

    @Setter
    @Getter
    @Persisted
    @DescSynced
    public boolean inverted;

    public static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(
            AdvancedMicroverseTypeSensorHatchPartMachine.class,
            MicroverseTypeSensorHatchPartMachine.MANAGED_FIELD_HOLDER);

    public AdvancedMicroverseTypeSensorHatchPartMachine(IMachineBlockEntity holder) {
        super(holder);
    }

    @Override
    public boolean shouldOpenUI(Player player, InteractionHand hand, BlockHitResult hit) {
        return true;
    }

    public static final Object2ObjectMap<String, Microverse> MICROVERSE_NAME = new Object2ObjectOpenHashMap<>();

    static {
        for (Microverse microverse : Microverse.values()) {
            MICROVERSE_NAME.put(microverse.name(), microverse);
        }
    }

    private static final List<Microverse> VALID_TYPES = Arrays.asList(ACTUAL_MICROVERSES);

    // Now uses getDisplayName() for plain white text
    private static final List<String> ACTUAL_MICROVERSE_DISPLAY_NAMES = VALID_TYPES.stream()
            .map(Microverse::getDisplayName)
            .toList();

    @Override
    public int getOutputSignal(@Nullable Direction direction) {
        if (direction == getFrontFacing().getOpposite()) {
            var controller = (MicroverseProjectorMachine) getController();

            if (controller == null) {
                return 0;
            }

            var projectorMicroverse = controller.getMicroverse();
            if (inverted) {
                return projectorMicroverse == detectorMicroverse ? 0 : 15;
            } else {
                return projectorMicroverse == detectorMicroverse ? 15 : 0;
            }
        } else {
            return 0;
        }
    }

    @Override
    public Widget createUIWidget() {
        WidgetGroup group = new WidgetGroup(0, 0, 70, 70);
        group.addWidget(new LabelWidget(-40, 15, "gui.advanced_chroma_sensor.display"));

        group.addWidget(new SelectorWidget(
                -5, 11, 80, 20,
                ACTUAL_MICROVERSE_DISPLAY_NAMES,
                VALID_TYPES.indexOf(getDetectorMicroverse()))
                .setMaxCount(3)
                .setOnChanged(selectedName -> {
                    int index = ACTUAL_MICROVERSE_DISPLAY_NAMES.indexOf(selectedName);
                    if (index >= 0) {
                        setDetectorMicroverse(VALID_TYPES.get(index));
                    }
                })
                .setButtonBackground(ResourceBorderTexture.BUTTON_COMMON)
                .setSupplier(() -> {
                    int idx = VALID_TYPES.indexOf(getDetectorMicroverse());
                    return idx >= 0 ? ACTUAL_MICROVERSE_DISPLAY_NAMES.get(idx) : "gui.advanced_chroma_sensor.none";
                }));

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

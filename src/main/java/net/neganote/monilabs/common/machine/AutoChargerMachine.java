package net.neganote.monilabs.common.machine;

import com.gregtechceu.gtceu.api.GTValues;
import com.gregtechceu.gtceu.api.capability.*;
import com.gregtechceu.gtceu.api.capability.compat.FeCompat;
import com.gregtechceu.gtceu.api.capability.recipe.IO;
import com.gregtechceu.gtceu.api.gui.GuiTextures;
import com.gregtechceu.gtceu.api.gui.fancy.ConfiguratorPanel;
import com.gregtechceu.gtceu.api.gui.fancy.IFancyConfiguratorButton;
import com.gregtechceu.gtceu.api.gui.widget.SlotWidget;
import com.gregtechceu.gtceu.api.machine.IMachineBlockEntity;
import com.gregtechceu.gtceu.api.machine.TickableSubscription;
import com.gregtechceu.gtceu.api.machine.TieredEnergyMachine;
import com.gregtechceu.gtceu.api.machine.feature.IAutoOutputItem;
import com.gregtechceu.gtceu.api.machine.feature.IFancyUIMachine;
import com.gregtechceu.gtceu.api.machine.feature.IMachineLife;
import com.gregtechceu.gtceu.api.machine.property.GTMachineModelProperties;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableEnergyContainer;
import com.gregtechceu.gtceu.api.machine.trait.NotifiableItemStackHandler;
import com.gregtechceu.gtceu.common.machine.electric.ChargerMachine;
import com.gregtechceu.gtceu.config.ConfigHolder;
import com.gregtechceu.gtceu.utils.GTTransferUtils;
import com.gregtechceu.gtceu.utils.GTUtil;

import com.lowdragmc.lowdraglib.gui.texture.GuiTextureGroup;
import com.lowdragmc.lowdraglib.gui.widget.Widget;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;
import com.lowdragmc.lowdraglib.syncdata.annotation.DescSynced;
import com.lowdragmc.lowdraglib.syncdata.annotation.Persisted;
import com.lowdragmc.lowdraglib.syncdata.annotation.RequireRerender;
import com.lowdragmc.lowdraglib.syncdata.field.ManagedFieldHolder;
import com.lowdragmc.lowdraglib.utils.Position;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.ItemHandlerHelper;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class AutoChargerMachine extends TieredEnergyMachine
                                implements IFancyUIMachine, IMachineLife, IAutoOutputItem {

    protected static final ManagedFieldHolder MANAGED_FIELD_HOLDER = new ManagedFieldHolder(AutoChargerMachine.class,
            TieredEnergyMachine.MANAGED_FIELD_HOLDER);

    public static final long AMPS_PER_ITEM = 4L;
    private final int inventorySize;

    @Persisted
    private boolean isWorkingEnabled = true;

    @Persisted
    protected final NotifiableItemStackHandler chargerInventory;

    @DescSynced
    @RequireRerender
    private ChargerMachine.State state = ChargerMachine.State.IDLE;

    @Persisted
    @DescSynced
    @RequireRerender
    protected Direction outputFacingItems;

    @Persisted
    @DescSynced
    @RequireRerender
    protected boolean autoOutputItems;

    @Persisted
    protected boolean allowInputFromOutputSideItems;

    @Nullable
    protected TickableSubscription autoOutputSubs;

    public AutoChargerMachine(IMachineBlockEntity holder, int tier, int inventorySize) {
        super(holder, tier, inventorySize);

        this.chargerInventory = new NotifiableItemStackHandler(this, inventorySize, IO.BOTH);
        this.chargerInventory.setFilter(stack -> GTCapabilityHelper.getElectricItem(stack) != null ||
                (ConfigHolder.INSTANCE.compat.energy.nativeEUToFE &&
                        GTCapabilityHelper.getForgeEnergyItem(stack) != null));
        this.inventorySize = inventorySize;

        this.outputFacingItems = getFrontFacing().getOpposite();
    }

    @Override
    public ManagedFieldHolder getFieldHolder() {
        return MANAGED_FIELD_HOLDER;
    }

    @Override
    protected NotifiableEnergyContainer createEnergyContainer(Object... args) {
        return new EnergyBatteryTrait((int) args[0]);
    }

    private void changeState(ChargerMachine.State newState) {
        if (this.state != newState) {
            this.state = newState;
            setRenderState(getRenderState().setValue(GTMachineModelProperties.CHARGER_STATE, newState));
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (!isRemote()) {
            updateAutoOutputSubscription();
            this.chargerInventory.addChangedListener(this::updateAutoOutputSubscription);
        }
    }

    protected void updateAutoOutputSubscription() {
        if (isAutoOutputItems() && !chargerInventory.isEmpty()) {
            autoOutputSubs = subscribeServerTick(autoOutputSubs, this::autoOutput);
        } else if (autoOutputSubs != null) {
            autoOutputSubs.unsubscribe();
            autoOutputSubs = null;
        }
    }

    protected void autoOutput() {
        if (getOffsetTimer() % 5 == 0 && isAutoOutputItems()) {
            boolean exported = false;
            for (int i = 0; i < chargerInventory.getSlots(); i++) {
                ItemStack stack = chargerInventory.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                if (isFullyCharged(stack)) {
                    final int slotIndex = i;
                    var handlerCap = GTTransferUtils.getItemHandler(getLevel(), getPos().relative(outputFacingItems),
                            outputFacingItems.getOpposite());

                    handlerCap.ifPresent(handler -> {
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                        chargerInventory.setStackInSlot(slotIndex, remainder);
                    });

                    if (chargerInventory.getStackInSlot(slotIndex).getCount() != stack.getCount()) {
                        exported = true;
                    }
                }
            }
            if (exported) markDirty();
        }
    }

    @Override
    public void attachConfigurators(ConfiguratorPanel panel) {
        IFancyUIMachine.super.attachConfigurators(panel);

        panel.attachConfigurators(new IFancyConfiguratorButton.Toggle(
                new GuiTextureGroup(
                        GuiTextures.TOGGLE_BUTTON_BACK.getSubTexture(0, 0, 1, 0.5f),
                        GuiTextures.IO_CONFIG_ITEM_MODES_BUTTON.getSubTexture(0, 1f / 3f, 1, 1f / 3f)),
                new GuiTextureGroup(
                        GuiTextures.TOGGLE_BUTTON_BACK.getSubTexture(0, 0.5f, 1, 0.5f),
                        GuiTextures.IO_CONFIG_ITEM_MODES_BUTTON.getSubTexture(0, 2f / 3f, 1, 1f / 3f)),
                this::isAutoOutputItems,
                (cd, next) -> setAutoOutputItems(next)));
    }

    private boolean isFullyCharged(ItemStack stack) {
        var elec = GTCapabilityHelper.getElectricItem(stack);
        if (elec != null) return elec.getCharge() >= elec.getMaxCharge();

        var fe = GTCapabilityHelper.getForgeEnergyItem(stack);
        if (fe != null) return fe.getEnergyStored() >= fe.getMaxEnergyStored();

        return false;
    }

    @Override
    public boolean isAutoOutputItems() {
        return this.autoOutputItems;
    }

    @Override
    public void setAutoOutputItems(boolean allow) {
        this.autoOutputItems = allow;
        updateAutoOutputSubscription();
    }

    @Override
    public boolean isAllowInputFromOutputSideItems() {
        return this.allowInputFromOutputSideItems;
    }

    @Override
    public void setAllowInputFromOutputSideItems(boolean b) {
        this.allowInputFromOutputSideItems = b;
    }

    @Override
    public Direction getOutputFacingItems() {
        return outputFacingItems;
    }

    @Override
    public void setOutputFacingItems(@Nullable Direction direction) {
        this.outputFacingItems = direction == null ? getFrontFacing().getOpposite() : direction;
        updateAutoOutputSubscription();
    }

    @Override
    public Widget createUIWidget() {
        int rowSize = (int) Math.sqrt(inventorySize);
        int colSize = rowSize;
        if (inventorySize == 8) {
            rowSize = 4;
            colSize = 2;
        }
        var template = new WidgetGroup(0, 0, 18 * rowSize + 8, 18 * colSize + 8);
        template.setBackground(GuiTextures.BACKGROUND_INVERSE);
        int index = 0;
        for (int y = 0; y < colSize; y++) {
            for (int x = 0; x < rowSize; x++) {
                template.addWidget(new SlotWidget(chargerInventory, index++, 4 + x * 18, 4 + y * 18, true, true)
                        .setBackgroundTexture(new GuiTextureGroup(GuiTextures.SLOT, GuiTextures.CHARGER_OVERLAY)));
            }
        }

        var editableUI = createEnergyBar();
        var energyBar = editableUI.createDefault();

        var group = new WidgetGroup(0, 0,
                Math.max(energyBar.getSize().width + template.getSize().width + 4 + 8, 172),
                Math.max(template.getSize().height + 8, energyBar.getSize().height + 8));
        var size = group.getSize();
        energyBar.setSelfPosition(new Position(3, (size.height - energyBar.getSize().height) / 2));
        template.setSelfPosition(new Position(
                (size.width - energyBar.getSize().width - 4 - template.getSize().width) / 2 + 2 +
                        energyBar.getSize().width + 2,
                (size.height - template.getSize().height) / 2));
        group.addWidget(energyBar);
        group.addWidget(template);
        editableUI.setupUI(group, this);
        return group;
    }

    protected class EnergyBatteryTrait extends NotifiableEnergyContainer {

        protected EnergyBatteryTrait(int invSize) {
            super(
                    AutoChargerMachine.this,
                    GTValues.V[tier] * invSize * 32L,
                    GTValues.V[tier],
                    invSize * AMPS_PER_ITEM,
                    0L,
                    0L);
            setSideInputCondition(side -> isWorkingEnabled);
            setSideOutputCondition(side -> false);
        }

        private List<Object> getNonFullElectricItem() {
            List<Object> electricItems = new ArrayList<>();
            for (int i = 0; i < chargerInventory.getSlots(); i++) {
                var electricItemStack = chargerInventory.getStackInSlot(i);
                var electricItem = GTCapabilityHelper.getElectricItem(electricItemStack);
                if (electricItem != null) {
                    if (electricItem.getCharge() < electricItem.getMaxCharge()) {
                        electricItems.add(electricItem);
                    }
                } else if (ConfigHolder.INSTANCE.compat.energy.nativeEUToFE) {
                    var energyStorage = GTCapabilityHelper.getForgeEnergyItem(electricItemStack);
                    if (energyStorage != null) {
                        if (energyStorage.getEnergyStored() < energyStorage.getMaxEnergyStored()) {
                            electricItems.add(energyStorage);
                        }
                    }
                }
            }
            return electricItems;
        }

        private void changeState(ChargerMachine.State newState) {
            if (state != newState) {
                state = newState;
                setRenderState(getRenderState().setValue(GTMachineModelProperties.CHARGER_STATE, newState));
            }
        }

        @Override
        public long acceptEnergyFromNetwork(@Nullable Direction side, long voltage, long amperage) {
            var latestTimeStamp = getMachine().getOffsetTimer();
            if (lastTimeStamp < latestTimeStamp) {
                amps = 0;
                lastTimeStamp = latestTimeStamp;
            }
            if (amperage <= 0 || voltage <= 0) {
                changeState(ChargerMachine.State.IDLE);
                return 0;
            }

            var electricItems = getNonFullElectricItem();
            var maxAmps = electricItems.size() * AMPS_PER_ITEM - amps;
            var usedAmps = Math.min(maxAmps, amperage);
            if (maxAmps <= 0) {
                return 0;
            }

            if (side == null || inputsEnergy(side)) {
                if (voltage > getInputVoltage()) {
                    doExplosion(GTUtil.getExplosionPower(voltage));
                    return usedAmps;
                }

                long internalAmps = Math.min(maxAmps, Math.max(0, getInternalStorage() / voltage));

                usedAmps = Math.min(usedAmps, maxAmps - internalAmps);
                amps += usedAmps;

                long energy = (usedAmps + internalAmps) * voltage;
                long distributed = energy / electricItems.size();

                boolean changed = false;
                for (var electricItem : electricItems) {
                    long charged = 0;
                    if (electricItem instanceof IElectricItem item) {
                        charged = item.charge(Math.min(distributed, GTValues.V[item.getTier()] * AMPS_PER_ITEM),
                                getTier(), true, false);
                    } else if (electricItem instanceof IEnergyStorage energyStorage) {
                        charged = FeCompat.insertEu(energyStorage,
                                Math.min(distributed, GTValues.V[getTier()] * AMPS_PER_ITEM), false);
                    }
                    if (charged > 0) {
                        changed = true;
                    }
                    energy -= charged;
                    energyInputPerSec += charged;
                }

                if (changed) {
                    AutoChargerMachine.this.markDirty();
                    changeState(ChargerMachine.State.RUNNING);
                }

                setEnergyStored(getInternalStorage() - internalAmps * voltage + energy);
                return usedAmps;
            }
            return 0;
        }

        @Override
        public long getEnergyCapacity() {
            long energyCapacity = 0L;
            for (int i = 0; i < chargerInventory.getSlots(); i++) {
                var electricItemStack = chargerInventory.getStackInSlot(i);
                var electricItem = GTCapabilityHelper.getElectricItem(electricItemStack);
                if (electricItem != null) {
                    energyCapacity += electricItem.getMaxCharge();
                } else if (ConfigHolder.INSTANCE.compat.energy.nativeEUToFE) {
                    var energyStorage = GTCapabilityHelper.getForgeEnergyItem(electricItemStack);
                    if (energyStorage != null) {
                        energyCapacity += FeCompat.toEu(energyStorage.getMaxEnergyStored(),
                                FeCompat.ratio(false));
                    }
                }
            }

            if (energyCapacity == 0) {
                changeState(ChargerMachine.State.IDLE);
            }

            return energyCapacity;
        }

        @Override
        public long getEnergyStored() {
            long energyStored = 0L;
            for (int i = 0; i < chargerInventory.getSlots(); i++) {
                var electricItemStack = chargerInventory.getStackInSlot(i);
                var electricItem = GTCapabilityHelper.getElectricItem(electricItemStack);
                if (electricItem != null) {
                    energyStored += electricItem.getCharge();
                } else if (ConfigHolder.INSTANCE.compat.energy.nativeEUToFE) {
                    var energyStorage = GTCapabilityHelper.getForgeEnergyItem(electricItemStack);
                    if (energyStorage != null) {
                        energyStored += FeCompat.toEu(energyStorage.getEnergyStored(),
                                FeCompat.ratio(false));
                    }
                }
            }

            var capacity = getEnergyCapacity();

            if (capacity != 0 && capacity == energyStored) {
                changeState(ChargerMachine.State.FINISHED);
            }

            return energyStored;
        }

        private long getInternalStorage() {
            return energyStored;
        }
    }
}

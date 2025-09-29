package net.neganote.monilabs.integration.fancymenu;

import com.gregtechceu.gtceu.GTCEu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.keksuccino.fancymenu.customization.action.ActionRegistry;

@SideOnly(Side.CLIENT)
public class ActionRegister {

    // Registers FancyMenu action
    public static void init() {
        if (GTCEu.isClientSide()) {
            ActionRegistry.register(new PackSwitcherAction());
            ActionRegistry.register(new SaveTmpModeFileAction());
        }
    }
}

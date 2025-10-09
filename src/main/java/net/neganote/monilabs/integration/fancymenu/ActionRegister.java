package net.neganote.monilabs.integration.fancymenu;

import de.keksuccino.fancymenu.customization.action.ActionRegistry;

public class ActionRegister {

    // Registers FancyMenu action
    public static void init() {
        ActionRegistry.register(new PackSwitcherAction());
        ActionRegistry.register(new SaveTmpModeFileAction());
    }
}

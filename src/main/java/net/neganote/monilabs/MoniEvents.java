package net.neganote.monilabs;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.neganote.monilabs.commands.PackSwitcherCommands;

@Mod.EventBusSubscriber(modid = MoniLabs.MOD_ID)
public class MoniEvents {

    @SubscribeEvent
    public static void RegisterCommandsEvent(RegisterCommandsEvent event) {
        MoniLabs.LOGGER.debug("Registering commands...");
        PackSwitcherCommands.register(event.getDispatcher(), event.getBuildContext());
    }
}

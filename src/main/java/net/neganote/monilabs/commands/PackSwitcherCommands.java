package net.neganote.monilabs.commands;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;
import net.neganote.monilabs.utils.PackSwitchUtil;

import com.mojang.brigadier.CommandDispatcher;

import static net.minecraft.commands.Commands.*;

public class PackSwitcherCommands {

    public static int switchToNormal() {
        PackSwitchUtil.switchToNormal(System.getProperty("user.dir"));
        stopMinecraft();
        return 0;
    }

    public static int switchToHard() {
        PackSwitchUtil.switchToHard(System.getProperty("user.dir"));
        stopMinecraft();
        return 0;
    }

    public static int switchToExpert() {
        PackSwitchUtil.switchToExpert(System.getProperty("user.dir"));
        stopMinecraft();
        return 0;
    }

    public static void stopMinecraft() {
        if (GTCEu.isClientSide()) {
            Minecraft.getInstance().stop();
        } else {
            GTCEu.getMinecraftServer().stopServer();
        }
    }

    public static int help(CommandSourceStack sourceStack) {
        sourceStack.sendSystemMessage(Component.translatable("monilabs.commands.helpnormal"));
        sourceStack.sendSystemMessage(Component.translatable("monilabs.commands.helphard"));
        sourceStack.sendSystemMessage(Component.translatable("monilabs.commands.helpexpert"));
        return 0;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        MoniLabs.LOGGER.debug("Pack Switcher Command Registered");

        dispatcher.register(literal("packmode")
                .then(literal("normal")
                        .executes(commandContext -> switchToNormal()))
                .then(literal("hard")
                        .executes(commandContext -> switchToHard()))
                .then(literal("expert")
                        .executes(commandContext -> switchToExpert()))
                .then(literal("help")
                        .executes(commandContext -> help(commandContext.getSource())))
                .requires(stack -> stack.hasPermission(4)));
    }
}

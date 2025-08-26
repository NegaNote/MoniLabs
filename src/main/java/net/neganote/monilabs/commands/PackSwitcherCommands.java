package net.neganote.monilabs.commands;

import com.gregtechceu.gtceu.GTCEu;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;

import com.mojang.brigadier.CommandDispatcher;

import java.io.File;
import java.nio.file.Path;

import static net.minecraft.commands.Commands.*;
import static net.neganote.monilabs.integration.fancymenu.PackSwitcherAction.copyFiles;
import static net.neganote.monilabs.integration.fancymenu.PackSwitcherAction.createModeFile;

public class PackSwitcherCommands {

    static String cwd = System.getProperty("user.dir");

    public static int switchToNormal(boolean inMinecraftInstance) {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "normal"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("normal");
        if (inMinecraftInstance) {
            stopMinecraft();
        }
        return 0;
    }

    public static int switchToHard(boolean inMinecraftInstance) {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hard"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("hard");
        if (inMinecraftInstance) {
            stopMinecraft();
        }
        return 0;
    }

    public static int switchToExpert(boolean inMinecraftInstance) {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hardmode"),
                Path.of(cwd, File.separator, "config"));
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "expert"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("expert");
        if (inMinecraftInstance) {
            stopMinecraft();
        }
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
                        .executes(commandContext -> switchToNormal(true)))
                .then(literal("hard")
                        .executes(commandContext -> switchToHard(true)))
                .then(literal("expert")
                        .executes(commandContext -> switchToExpert(true)))
                .then(literal("help")
                        .executes(commandContext -> help(commandContext.getSource())))
                .requires(stack -> stack.hasPermission(4)));
    }
}

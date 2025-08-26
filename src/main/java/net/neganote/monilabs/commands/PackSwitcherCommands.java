package net.neganote.monilabs.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.neganote.monilabs.MoniLabs;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;

import java.io.File;
import java.nio.file.Path;

import static net.minecraft.commands.Commands.*;
import static net.neganote.monilabs.integration.fancymenu.PackSwitcherAction.copyFiles;
import static net.neganote.monilabs.integration.fancymenu.PackSwitcherAction.createModeFile;

public class PackSwitcherCommands {

    static String cwd = System.getProperty("user.dir");

    static SimpleCommandExceptionType invalidLetter = new SimpleCommandExceptionType(
            Component.translatable("monilabs.commands.packswitcherinvalidletter"));
    static SimpleCommandExceptionType moreThanOneLetter = new SimpleCommandExceptionType(
            Component.translatable("monilabs.commands.packswitcheraword"));

    public static int switchToNormal() {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "normal"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("normal");
        Minecraft.getInstance().stop();
        return 0;
    }

    public static int switchToHard() {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hard"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("hard");
        Minecraft.getInstance().stop();
        return 0;
    }

    public static int switchToExpert() {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hardmode"),
                Path.of(cwd, File.separator, "config"));
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "expert"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("expert");
        Minecraft.getInstance().stop();
        return 0;
    }

    public static int help(CommandSourceStack sourceStack) {
        sourceStack.sendSystemMessage(Component.translatable("monilabs.commands.helpnormal"));
        sourceStack.sendSystemMessage(Component.translatable("monilabs.commands.helphard"));
        sourceStack.sendSystemMessage(Component.translatable("monilabs.commands.helpexpert"));
        return 0;
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext buildContext) {
        MoniLabs.LOGGER.warn("Pack Switcher Registered");
        dispatcher.register(literal("packmode")
                .then(literal("normal")
                        .executes(commandContext -> switchToNormal()))
                .then(literal("hard")
                        .executes(commandContext -> switchToHard()))
                .then(literal("expert")
                        .executes(commandContext -> switchToExpert()))
                .then(literal("help")
                        .executes(commandContext -> help(commandContext.getSource()))));
    }
}

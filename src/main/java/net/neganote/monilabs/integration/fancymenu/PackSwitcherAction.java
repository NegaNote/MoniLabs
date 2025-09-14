package net.neganote.monilabs.integration.fancymenu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import de.keksuccino.fancymenu.customization.action.Action;
import net.neganote.monilabs.utils.PackSwitchUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.Scanner;

import static net.neganote.monilabs.utils.PackSwitchUtil.copyFiles;
import static net.neganote.monilabs.utils.PackSwitchUtil.createModeFile;

public class PackSwitcherAction extends Action {

    String cwd = System.getProperty("user.dir");

    public PackSwitcherAction() {
        super("packModeSwitcherAction");
    }

    public String readTmpModeFile() {
        try {
            File tmpModeFile = new File(".tmpmode");
            Scanner reader = new Scanner(tmpModeFile);

            return reader.nextLine();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public void execute(@Nullable String s) {
        assert s != null;
        String mode = readTmpModeFile();
        switch (mode) {
            case "n" -> {
                PackSwitchUtil.switchToNormal(cwd);
            }
            case "h" -> {
                PackSwitchUtil.switchToHard(cwd);
            }
            case "e" -> {
                PackSwitchUtil.switchToExpert(cwd);
            }
        }
    }

    @Override
    public @NotNull Component getActionDisplayName() {
        return Component.translatable("monilabs.menu.packmodeswitcher.displayname");
    }

    @Override
    public @NotNull Component[] getActionDescription() {
        return new MutableComponent[] { Component.translatable("monilabs.menu.packmodeswitcher.description") };
    }

    @Override
    public @Nullable Component getValueDisplayName() {
        return Component.translatable("monilabs.menu.packmodeswitcher.valuename");
    }

    @Override
    public @Nullable String getValueExample() {
        return "N";
    }
}

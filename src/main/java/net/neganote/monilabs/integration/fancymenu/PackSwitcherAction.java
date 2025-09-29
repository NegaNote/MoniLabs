package net.neganote.monilabs.integration.fancymenu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.keksuccino.fancymenu.customization.action.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.*;
import java.util.Scanner;

import static net.neganote.monilabs.utils.PackSwitchUtil.copyFiles;
import static net.neganote.monilabs.utils.PackSwitchUtil.createModeFile;

@SideOnly(Side.CLIENT)
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
                copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "normal"),
                        Path.of(cwd, File.separator, "config"));

                createModeFile("normal");
            }
            case "h" -> {
                copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hardmode"),
                        Path.of(cwd, File.separator, "config"));

                createModeFile("hard");
            }
            case "e" -> {
                copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hardmode"),
                        Path.of(cwd, File.separator, "config"));
                copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "expert"),
                        Path.of(cwd, File.separator, "config"));

                createModeFile("expert");
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

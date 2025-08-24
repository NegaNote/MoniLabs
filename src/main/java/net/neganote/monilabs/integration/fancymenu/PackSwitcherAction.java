package net.neganote.monilabs.integration.fancymenu;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.neganote.monilabs.MoniLabs;

import de.keksuccino.fancymenu.customization.action.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.Objects;

public class PackSwitcherAction extends Action {

    String cwd = System.getProperty("user.dir");

    public PackSwitcherAction() {
        super("packModeSwitcherAction");
    }

    public boolean copyFiles(Path source, Path target) {
        if (source == null || target == null) {
            return false;
        }
        try {
            File directoryWithFiles = new File(source.toString());
            File[] files = directoryWithFiles.listFiles();

            for (File file : files) {
                Path path = Path.of(target.toString(), file.getName());
                if (!file.isDirectory()) {
                    MoniLabs.LOGGER.info("Copying File " + file.getName());
                    path.toFile().delete();
                    Files.copy(Path.of(source.toString(), file.getName()), path);
                } else {
                    MoniLabs.LOGGER.info("Copying Directory " + file.getName());
                    File directory = new File(Path.of(target.toString(), file.getName()).toUri());
                    directory.delete();
                    directory.mkdir();

                    copyFiles(Path.of(source.toString(), file.getName()), path);
                }

            }
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean createModeFile(String contents) {
        try {
            File modeFile = new File(".mode");
            modeFile.delete();

            modeFile.createNewFile();

            FileWriter modeFileWriter = new FileWriter(".mode");
            modeFileWriter.write(contents);
            modeFileWriter.close();
        } catch (IOException e) {
            throw new RuntimeException();
        }
        return true;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public void execute(@Nullable String s) {
        assert s != null;
        switch (s.toLowerCase()) {
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

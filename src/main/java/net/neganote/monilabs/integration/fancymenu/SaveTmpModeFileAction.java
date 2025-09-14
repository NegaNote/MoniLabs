package net.neganote.monilabs.integration.fancymenu;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

import de.keksuccino.fancymenu.customization.action.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SideOnly(Side.CLIENT)
public class SaveTmpModeFileAction extends Action {

    public SaveTmpModeFileAction() {
        super("savetmpmodefileaction");
    }

    public boolean createTmpModeFile(String contents) {
        try {
            File modeFile = new File(".tmpmode");
            modeFile.delete();

            modeFile.createNewFile();

            FileWriter modeFileWriter = new FileWriter(".tmpmode");
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
        createTmpModeFile(s.toLowerCase());
    }

    @Override
    public @NotNull Component getActionDisplayName() {
        return Component.translatable("monilabs.menu.SaveTmpModeFileAction.displayname");
    }

    @Override
    public @NotNull Component[] getActionDescription() {
        return new MutableComponent[] { Component.translatable("monilabs.menu.SaveTmpModeFileAction.displayname") };
    }

    @Override
    public @Nullable Component getValueDisplayName() {
        return Component.translatable("monilabs.menu.SaveTmpModeFileAction.valuename");
    }

    @Override
    public @Nullable String getValueExample() {
        return "N";
    }
}

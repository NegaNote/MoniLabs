package net.neganote.monilabs.integration.fancymenu;

import net.minecraft.network.chat.Component;

import de.keksuccino.fancymenu.customization.action.Action;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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
        return null;
    }

    @Override
    public @NotNull Component[] getActionDescription() {
        return new Component[0];
    }

    @Override
    public @Nullable Component getValueDisplayName() {
        return null;
    }

    @Override
    public @Nullable String getValueExample() {
        return "";
    }
}

package net.neganote.monilabs.utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class PackSwitchUtil {

    public static void switchToNormal(String cwd) {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "normal"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("normal");
    }

    public static void switchToHard(String cwd) {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hardmode"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("hard");
    }

    public static void switchToExpert(String cwd) {
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "hardmode"),
                Path.of(cwd, File.separator, "config"));
        copyFiles(Path.of(cwd, File.separator, "config-overrides", File.separator, "expert"),
                Path.of(cwd, File.separator, "config"));

        createModeFile("expert");
    }

    public static boolean copyFiles(Path source, Path target) {
        if (source == null || target == null) {
            return false;
        }
        try {
            System.out.println("Copying files from " + source);
            File directoryWithFiles = new File(source.toString());
            File[] files = directoryWithFiles.listFiles();

            for (File file : files) {
                Path path = Path.of(target.toString(), file.getName());
                if (!file.isDirectory()) {
                    path.toFile().delete();
                    Files.copy(Path.of(source.toString(), file.getName()), path);
                } else {
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

    public static boolean createModeFile(String contents) {
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
}

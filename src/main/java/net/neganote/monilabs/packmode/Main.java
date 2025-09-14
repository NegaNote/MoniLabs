package net.neganote.monilabs.packmode;

import net.neganote.monilabs.utils.PackSwitchUtil;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        var loc = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                .toURI()).getParent();
        Path pathLoc = Path.of(loc);
        String mcRoot = pathLoc.getParent().toString();

        String mode = null;
        for (String arg : args) {
            if (arg.equalsIgnoreCase("-h") || arg.equalsIgnoreCase("--help")) {
                usage();
                return;
            } else if (arg.equalsIgnoreCase("-r") || arg.equalsIgnoreCase("--relative")) {
                Path cwdPath = Path.of(System.getProperty("user.dir"));
                mcRoot = cwdPath.toString();
            } else {
                mode = arg;
            }
        }

        if (mode == null) {
            System.out.print("Hello! Please provide a mode to switch Monifactory to (N/H/E): ");
            Scanner inputScanner = new Scanner(System.in);
            mode = inputScanner.nextLine().strip();
        }
        switchOnMode(mode, mcRoot);
    }

    public static void usage() {
        System.out.println("Usage: java -jar monilabs-(version).jar [mode] (-r/--relative)");
        System.out.println("Relative flag is optional, and the mode can be omitted from");
        System.out.println("command-line arguments for the script to be run interactively.");
    }

    public static void switchOnMode(String mode, String mcRoot) {
        if (mode.equalsIgnoreCase("n") || mode.equalsIgnoreCase("normal")) {
            PackSwitchUtil.switchToNormal(mcRoot);
            System.out.println("Switched Monifactory to normal mode!");
        } else if (mode.equalsIgnoreCase("h") || mode.equalsIgnoreCase("hard")) {
            PackSwitchUtil.switchToHard(mcRoot);
            System.out.println("Switched Monifactory to hard mode!");
        } else if (mode.equalsIgnoreCase("e") || mode.equalsIgnoreCase("expert")) {
            PackSwitchUtil.switchToExpert(mcRoot);
            System.out.println("Switched Monifactory to expert mode!");
        } else {
            System.out.println("Invalid mode! Please run the script again.");
            usage();
        }
    }
}

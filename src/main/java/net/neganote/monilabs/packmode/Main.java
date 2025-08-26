package net.neganote.monilabs.packmode;

import net.neganote.monilabs.utils.PackSwitchUtil;

import lombok.SneakyThrows;

import java.io.File;
import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    @SneakyThrows
    public static void main(String[] args) {
        Path cwdPath = Path.of(System.getProperty("user.dir"));
        String mcRoot = cwdPath.getParent().toString();
        if (args.length == 0) {
            if (!cwdPath.endsWith("mods")) {
                System.out.println("Please run this script in the mods folder.");
                return;
            }
            System.out.print("Hello! Please provide a mode to switch Monifactory to (N/H/E): ");
            Scanner inputScanner = new Scanner(System.in);
            String mode = inputScanner.nextLine().strip();
            switchOnMode(mode, mcRoot);
        } else if (args.length == 1) {
            String mode = args[0];
            if (args[0].equalsIgnoreCase("-r") || args[0].equalsIgnoreCase("--relative")) {
                var loc = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                        .toURI()).getParent();
                mcRoot = Path.of(loc).getParent().toString();
                System.out.print("Hello! Please provide a mode to switch Monifactory to (N/H/E): ");
                Scanner inputScanner = new Scanner(System.in);
                mode = inputScanner.nextLine().strip();
            }
            switchOnMode(mode, mcRoot);
        } else if (args.length == 2) {
            String mode;
            if (args[0].equalsIgnoreCase("-r") || args[0].equalsIgnoreCase("--relative")) {
                mode = args[1];
            } else if (args[1].equalsIgnoreCase("-r") || args[1].equalsIgnoreCase("--relative")) {
                mode = args[0];
            } else {
                usage();
                return;
            }
            var loc = new File(Main.class.getProtectionDomain().getCodeSource().getLocation()
                    .toURI()).getParent();
            mcRoot = Path.of(loc).getParent().toString();
            switchOnMode(mode, mcRoot);
        } else {
            usage();
        }
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
        }
    }
}

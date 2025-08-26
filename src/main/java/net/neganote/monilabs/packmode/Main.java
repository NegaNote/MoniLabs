package net.neganote.monilabs.packmode;

import net.neganote.monilabs.utils.PackSwitchUtil;

import java.nio.file.Path;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.print("Hello! Please provide a mode to switch Monifactory to (N/H/E): ");
            Scanner inputScanner = new Scanner(System.in);
            String mode = inputScanner.nextLine().strip();
            switchOnMode(mode);
        } else if (args.length == 1) {
            String mode = args[0];
            switchOnMode(mode);
        } else {
            System.out.println("Please run this jar with either zero arguments (interactively)");
            System.out.println("or with one argument (the monifactory packmode to switch to).");
        }
    }

    public static void switchOnMode(String mode) {
        Path mcRootPath = Path.of(System.getProperty("user.dir")).getParent();
        if (!mcRootPath.endsWith("minecraft") && !mcRootPath.endsWith(".minecraft")) {
            System.out.println("Please load this jar from the mods directory!");
            return;
        }
        String mcRoot = mcRootPath.toString();
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
            System.out.println("Invalid mode! Please run the jar again.");
        }
    }
}

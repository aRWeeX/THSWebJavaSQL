package core;

import util.DevLogger;

import java.math.BigDecimal;
import java.util.Scanner;

public abstract class CoreController {
    public CoreController() {}

    protected BigDecimal getBigDecimalInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String inputString = scanner.nextLine().trim();

        if (inputString.isEmpty()) {
            return null;
        }

        try {
            return new BigDecimal(inputString);
        } catch (NumberFormatException e) {
            DevLogger.logError(e);
            System.out.println("Please enter a valid number (e.g., 123.45).");
            return null;
        }
    }

    protected Integer getIntInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String inputString = scanner.nextLine().trim();

        if (inputString.isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(inputString);
        } catch (NumberFormatException e) {
            DevLogger.logError(e);
            System.out.println("Please enter a valid number (e.g., 123).");
            return null;
        }
    }

    protected String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String inputString = scanner.nextLine().trim();
        return inputString.isEmpty() ? "" : inputString;
    }
}

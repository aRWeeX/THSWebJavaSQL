package core;

import java.math.BigDecimal;
import java.util.Scanner;

public abstract class CoreController {
    public CoreController() {}

    protected String getStringInput(Scanner scanner, String prompt) {
        System.out.print(prompt);
        String inputString = scanner.nextLine().trim();
        return inputString.isEmpty() ? "" : inputString;
    }

    protected Integer getIntInput(Scanner scanner, String prompt) {
        Integer input = null;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);

            String inputString = scanner.nextLine().trim();

            if (inputString.isEmpty()) {
                System.err.println("Input cannot be empty. Please try again.");
            } else {
                try {
                    input = Integer.parseInt(inputString);
                    valid = true;
                } catch (NumberFormatException e) {
                    System.err.println("That wasn't a valid number. Please try again.");
                }
            }
        }

        return input;
    }

    protected BigDecimal getBigDecimalInput(Scanner scanner, String prompt) {
        BigDecimal input = null;
        boolean valid = false;

        while (!valid) {
            System.out.print(prompt);
            String inputString = scanner.nextLine().trim();

            if (inputString.isEmpty()) {
                System.err.println("Input cannot be empty. Please try again.");
            } else {
                try {
                    input = new BigDecimal(inputString);
                    valid = true;
                } catch (NumberFormatException e) {
                    System.err.println("That wasn't a valid number. Please try again.");
                }
            }
        }

        return input;
    }
}

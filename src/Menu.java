import customer.CustomerController;
import order.OrderController;
import product.ProductController;
import util.DevLogger;

import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Menu {
    private final CustomerController customerController;
    private final ProductController productController;
    private final OrderController orderController;
    private final Scanner scanner;

    public Menu(CustomerController customerController,
                ProductController productController,
                OrderController orderController) {
        this.customerController = customerController;
        this.productController = productController;
        this.orderController = orderController;
        this.scanner = new Scanner(System.in);
    }

    public void showMainMenu() {
        int choice = -1;

        do {
            System.out.println();
            System.out.println("--- Main menu ---");
            System.out.println("1. Customer menu");
            System.out.println("2. Product menu");
            System.out.println("3. Order menu");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            try {
                String input = scanner.nextLine().trim();
                choice = Integer.parseInt(input);
                handleMenuOption(choice);
            } catch (InputMismatchException | NumberFormatException e) {
                DevLogger.logError(e);
                System.err.println("Please enter a valid option from the menu.");
            }
        } while (choice != 0);
    }

    private void handleMenuOption(int choice) {
        try {
            switch (choice) {
                case 1:
                    customerController.showMenu();
                    break;

                case 2:
                    productController.showMenu();
                    break;

                case 3:
                    orderController.showMenu();
                    break;

                case 0:
                    System.out.println("Closing down the application.");
                    break;

                default:
                    System.out.println("That wasn't a valid option.");
            }
        } catch (SQLException e) {
            DevLogger.logError(e);
            System.out.println("We're having trouble connecting to the system. Please try again later.");
        }
    }

    public void closeScanner() {
        scanner.close();
    }
}

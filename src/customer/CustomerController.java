package customer;

import core.CoreController;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class CustomerController extends CoreController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        super();
        this.customerService = customerService;
    }

    public void showMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println();
            System.out.println("--- Customer management menu ---");
            System.out.println("1. Add customer");
            System.out.println("2. View customers");
            System.out.println("3. View customer");
            System.out.println("4. Update customer");
            System.out.println("5. Delete customer");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addCustomer(scanner);
                    break;

                case 2:
                    viewCustomers();
                    break;

                case 3:
                    viewCustomer(scanner);
                    break;

                case 4:
                    updateCustomer(scanner);
                    break;

                case 5:
                    deleteCustomer(scanner);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void addCustomer(Scanner scanner) {
        System.out.println();
        System.out.println("--- Add customer ---");

        String name = getStringInput(scanner, "Enter name: ");
        String email = getStringInput(scanner, "Enter email: ");
        String phone = getStringInput(scanner, "Enter phone (optional): ");
        String address = getStringInput(scanner, "Enter address (optional): ");
        String password = getStringInput(scanner, "Enter password (must be at least 8 characters long): ");

        Customer customer = new Customer(
                name,
                email,
                phone.isEmpty() ? null : phone,
                address.isEmpty() ? null : address,
                password
        );

        try {
            Customer createdCustomer = customerService.createCustomer(customer);
            System.out.println("Customer successfully created with ID " + createdCustomer.getCustomerId() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("An error occurred while processing the request: " + e.getMessage());
        }
    }

    private void viewCustomers() throws SQLException {
        List<Customer> customers = customerService.getAllCustomers();

        System.out.println();
        System.out.println("--- View customers ---");

        for (int i = 0; i < customers.size(); i++) {
            Customer customer = customers.get(i);
            System.out.println("Customer ID: " + customer.getCustomerId());
            System.out.println("Name: " + customer.getName());
            System.out.println("Email: " + customer.getEmail());

            if (i < customers.size() - 1) {
                System.out.println();
            }
        }
    }

    private void viewCustomer(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- View customer ---");

        int customerId = getIntInput(scanner, "Enter customer ID: ");
        Customer customer = customerService.getCustomerById(customerId);

        if (customer != null) {
            System.out.println("Customer ID: " + customer.getCustomerId());
            System.out.println("Name: " + customer.getName());
            System.out.println("Email: " + customer.getEmail());
            System.out.println("Phone: " + (customer.getPhone() != null ? customer.getPhone() : "N/A"));
            System.out.println("Address: " + (customer.getAddress() != null ? customer.getAddress() : "N/A"));
            System.out.println("Password: [HIDDEN]");
        } else {
            System.out.println("No customer found with ID " + customerId + ".");
        }
    }

    private void updateCustomer(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- Update customer ---");

        int customerId = getIntInput(scanner, "Enter customer ID: ");
        Customer existingCustomer = customerService.getCustomerById(customerId);

        if (existingCustomer == null) {
            System.out.println("No customer found with ID " + customerId + ".");
            return;
        }

        System.out.println("Current name: " + existingCustomer.getName());
        String name = getStringInput(scanner, "Enter new name (or leave blank to keep current): ");
        if (name.isEmpty()) name = existingCustomer.getName();

        System.out.println("Current email: " + existingCustomer.getEmail());
        String email = getStringInput(scanner, "Enter new email (or leave blank to keep current): ");
        if (email.isEmpty()) email = existingCustomer.getEmail();

        System.out.println("Current phone: " + (existingCustomer.getPhone() != null ?
                existingCustomer.getPhone() : "N/A"));
        String phone = getStringInput(scanner, "Enter new phone (or leave blank to keep current): ");
        if (phone.isEmpty()) phone = existingCustomer.getPhone();

        System.out.println("Current address: " + (existingCustomer.getAddress() != null ?
                existingCustomer.getAddress() : "N/A"));
        String address = getStringInput(scanner, "Enter new address (or leave blank to keep current): ");
        if (address.isEmpty()) address = existingCustomer.getAddress();

        System.out.println("Current password: [HIDDEN]");
        String password = getStringInput(scanner, "Enter new password (or leave blank to keep current): ");
        if (password.isEmpty()) password = existingCustomer.getPassword();

        Customer updatedCustomer = new Customer(
                customerId,
                name,
                email,
                phone == null || phone.isEmpty() ? null : phone,
                address == null || address.isEmpty() ? null : address,
                password
        );

        try {
            boolean updated = customerService.updateCustomer(updatedCustomer);

            if (updated) {
                System.out.println("Customer successfully updated with ID " + customerId + ".");
            } else {
                System.out.println("No changes were made to customer.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("An error occurred while processing the request: " + e.getMessage());
        }
    }

    private void deleteCustomer(Scanner scanner) {
        System.out.println();
        System.out.println("--- Delete customer ---");

        int customerId = getIntInput(scanner, "Enter customer ID: ");

        try {
            customerService.deleteCustomer(customerId);
            System.out.println("Customer with ID " + customerId + " deleted successfully.");
        } catch (SQLException e) {
            System.err.println("An error occurred while processing the request: " + e.getMessage());
        }
    }
}

package product;

import core.CoreController;
import util.DevLogger;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class ProductController extends CoreController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        super();
        this.productService = productService;
    }

    public void showMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println();
            System.out.println("--- Product management menu ---");
            System.out.println("1. Add product");
            System.out.println("2. View products");
            System.out.println("3. View product");
            System.out.println("4. Update product");
            System.out.println("5. Delete product");
            System.out.println("6. Search products by name");
            System.out.println("7. Search products by category");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addProduct(scanner);
                    break;

                case 2:
                    viewProducts();
                    break;

                case 3:
                    viewProduct(scanner);
                    break;

                case 4:
                    updateProduct(scanner);
                    break;

                case 5:
                    deleteProduct(scanner);
                    break;

                case 6:
                    searchProductsByName(scanner);
                    break;

                case 7:
                    searchProductsByCategory(scanner);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("That wasn't a valid option.");
            }
        } while (choice != 0);
    }

    private void addProduct(Scanner scanner) {
        System.out.println();
        System.out.println("--- Add product ---");

        String name = getStringInput(scanner, "Enter name: ");
        String description = getStringInput(scanner, "Enter description (optional): ");
        BigDecimal price = getBigDecimalInput(scanner, "Enter price: ");
        int stockQuantity = getIntInput(scanner, "Enter stock quantity: ");

        Product product = new Product(
                name,
                description == null || description.isEmpty() ? null : description,
                price,
                stockQuantity
        );

        try {
            Product createdProduct = productService.createProduct(product);
            System.out.println("Product with ID " + createdProduct.getProductId() + " was created.");
        } catch (IllegalArgumentException e) {
            DevLogger.logError(e);
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            DevLogger.logError(e);
            System.out.println("Something went wrong while handling your request.");
        }
    }

    private void viewProducts() throws SQLException {
        List<Product> products = productService.getAllProducts();

        System.out.println();
        System.out.println("--- View products ---");

        for (int i = 0; i < products.size(); i++) {
            Product product = products.get(i);

            System.out.println("Product ID: " + product.getProductId());
            System.out.println("Name: " + product.getName());
            System.out.println("Description: " + product.getDescription());

            if (i < products.size() - 1) {
                System.out.println();
            }
        }
    }

    private void viewProduct(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- View product ---");

        int productId = getIntInput(scanner, "Enter product ID: ");
        Product product = productService.getProductById(productId);

        if (product != null) {
            System.out.println("Product ID: " + product.getProductId());
            System.out.println("Manufacturer ID: " + (product.getManufacturerId() != -1
                    ? product.getManufacturerId()
                    : "N/A"));
            System.out.println("Name: " + product.getName());
            System.out.println("Manufacturer name: " + (product.getManufacturerName() != null
                    ? product.getManufacturerName()
                    : "N/A"));
            System.out.println("Description: " + (product.getDescription() != null ? product.getDescription() : "N/A"));
            System.out.println("Price: " + product.getPrice());
            System.out.println("Stock quantity: " + product.getStockQuantity());
        } else {
            System.out.println("Product with ID " + productId + " wasn't found.");
        }
    }

    private void updateProduct(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- Update product ---");

        int productId = getIntInput(scanner, "Enter product ID: ");
        Product product = productService.getProductById(productId);

        System.out.println("Current name: " + product.getName());
        String name = getStringInput(scanner, "Enter new name (or leave blank to keep current): ");
        if (name.isEmpty()) name = product.getName();

        System.out.println("Current description: " + (product.getDescription() != null
                ? product.getDescription()
                : "N/A"));
        String description = getStringInput(
                scanner,
                "Enter new description (or leave blank to keep current): ");
        if (description.isEmpty()) description = product.getDescription();

        System.out.println("Current price: " + product.getPrice());
        BigDecimal price = getBigDecimalInput(scanner, "Enter new price (or leave blank to keep current): ");
        if (price == null) price = product.getPrice();

        System.out.println("Current stock quantity: " + product.getStockQuantity());
        Integer stockQuantity = getIntInput(
                scanner,
                "Enter new stock quantity (or leave blank to keep current): ");
        if (stockQuantity == null) stockQuantity = product.getStockQuantity();

        Product updatedProduct = new Product(
                productId,
                product.getManufacturerId(),
                name,
                product.getManufacturerName(),
                description == null || description.isEmpty() ? null : description,
                price,
                stockQuantity
        );

        try {
            boolean updated = productService.updateProduct(updatedProduct);

            if (updated) {
                System.out.println("Product with ID " + productId + " was updated.");
            } else {
                System.out.println("No changes were made to the product.");
            }
        } catch (IllegalArgumentException e) {
            DevLogger.logError(e);
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            DevLogger.logError(e);
            System.out.println("Something went wrong while handling your request.");
        }
    }

    private void deleteProduct(Scanner scanner) {
        System.out.println();
        System.out.println("--- Delete product ---");

        int productId = getIntInput(scanner, "Enter product ID: ");

        try {
            productService.deleteProduct(productId);
            System.out.println("Product with ID " + productId + " was deleted.");
        } catch (SQLException e) {
            DevLogger.logError(e);
            System.out.println("Something went wrong while handling your request.");
        }
    }

    private void searchProductsByName(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- Search products by name ---");

        String nameKeyword = getStringInput(scanner, "Enter name search keyword: ");
        List<Product> products = productService.searchProductsByName(nameKeyword);

        if (products.isEmpty()) {
            System.out.println("No products found matching \"" + nameKeyword + "\".");
        } else {
            System.out.println("Matching products:");

            for (Product product : products) {
                System.out.println("- " + product.getName() + " (ID: " + product.getProductId() + ")");
            }
        }
    }

    private void searchProductsByCategory(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- Search products by category ---");

        String categoryKeyword = getStringInput(scanner, "Enter category search keyword: ");
        List<Product> products = productService.searchProductsByCategory(categoryKeyword);

        if (products.isEmpty()) {
            System.out.println("No products found matching \"" + categoryKeyword + "\".");
        } else {
            System.out.println("Matching products:");

            for (Product product : products) {
                System.out.println("- " + product.getName() + " (ID: " + product.getProductId() + ")");
            }
        }
    }
}

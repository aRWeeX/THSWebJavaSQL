package product;

import core.CoreController;

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
                    System.out.println("Invalid choice. Please try again.");
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
                description.isEmpty() ? null : description,
                price,
                stockQuantity
        );

        try {
            Product createdProduct = productService.createProduct(product);
            System.out.println("Product successfully created with ID " + createdProduct.getProductId() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("An error occurred while processing the request: " + e.getMessage());
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
            System.out.println("Manufacturer ID: " + product.getManufacturerId());
            System.out.println("Name: " + product.getName());
            System.out.println("Manufacturer name: " + product.getManufacturerName());
            System.out.println("Description: " + (product.getDescription() != null ? product.getDescription() : "N/A"));
            System.out.println("Price: " + product.getPrice());
            System.out.println("Stock quantity: " + product.getStockQuantity());
        } else {
            System.out.println("No product found with ID " + productId + ".");
        }
    }

    private void updateProduct(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- Update product ---");

        int productId = getIntInput(scanner, "Enter product ID: ");
        Product existingProduct = productService.getProductById(productId);

        if (existingProduct == null) {
            System.out.println("No product found with ID " + productId + ".");
            return;
        }

        System.out.println("Current name: " + existingProduct.getName());
        String name = getStringInput(scanner, "Enter new name (or leave blank to keep current): ");
        if (name.isEmpty()) name = existingProduct.getName();

        System.out.println("Current description: " + (existingProduct.getDescription() != null ?
                existingProduct.getDescription() : "N/A"));
        String description = getStringInput(
                scanner, "Enter new description (or leave blank to keep current): ");
        if (description.isEmpty()) description = existingProduct.getDescription();

        System.out.println("Current price: " + existingProduct.getPrice());
        BigDecimal price = getBigDecimalInput(scanner, "Enter new price (or leave blank to keep current): ");
        if (price == null) price = existingProduct.getPrice();

        System.out.println("Current stock quantity: " + existingProduct.getStockQuantity());
        Integer stockQuantity = getIntInput(
                scanner, "Enter new stock quantity (or leave blank to keep current): ");
        if (stockQuantity == null) stockQuantity = existingProduct.getStockQuantity();

        Product updatedProduct = new Product(
                productId,
                name,
                description == null || description.isEmpty() ? null : description,
                price,
                stockQuantity
        );

        try {
            boolean updated = productService.updateProduct(updatedProduct);

            if (updated) {
                System.out.println("Product successfully deleted with ID " + productId + ".");
            } else {
                System.out.println("No changes were made to product.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("An error occurred while processing the request: " + e.getMessage());
        }
    }

    private void deleteProduct(Scanner scanner) {
        System.out.println();
        System.out.println("--- Delete product ---");

        int productId = getIntInput(scanner, "Enter product ID: ");

        try {
            productService.deleteProduct(productId);
            System.out.println("Product with ID " + productId + " deleted successfully.");
        } catch (SQLException e) {
            System.err.println("An error occurred while processing the request: " + e.getMessage());
        }
    }

    private void searchProductsByName(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- Search products by name ---");
        String searchKeyword = getStringInput(scanner, "Enter search keyword (name): ");
        List<Product> products = productService.searchProductsByName(searchKeyword);

        if (products.isEmpty()) {
            System.out.println("No products found matching keyword (name) \"" + searchKeyword + "\".");
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
        String searchCategory = getStringInput(scanner, "Enter search keyword (category): ");
        List<Product> products = productService.searchProductsByCategory(searchCategory);

        if (products.isEmpty()) {
            System.out.println("No products found matching keyword (category) \"" + searchCategory + "\".");
        } else {
            System.out.println("Matching products:");

            for (Product product : products) {
                System.out.println("- " + product.getName() + " (ID: " + product.getProductId() + ")");
            }
        }
    }
}

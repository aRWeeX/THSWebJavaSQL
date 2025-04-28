import customer.CustomerController;
import customer.CustomerRepository;
import customer.CustomerService;

import order.OrderController;
import order.OrderRepository;
import order.OrderService;

import product.ProductController;
import product.ProductRepository;
import product.ProductService;

import util.DevLogger;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            CustomerRepository customerRepository = new CustomerRepository(connection);
            CustomerService customerService = new CustomerService(customerRepository);
            CustomerController customerController = new CustomerController(customerService);

            ProductRepository productRepository = new ProductRepository(connection);
            ProductService productService = new ProductService(productRepository);
            ProductController productController = new ProductController(productService);

            OrderRepository orderRepository = new OrderRepository(connection);
            OrderService orderService = new OrderService(orderRepository);
            OrderController orderController = new OrderController(orderService);

            Menu menu = new Menu(customerController, productController, orderController);
            menu.showMainMenu();
            menu.closeScanner();
        } catch (SQLException e) {
            DevLogger.logError(e);
            System.out.println("Something went wrong while connecting.");
        }
    }
}

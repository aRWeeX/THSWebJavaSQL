package order;

import core.CoreController;
import orderproduct.OrderProduct;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderController extends CoreController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        super();
        this.orderService = orderService;
    }

    public void showMenu() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println();
            System.out.println("--- Order management menu ---");
            System.out.println("1. Add order");
            System.out.println("2. View orders (NOT IMPLEMENTED YET)");
            System.out.println("3. View order");
            System.out.println("4. Update order (NOT IMPLEMENTED YET)");
            System.out.println("5. Delete order (NOT IMPLEMENTED YET)");
            System.out.println("0. Back");
            System.out.print("Choose an option: ");

            choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addOrder(scanner);
                    break;

                case 2:
                    //viewOrders(scanner);
                    break;

                case 3:
                    viewOrder(scanner);
                    break;

                case 4:
                    //updateOrder(scanner);
                    break;

                case 5:
                    //deleteOrder(scanner);
                    break;

                case 0:
                    break;

                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        } while (choice != 0);
    }

    private void addOrder(Scanner scanner) {
        System.out.println();
        System.out.println("--- Add order ---");

        int customerId = getIntInput(scanner, "Enter customer ID: ");
        LocalDateTime orderDate = LocalDateTime.now();
        List<OrderProduct> products = new ArrayList<>();
        boolean addingMore = true;

        while (addingMore) {
            int productId = getIntInput(scanner, "Enter product ID: ");
            int quantity = getIntInput(scanner, "Enter quantity: ");
            BigDecimal unitPrice = getBigDecimalInput(scanner, "Enter unit price: ");

            products.add(new OrderProduct(
                    0,
                    0,
                    productId,
                    quantity,
                    unitPrice)
            );

            String more = getStringInput(scanner, "Add another product? (y/n): ");
            addingMore = more.equalsIgnoreCase("y");
        }

        Order order = new Order(
                0,
                customerId,
                orderDate
        );

        try {
            Order createdOrder = orderService.createOrder(order, products);
            System.out.println("Order successfully created with ID " + createdOrder.getOrderId() + ".");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            System.err.println("An error occurred while processing the request: " + e.getMessage());
        }
    }

    private void viewOrder(Scanner scanner) throws SQLException {
        System.out.println();
        System.out.println("--- View order ---");

        int customerId = getIntInput(scanner, "Enter customer ID: ");
        List<Order> orders = orderService.getOrderById(customerId);

        if (!orders.isEmpty()) {
            for (int i = 0; i < orders.size(); i++) {
                Order order = orders.get(i);
                System.out.println("Order ID: " + order.getOrderId());
                System.out.println("Order date: " + order.getOrderDate());
                System.out.println("Products:");

                for (OrderProduct orderProduct : order.getProducts()) {
                    System.out.println("- Name: " + orderProduct.getProductName()
                                     + ", Product ID: " + orderProduct.getProductId()
                                     + ", Quantity: " + orderProduct.getQuantity()
                                     + ", Unit price: " + orderProduct.getUnitPrice());
                }

                System.out.println("Total price: " + order.getTotalPrice());

                if (i < orders.size() - 1) {
                    System.out.println();
                }
            }
        } else {
            System.out.println("No orders found for this customer.");
        }
    }
}

package order;

import core.CoreController;
import orderproduct.OrderProduct;
import util.DevLogger;

import java.math.BigDecimal;
import java.sql.SQLException;
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
                    System.out.println("That wasn't a valid option.");
            }
        } while (choice != 0);
    }

    private void addOrder(Scanner scanner) {
        System.out.println();
        System.out.println("--- Add order ---");

        int customerId = getIntInput(scanner, "Enter customer ID: ");
        List<OrderProduct> products = new ArrayList<>();
        boolean addingMore = true;

        while (addingMore) {
            int productId = getIntInput(scanner, "Enter product ID: ");
            int quantity = getIntInput(scanner, "Enter quantity: ");
            BigDecimal unitPrice = getBigDecimalInput(scanner, "Enter unit price: ");

            products.add(new OrderProduct(
                    -1,
                    -1,
                    productId,
                    quantity,
                    unitPrice)
            );

            String more = getStringInput(scanner, "Add another product? (y/n): ");
            addingMore = more.equalsIgnoreCase("y");
        }

        Order order = new Order(
                -1,
                customerId
        );

        try {
            Order createdOrder = orderService.createOrder(order, products);
            System.out.println("Order with ID " + createdOrder.getOrderId() + " was created.");
        } catch (IllegalArgumentException e) {
            DevLogger.logError(e);
            System.out.println(e.getMessage());
        } catch (SQLException e) {
            DevLogger.logError(e);
            System.out.println("Something went wrong while handling your request.");
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
                    System.out.println("- Name: " + orderProduct.getProductName() +
                            ", product ID: " + orderProduct.getProductId() +
                            ", quantity: " + orderProduct.getQuantity() +
                            ", unit price: " + orderProduct.getUnitPrice());
                }

                System.out.println("Total price: " + order.getTotalPrice());

                if (i < orders.size() - 1) {
                    System.out.println();
                }
            }
        } else {
            System.out.println("This customer has no associated orders.");
        }
    }
}

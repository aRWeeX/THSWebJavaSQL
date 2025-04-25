package order;

import customer.Customer;
import customer.CustomerService;
import orderproduct.OrderProduct;

import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepository, CustomerService customerService) {
        this.orderRepository = orderRepository;
        this.customerService = customerService;
    }

    public Order createOrder(Order order, List<OrderProduct> products) throws SQLException {
        validateOrderInput(order, products);
        return orderRepository.create(order, products);
    }

    public List<Order> getOrderById(int orderId) throws SQLException {
        return orderRepository.getById(orderId);
    }

    private void validateOrderInput(Order order, List<OrderProduct> products) throws SQLException {
        if (order == null || products == null || products.isEmpty()) {
            throw new IllegalArgumentException(
                    "Order must not be null, and the product list must not be null or empty.");
        }

        Customer customer = customerService.getCustomerById(order.getCustomerId());

        if (customer == null) {
            throw new IllegalArgumentException("No customer found with that ID.");
        }
    }
}

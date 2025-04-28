package order;

import orderproduct.OrderProduct;

import java.sql.SQLException;
import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
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
            throw new IllegalArgumentException("An order must include at least one product.");
        }

        if (order.getCustomerId() == -1) {
            throw new IllegalArgumentException("No customer exists with the provided ID.");
        }
    }
}

package order;

import orderproduct.OrderProduct;
import util.DevLogger;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private static final DateTimeFormatter ORDER_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Connection connection;

    public OrderRepository(Connection connection) {
        this.connection = connection;
    }

    public Order create(Order order, List<OrderProduct> products) throws SQLException {
        String queryOrder = "INSERT INTO orders (customer_id) VALUES (?)";
        String queryOrderProduct = """
            INSERT INTO orders_products (order_id, product_id, quantity, unit_price)
            VALUES (?, ?, ?, ?)
        """;

        try {
            connection.setAutoCommit(false);
            int orderId;

            try (PreparedStatement orderPstmt = connection.prepareStatement(
                    queryOrder,
                    Statement.RETURN_GENERATED_KEYS)) {
                orderPstmt.setInt(1, order.getCustomerId());
                orderPstmt.executeUpdate();
                ResultSet rs = orderPstmt.getGeneratedKeys();

                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to create the order. " +
                            "No rows were affected in the database operation.");
                }
            }

            try (PreparedStatement orderProductPstmt = connection.prepareStatement(queryOrderProduct)) {
                for (OrderProduct op : products) {
                    orderProductPstmt.setInt(1, orderId);
                    orderProductPstmt.setInt(2, op.getProductId());
                    orderProductPstmt.setInt(3, op.getQuantity());
                    orderProductPstmt.setBigDecimal(4, op.getUnitPrice());

                    orderProductPstmt.addBatch();
                }

                orderProductPstmt.executeBatch();
            }

            connection.commit();

            return new Order(
                    orderId,
                    order.getCustomerId()
            );
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                DevLogger.logError(rollbackEx);
            }

            throw new SQLException("Error inserting the order into the database: " + e.getMessage(), e);
        } finally {
            connection.setAutoCommit(true);
        }
    }

    public List<Order> getById(int customerId) throws SQLException {
        String query = """
            SELECT
                o.order_id,
                o.order_date,
                p.product_id,
                p.name AS product_name,
                op.quantity,
                op.unit_price
            FROM orders o
            JOIN orders_products op ON o.order_id = op.order_id
            JOIN products p ON op.product_id = p.product_id
            WHERE o.customer_id = ?
            ORDER BY o.order_id
        """;
        List<Order> orders = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                Order currentOrder = null;

                while (rs.next()) {
                    int orderId = rs.getInt("order_id");
                    String orderDateString = rs.getString("order_date");
                    LocalDateTime orderDate = LocalDateTime.parse(orderDateString, ORDER_DATE_FORMATTER);

                    if (currentOrder == null || currentOrder.getOrderId() != orderId) {
                        if (currentOrder != null) {
                            orders.add(currentOrder);
                        }

                        currentOrder = new Order();

                        currentOrder.setOrderId(orderId);
                        currentOrder.setOrderDate(orderDate);
                        currentOrder.setProducts(new ArrayList<>());
                        currentOrder.setTotalPrice(BigDecimal.ZERO);
                    }

                    int productId = rs.getInt("product_id");
                    int quantity = rs.getInt("quantity");
                    BigDecimal unitPrice = rs.getBigDecimal("unit_price");
                    String productName = rs.getString("product_name");

                    OrderProduct product = new OrderProduct();

                    product.setProductId(productId);
                    product.setQuantity(quantity);
                    product.setUnitPrice(unitPrice);
                    product.setProductName(productName);

                    currentOrder.getProducts().add(product);

                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
                    BigDecimal currentTotal = currentOrder.getTotalPrice();
                    currentOrder.setTotalPrice(currentTotal.add(lineTotal));
                }

                if (currentOrder != null) {
                    orders.add(currentOrder);
                }
            }
        }

        return orders;
    }
}

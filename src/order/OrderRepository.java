package order;

import orderproduct.OrderProduct;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderRepository {
    private final Connection connection;

    public OrderRepository(Connection connection) {
        this.connection = connection;
    }

    public Order create(Order order, List<OrderProduct> products) throws SQLException {
        String queryOrders = "INSERT INTO orders (customer_id, order_date) VALUES (?, ?)";
        String queryOrdersProducts = """
            INSERT INTO orders_products (order_id, product_id, quantity, unit_price)
            VALUES (?, ?, ?, ?)
        """;

        try {
            connection.setAutoCommit(false);
            int orderId;

            try (PreparedStatement ordersStmt =
                         connection.prepareStatement(queryOrders, Statement.RETURN_GENERATED_KEYS)) {
                ordersStmt.setInt(1, order.getCustomerId());

                if (order.getOrderDate() != null) {
                    ordersStmt.setTimestamp(2, Timestamp.valueOf(order.getOrderDate()));
                } else {
                    ordersStmt.setNull(2, Types.TIMESTAMP);
                }

                ordersStmt.executeUpdate();
                ResultSet rs = ordersStmt.getGeneratedKeys();

                if (rs.next()) {
                    orderId = rs.getInt(1);
                } else {
                    throw new SQLException("Failed to create order, no rows affected.");
                }
            }

            try (PreparedStatement ordersProductsStmt = connection.prepareStatement(queryOrdersProducts)) {
                for (OrderProduct op : products) {
                    ordersProductsStmt.setInt(1, orderId);
                    ordersProductsStmt.setInt(2, op.getProductId());
                    ordersProductsStmt.setInt(3, op.getQuantity());
                    ordersProductsStmt.setBigDecimal(4, op.getUnitPrice());
                    ordersProductsStmt.addBatch();
                }

                ordersProductsStmt.executeBatch();
            }

            connection.commit();

            return new Order(
                    orderId,
                    order.getCustomerId(),
                    order.getOrderDate()
            );
        } catch (SQLException e) {
            connection.rollback();
            throw e;
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

                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    LocalDateTime orderDate = LocalDateTime.parse(orderDateString, formatter);

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

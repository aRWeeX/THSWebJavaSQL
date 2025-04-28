package order;

import orderproduct.OrderProduct;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class Order {
    private int orderId;
    private int customerId;
    private LocalDateTime orderDate;
    private List<OrderProduct> products; // Joined in
    private BigDecimal totalPrice; // Joined in

    public Order() {
        this(-1, -1, null, null, null);
    }

    public Order(int orderId, int customerId) {
        this(orderId, customerId, null, null, null);
    }

    public Order(int orderId, int customerId, LocalDateTime orderDate,
                 List<OrderProduct> products, BigDecimal totalPrice) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.products = products;
        this.totalPrice = totalPrice;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public List<OrderProduct> getProducts() {
        return products;
    }

    public void setProducts(List<OrderProduct> products) {
        this.products = products;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customerId=" + customerId +
                ", orderDate=" + orderDate +
                ", products=" + (products != null ? products : "N/A") +
                ", totalPrice=" + (totalPrice != null ? totalPrice : "N/A") +
                '}';
    }
}

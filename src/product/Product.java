package product;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {
    private int productId;
    private int manufacturerId; // May be null
    private String name;
    private String manufacturerName; // Joined in
    private String description; // May be null
    private BigDecimal price;
    private int stockQuantity;

    public Product() {
        this(-1, -1, null, null,
                null, null, -1);
    }

    public Product(int productId, int manufacturerId, String name, String manufacturerName) {
        this(productId, manufacturerId, name, manufacturerName, null, null, -1);
    }

    public Product(int productId, String name, String description, BigDecimal price, int stockQuantity) {
        this(productId, -1, name, null, description, price, stockQuantity);
    }

    public Product(String name, String description, BigDecimal price, int stockQuantity) {
        this(-1, -1, name, null, description, price, stockQuantity);
    }

    public Product(int productId, int manufacturerId, String name, String manufacturerName,
                   String description, BigDecimal price, int stockQuantity) {
        this.productId = productId;
        this.manufacturerId = manufacturerId;
        this.name = name;
        this.manufacturerName = manufacturerName;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Integer getManufacturerId() {
        return manufacturerId;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;

        return productId == product.productId &&
                stockQuantity == product.stockQuantity &&
                Objects.equals(manufacturerId, product.manufacturerId) &&
                Objects.equals(name, product.name) &&
                Objects.equals(manufacturerName, product.manufacturerName) &&
                Objects.equals(description, product.description) &&
                Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, manufacturerId, name, manufacturerName, description, price, stockQuantity);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", manufacturerId=" + (manufacturerId != -1 ? manufacturerId : "N/A") +
                ", name='" + name + '\'' +
                ", manufacturerName='" + (manufacturerName != null ? manufacturerName : "N/A") + '\'' +
                ", description='" + (description != null ? description : "N/A") + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                '}';
    }
}

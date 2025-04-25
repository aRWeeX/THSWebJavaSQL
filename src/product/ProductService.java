package product;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) throws SQLException {
        validateProductInput(product);
        return productRepository.create(product);
    }

    public List<Product> getAllProducts() throws SQLException {
        return productRepository.getAll();
    }

    public Product getProductById(int productId) throws SQLException {
        return productRepository.getById(productId);
    }

    public boolean updateProduct(Product product) throws SQLException {
        Product existingProduct = productRepository.getById(product.getProductId());

        if (existingProduct == null) {
            throw new IllegalArgumentException("No product found with ID " + product.getProductId() + ".");
        }

        validateProductInput(product);

        if (!existingProduct.equals(product)) {
            productRepository.update(product.getProductId(), product);
            return true;
        } else {
            return false;
        }
    }

    public void deleteProduct(int productId) throws SQLException {
        Product existingProduct = productRepository.getById(productId);

        if (existingProduct == null) {
            throw new IllegalArgumentException("No product found with ID " + productId + ".");
        }

        productRepository.delete(productId);
    }

    public List<Product> searchProductsByName(String keyword) throws SQLException {
        if (isNullOrEmpty(keyword)) {
            throw new IllegalArgumentException("Search keyword is required.");
        }

        return productRepository.searchByName(keyword);
    }

    public List<Product> searchProductsByCategory(String categoryName) throws SQLException {
        if (isNullOrEmpty(categoryName)) {
            throw new IllegalArgumentException("Search category is required.");
        }

        return productRepository.searchByCategory(categoryName);
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    private void trimProductFields(Product product) {
        if (product != null && product.getName() != null) {
            product.setName(product.getName().trim());
        }

        if (product != null && product.getDescription() != null) {
            product.setDescription(product.getDescription().trim());
        }
    }

    private void validateProductInput(Product product) {
        trimProductFields(product);

        if (isNullOrEmpty(product.getName())) {
            throw new IllegalArgumentException("Name is required.");
        }

        if (product.getPrice() == null || product.getPrice().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price is required and cannot be negative.");
        }

        if (product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity is required and cannot be negative.");
        }
    }
}

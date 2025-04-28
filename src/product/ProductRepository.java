package product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepository {
    private final Connection connection;

    public ProductRepository(Connection connection) {
        this.connection = connection;
    }

    public Product create(Product product) throws SQLException {
        resolveManufacturerFromName(product);
        String query = "INSERT INTO products (manufacturer_id, name, description, price, stock_quantity) " +
                       "VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            if (product.getManufacturerId() == null) {
                pstmt.setNull(1, Types.INTEGER);
            } else {
                pstmt.setInt(1, product.getManufacturerId());
            }

            pstmt.setString(2, product.getName());
            pstmt.setObject(3, product.getDescription());
            pstmt.setBigDecimal(4, product.getPrice());
            pstmt.setInt(5, product.getStockQuantity());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int productId = rs.getInt(1);

                        return new Product(
                                productId,
                                product.getManufacturerId(),
                                product.getName(),
                                product.getManufacturerName(),
                                product.getDescription(),
                                product.getPrice(),
                                product.getStockQuantity()
                        );
                    }
                }
            }

            throw new SQLException("Failed to create the product. No rows were affected in the database operation.");
        } catch (SQLException e) {
            throw new SQLException("Error inserting the product into the database: " + e.getMessage(), e);
        }
    }

    public List<Product> getAll() throws SQLException {
        String query = """
            SELECT p.product_id, p.name AS product_name,
                   m.manufacturer_id, m.name AS manufacturer_name
            FROM products p
            LEFT JOIN manufacturers m ON p.manufacturer_id = m.manufacturer_id
        """;
        List<Product> products = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int manufacturerId = rs.getObject("manufacturer_id") != null
                        ? rs.getInt("manufacturer_id")
                        : -1;

                Product product = new Product(
                        rs.getInt("product_id"),
                        manufacturerId,
                        rs.getString("product_name"),
                        rs.getString("manufacturer_name")
                );

                products.add(product);
            }
        }

        return products;
    }

    public Product getById(int productId) throws SQLException {
        String query = """
            SELECT p.product_id, p.name AS product_name,
                   p.description, p.price, p.stock_quantity,
                   m.manufacturer_id, m.name AS manufacturer_name
            FROM products p
            LEFT JOIN manufacturers m ON p.manufacturer_id = m.manufacturer_id
            WHERE p.product_id = ?
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Product(
                            rs.getInt("product_id"),
                            rs.getInt("manufacturer_id"),
                            rs.getString("product_name"),
                            rs.getString("manufacturer_name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock_quantity")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    public Product update(int productId, Product updatedProduct) throws SQLException {
        String query = "UPDATE products SET price = ?, stock_quantity = ? WHERE product_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setBigDecimal(1, updatedProduct.getPrice());
            pstmt.setInt(2, updatedProduct.getStockQuantity());
            pstmt.setInt(3, productId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Failed to update the product. " +
                        "No rows were affected in the database operation.");
            }
        }

        return getById(productId);
    }

    public void delete(int productId) throws SQLException {
        String query = "DELETE FROM products WHERE product_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, productId);
            pstmt.executeUpdate();
        }
    }

    public List<Product> searchByName(String keyword) throws SQLException {
        String query = """
            SELECT p.product_id, p.name AS product_name,
                   p.description, p.price, p.stock_quantity,
                   m.manufacturer_id, m.name AS manufacturer_name
            FROM products p
            LEFT JOIN manufacturers m ON p.manufacturer_id = m.manufacturer_id
            WHERE LOWER(p.name) LIKE ?
        """;
        List<Product> products = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + keyword.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getInt("manufacturer_id"),
                            rs.getString("product_name"),
                            rs.getString("manufacturer_name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock_quantity")
                    );

                    products.add(product);
                }
            }
        }

        return products;
    }

    public List<Product> searchByCategory(String categoryName) throws SQLException {
        String query = """
            SELECT p.product_id, p.name AS product_name,
                   p.description, p.price, p.stock_quantity,
                   m.manufacturer_id, m.name AS manufacturer_name
            FROM products p
            JOIN products_categories pc ON p.product_id = pc.product_id
            JOIN categories c ON pc.category_id = c.category_id
            LEFT JOIN manufacturers m ON p.manufacturer_id = m.manufacturer_id
            WHERE LOWER(c.name) LIKE ?
        """;
        List<Product> products = new ArrayList<>();

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + categoryName.toLowerCase() + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getInt("manufacturer_id"),
                            rs.getString("product_name"),
                            rs.getString("manufacturer_name"),
                            rs.getString("description"),
                            rs.getBigDecimal("price"),
                            rs.getInt("stock_quantity")
                    );

                    products.add(product);
                }
            }
        }

        return products;
    }

    /**
     * Attempts to resolve and set the manufacturer ID and name for the given product
     * by searching for a manufacturer whose name contains any of the keywords
     * from the product's name.
     *
     * @param product The product for which to resolve the manufacturer
     * @throws SQLException If a database access error occurs
     */
    private void resolveManufacturerFromName(Product product) throws SQLException {
        String[] keywords = product.getName().split(" ");
        StringBuilder queryBuilder = new StringBuilder("SELECT * FROM manufacturers WHERE ");

        for (int i = 0; i < keywords.length; i++) {
            if (i > 0) queryBuilder.append(" OR ");
            queryBuilder.append("LOWER(name) LIKE ?");
        }

        queryBuilder.append(" LIMIT 1");

        try (PreparedStatement pstmt = connection.prepareStatement(queryBuilder.toString())) {
            for (int i = 0; i < keywords.length; i++) {
                pstmt.setString(i + 1, "%" + keywords[i].toLowerCase() + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    product.setManufacturerId(rs.getInt("manufacturer_id"));
                    product.setManufacturerName(rs.getString("name"));
                } else {
                    product.setManufacturerId(-1);
                    product.setManufacturerName(null);
                }
            }
        }
    }
}

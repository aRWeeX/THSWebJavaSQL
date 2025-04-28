package customer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepository {
    private final Connection connection;

    public CustomerRepository(Connection connection) {
        this.connection = connection;
    }

    public Customer create(Customer customer) throws SQLException {
        String query = "INSERT INTO customers (name, email, phone, address, password) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, customer.getName());
            pstmt.setString(2, customer.getEmail());
            pstmt.setObject(3, customer.getPhone());
            pstmt.setObject(4, customer.getAddress());
            pstmt.setString(5, customer.getPassword());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int customerId = rs.getInt(1);

                        return new Customer(
                                customerId,
                                customer.getName(),
                                customer.getEmail(),
                                customer.getPhone(),
                                customer.getAddress(),
                                customer.getPassword()
                        );
                    }
                }
            }

            throw new SQLException("Failed to create the customer. No rows were affected in the database operation.");
        } catch (SQLException e) {
            throw new SQLException("Error inserting the customer into the database: " + e.getMessage(), e);
        }
    }

    public List<Customer> getAll() throws SQLException {
        String query = "SELECT customer_id, name, email FROM customers";
        List<Customer> customers = new ArrayList<>();

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Customer customer = new Customer(
                        rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("email")
                );

                customers.add(customer);
            }
        }

        return customers;
    }

    public Customer getById(int customerId) throws SQLException {
        String query = "SELECT * FROM customers WHERE customer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("customer_id"),
                            rs.getString("name"),
                            rs.getString("email"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("password")
                    );
                } else {
                    return null;
                }
            }
        }
    }

    public Customer update(int customerId, Customer updatedCustomer) throws SQLException {
        String query = "UPDATE customers SET name = ?, email = ?, phone = ?, address = ?, password = ? " +
                       "WHERE customer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, updatedCustomer.getName());
            pstmt.setString(2, updatedCustomer.getEmail());
            pstmt.setObject(
                    3,
                    updatedCustomer.getPhone() == null || updatedCustomer.getPhone().isEmpty()
                            ? null
                            : updatedCustomer.getPhone());
            pstmt.setObject(
                    4,
                    updatedCustomer.getAddress() == null || updatedCustomer.getAddress().isEmpty()
                            ? null
                            : updatedCustomer.getAddress());
            pstmt.setString(5, updatedCustomer.getPassword());
            pstmt.setInt(6, customerId);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Failed to update the customer. " +
                        "No rows were affected in the database operation.");
            }
        }

        return getById(customerId);
    }

    public void delete(int customerId) throws SQLException {
        String query = "DELETE FROM customers WHERE customer_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, customerId);
            pstmt.executeUpdate();
        }
    }
}

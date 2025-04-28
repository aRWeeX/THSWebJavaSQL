package customer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerService {
    private static final String EMAIL_REGEX =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) throws SQLException {
        validateCustomerInput(customer);
        return customerRepository.create(customer);
    }

    public List<Customer> getAllCustomers() throws SQLException {
        return customerRepository.getAll();
    }

    public Customer getCustomerById(int customerId) throws SQLException {
        return customerRepository.getById(customerId);
    }

    public boolean updateCustomer(Customer customer) throws SQLException {
        Customer existingCustomer = customerRepository.getById(customer.getCustomerId());

        if (existingCustomer == null) {
            throw new IllegalArgumentException("No customer was found with the provided ID: " +
                    customer.getCustomerId());
        }

        validateCustomerInput(customer);

        if (!existingCustomer.equals(customer)) {
            customerRepository.update(customer.getCustomerId(), customer);
            return true;
        } else {
            return false;
        }
    }

    public void deleteCustomer(int customerId) throws SQLException {
        Customer existingCustomer = customerRepository.getById(customerId);

        if (existingCustomer == null) {
            throw new IllegalArgumentException("No customer was found with the provided ID: " + customerId);
        }

        customerRepository.delete(customerId);
    }

    private boolean isNullOrEmpty(String string) {
        return string == null || string.trim().isEmpty();
    }

    private boolean isInvalidEmail(String email) {
        return email == null || !email.matches(EMAIL_REGEX);
    }

    private void trimCustomerFields(Customer customer) {
        if (customer != null && customer.getName() != null) {
            customer.setName(customer.getName().trim());
        }

        if (customer != null && customer.getEmail() != null) {
            customer.setEmail(customer.getEmail().trim());
        }
    }

    private void validateCustomerInput(Customer customer) {
        trimCustomerFields(customer);
        List<String> missing = new ArrayList<>();

        if (isNullOrEmpty(customer.getName())) missing.add("name");
        if (isNullOrEmpty(customer.getEmail())) missing.add("email");
        if (isNullOrEmpty(customer.getPassword())) missing.add("password");

        if (!missing.isEmpty()) {
            String message;

            if (missing.size() > 1) {
                message = "Missing required fields: " +
                        String.join(", ", missing.subList(0, missing.size() - 1)) +
                        " and " +
                        missing.get(missing.size() - 1);
            } else {
                message = "Missing required field: " + missing.get(0);
            }

            message += ".";
            throw new IllegalArgumentException(message);
        }

        if (isInvalidEmail(customer.getEmail())) {
            throw new IllegalArgumentException("Please enter a valid email address. " +
                    "Make sure it contains \"@\" and a domain name.");
        }

        if (!isNullOrEmpty(customer.getPassword()) && customer.getPassword().length() < 8) {
            throw new IllegalArgumentException("Your password must be at least 8 characters long.");
        }
    }
}

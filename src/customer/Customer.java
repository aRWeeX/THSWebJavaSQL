package customer;

import java.util.Objects;

public class Customer {
    private int customerId;
    private String name;
    private String email;
    private String phone; // May be null
    private String address; // May be null
    private String password;

    public Customer(int customerId, String name, String email) {
        this(customerId, name, email, null, null, null);
    }

    public Customer(String name, String email, String phone, String address, String password) {
        this(0, name, email, phone, address, password);
    }

    public Customer(int customerId, String name, String email, String phone, String address, String password) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.password = password;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object object) {
        if (object == null || getClass() != object.getClass()) return false;
        Customer customer = (Customer) object;

        return customerId == customer.customerId
                && Objects.equals(name, customer.name)
                && Objects.equals(email, customer.email)
                && Objects.equals(phone, customer.phone)
                && Objects.equals(address, customer.address)
                && Objects.equals(password, customer.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, name, email, phone, address, password);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + (phone != null ? phone : "N/A") + '\'' +
                ", address='" + (address != null ? address : "N/A") + '\'' +
                ", password='[HIDDEN]'" +
                '}';
    }
}

package model;

import java.util.Date;

public class Sale {

    // Fields/Attributes
    private String id;           
    private String customerName;
    private String item;
    private double price;
    private int quantity;
    private String status;       
    private String paymentStatus; 
    private Date saleDate;
    private String contactNumber;
    private String username;

    /**
    * Main constructor with ALL parameters
    * id - Auto-generated sale ID
    * customerName - Customer's name
    * item - Item purchased
    * price - Item price per unit
    * quantity - Number of units purchased
    * status - Order status ("Completed", "Pending", "Cancelled")
    * paymentStatus - Payment status ("Paid", "Unpaid")
    * saleDate - Date of sale
    * contactNumber - Customer contact number
    */
    public Sale(String id, String customerName, String item, double price,
            int quantity, String status, String paymentStatus,
            Date saleDate, String contactNumber) {
        this.id = id;
        this.customerName = customerName;
        this.item = item;
        this.price = price;
        this.quantity = quantity;
        this.status = status;
        this.paymentStatus = paymentStatus;
        this.saleDate = saleDate;
        this.contactNumber = contactNumber;

        // Get current username, but if it's "admin", maybe set to null or special value
        String currentUser = controller.SalesController.getCurrentUsername();
        if ("admin".equals(currentUser)) {
            // Admin shouldn't create sales, but if they do, mark them specially
            this.username = "SYSTEM";  // Or null, or "admin-system"
        } else {
            this.username = currentUser;
        }
    }

    /**
    * Constructor with default current date.
    * id - Auto-generated sale ID
    * customerName - Customer's name
    * item - Item purchased
    * price - Item price per unit
    * quantity - Number of units purchased
    * status - Order status ("Completed", "Pending", "Cancelled")
    * paymentStatus - Payment status ("Paid", "Unpaid")
    * contactNumber - Customer contact number
    */
    public Sale(String id, String customerName, String item, double price,
            int quantity, String status, String paymentStatus,
            String contactNumber) {
        this(id, customerName, item, price, quantity, status, paymentStatus,
                new Date(), contactNumber);
    }

    // Add getter and setter for username
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    // Calculate total = price * quantity
    public double getTotal() {
        return price * quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    /**
    * Returns a string representation of the sale.
    * Returns: Formatted string with ID, customer name, item, and total
    */
    @Override
    public String toString() {
        return id + " - " + customerName + " - " + item + " - $" + getTotal();
    }
}

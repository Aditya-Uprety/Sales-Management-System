package model;

import java.util.Date;

public class Sale {

    // Fields/Attributes
    private String id;           // Auto-generated
    private String customerName;
    private String item;
    private double price;
    private int quantity;
    private String status;       // "Completed", "Pending", "Cancelled"
    private String paymentStatus; // "Paid", "Unpaid"
    private Date saleDate;
    private String contactNumber;

    // Main constructor with ALL parameters 
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
    }
    
    public Sale(String id, String customerName, String item, double price,
            int quantity, String status, String paymentStatus,
            String contactNumber) {
        this(id, customerName, item, price, quantity, status, paymentStatus,
                new Date(), contactNumber); 
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

    public void setPaymentStatus(String paymentMethod) {
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

    // For displaying in console or debugging
    @Override
    public String toString() {
        return id + " - " + customerName + " - " + item + " - $" + getTotal();
    }
}

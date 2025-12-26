package controller;

import model.Sale;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
public class SalesController {

    // Main data storage - ArrayList of Sale objects
    private static ArrayList<Sale> salesList = new ArrayList<>();

    // Counter for auto-generating IDs
    private static int saleCounter = 1;

    // Sample data for testing (optional)
    static {
        // Add sample sales with DIFFERENT dates
        Calendar cal = Calendar.getInstance();

        // Sale 1: 3 days ago
        cal.add(Calendar.DATE, -3);
        addSale(new Sale(generateSaleId(), "John Smith", "Laptop", 850.00, 2,
                "Completed", "Paid", cal.getTime(), "9876543210"));

        // Sale 2: Yesterday
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        addSale(new Sale(generateSaleId(), "Emma Johnson", "Mouse", 25.50, 3,
                "Pending", "Unpaid", cal.getTime(), "9123456780"));

        // Sale 3: Today (now)
        addSale(new Sale(generateSaleId(), "Robert Brown", "Keyboard", 45.99, 1,
                "Completed", "Paid", new Date(), "9988776655"));

        // Sale 4: 5 days ago
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -5);
        addSale(new Sale(generateSaleId(), "Alice White", "Monitor", 299.99, 1,
                "Completed", "Paid", cal.getTime(), "9112233445"));

        // Sale 5: 7 days ago
        cal.add(Calendar.DATE, -2); // Another 2 days back
        addSale(new Sale(generateSaleId(), "Bob Green", "USB Cable", 12.99, 5,
                "Completed", "Paid", cal.getTime(), "9334455667"));
    }

    public static ArrayList<Sale> searchByPaymentStatus(String paymentStatus) {
        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getPaymentStatus().equalsIgnoreCase(paymentStatus)) {
                results.add(sale);
            }
        }
        return results;
    }

    // Sort by date (newest first)
    public static void sortByDate() {
        Collections.sort(salesList, new Comparator<Sale>() {
            @Override
            public int compare(Sale s1, Sale s2) {
                return s2.getSaleDate().compareTo(s1.getSaleDate()); // Newest first
            }
        });
    }

// Sort by price (low to high)
    public static void sortByPrice() {
        Collections.sort(salesList, new Comparator<Sale>() {
            @Override
            public int compare(Sale s1, Sale s2) {
                return Double.compare(s1.getPrice(), s2.getPrice()); // Low to high
            }
        });
    }

    // Generate unique Sale ID (e.g., "SALE001", "SALE002")
    public static String generateSaleId() {
        String id = "SALE" + String.format("%03d", saleCounter);
        saleCounter++;
        return id;
    }

    // Add a new sale to the list
    public static boolean addSale(Sale sale) {
        try {
            salesList.add(sale);
            return true;
        } catch (Exception e) {
            System.err.println("Error adding sale: " + e.getMessage());
            return false;
        }
    }

    // Get all sales
    public static ArrayList<Sale> getAllSales() {
        return salesList;
    }

    // Get sale by ID
    public static Sale getSaleById(String id) {
        for (Sale sale : salesList) {
            if (sale.getId().equals(id)) {
                return sale;
            }
        }
        return null; // Not found
    }

    // Get total number of sales
    public static int getTotalSalesCount() {
        return salesList.size();
    }

    // Get number of pending orders
    public static int getPendingOrdersCount() {
        int count = 0;
        for (Sale sale : salesList) {
            if (sale.getStatus().equalsIgnoreCase("Pending")) {
                count++;
            }
        }
        return count;
    }

    // Get total revenue (sum of all sale totals)
    public static double getTotalRevenue() {
        double total = 0;
        for (Sale sale : salesList) {
            total += sale.getTotal(); // price * quantity
        }
        return total;
    }

    // Get recent sales (last N sales)
    public static ArrayList<Sale> getRecentSales(int count) {
        ArrayList<Sale> recent = new ArrayList<>();
        int startIndex = Math.max(0, salesList.size() - count);

        for (int i = startIndex; i < salesList.size(); i++) {
            recent.add(salesList.get(i));
        }
        return recent;
    }

    // Update an existing sale
    public static boolean updateSale(String id, Sale updatedSale) {
        for (int i = 0; i < salesList.size(); i++) {
            if (salesList.get(i).getId().equals(id)) {
                salesList.set(i, updatedSale);
                return true;
            }
        }
        return false; // Sale not found
    }

    // Delete a sale by ID
    public static boolean deleteSale(String id) {
        for (int i = 0; i < salesList.size(); i++) {
            if (salesList.get(i).getId().equals(id)) {
                salesList.remove(i);
                return true;
            }
        }
        return false; // Sale not found
    }

    // Search sales by customer name (partial match)
    public static ArrayList<Sale> searchByCustomerName(String name) {
        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getCustomerName().toLowerCase().contains(name.toLowerCase())) {
                results.add(sale);
            }
        }
        return results;
    }

    // Search sales by status
    public static ArrayList<Sale> searchByStatus(String status) {
        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getStatus().equalsIgnoreCase(status)) {
                results.add(sale);
            }
        }
        return results;
    }

    // Get sales count for dashboard display
    public static String[] getDashboardStats() {
        String[] stats = new String[3];

        // 1. Total Orders
        stats[0] = String.valueOf(getTotalSalesCount());

        // 2. Pending Orders
        stats[1] = String.valueOf(getPendingOrdersCount());

        // 3. Items Sold (SUM of all quantities) - NOT Total Revenue
        int totalItemsSold = 0;
        for (Sale sale : salesList) {
            totalItemsSold += sale.getQuantity();
        }
        stats[2] = String.valueOf(totalItemsSold);

        return stats;
    }

    // For debugging: Print all sales
    public static void printAllSales() {
        System.out.println("=== ALL SALES ===");
        for (Sale sale : salesList) {
            System.out.println(sale);
        }
        System.out.println("=================");
    }

    // this is just a test delet it later           IMPORTANT READ THIS LINE
    public static void main(String[] args) {
        // Test 1: Print sample data
        printAllSales();

        // Test 2: Generate new ID
        System.out.println("Next ID: " + generateSaleId());

        // Test 3: Dashboard stats
        String[] stats = getDashboardStats();
        System.out.println("Total Orders: " + stats[0]);
        System.out.println("Pending: " + stats[1]);
        System.out.println("Total Sales: " + stats[2]);

        // Test 4: Search
        System.out.println("\nSearch for 'john':");
        ArrayList<Sale> results = searchByCustomerName("john");
        for (Sale s : results) {
            System.out.println("Found: " + s.getCustomerName());
        }
    }
}

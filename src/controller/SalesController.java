package controller;

import model.Sale;
import java.util.ArrayList;
import java.util.Calendar;


/**
 * Manages sales operations including CRUD, searching, sorting, and user management.
 * This controller serves as the central business logic layer for the sales system.
 */
public class SalesController {

    /**
     * Custom stack implementation for deleted sales to support undo functionality.
     * Uses LIFO (Last-In-First-Out) principle to track recently deleted sales.
     */
    public static class DeletedSalesStack {

        private ArrayList<Sale> items;

        public DeletedSalesStack() {
            items = new ArrayList<>();
        }
        
        /**
         * Adds a sale to the top of the deletion stack.
         * sale - The sale to be marked as deleted
         */
        public void push(Sale sale) {
            items.add(sale);
        }
        
        /**
         * Removes and returns the most recently deleted sale from the stack.
         * Returns null if the stack is empty. 
         * Returns: The most recent deleted sale, or null if none exist
         */
        public Sale pop() {
            if (items.size() == 0) {
                return null;
            }
            return items.remove(items.size() - 1);
        }

        /**
        * Returns the most recently deleted sale without removing it.
        * Returns null if the stack is empty.
        * Returns: The most recent deleted sale, or null if empty
        */
        public Sale peek() {
            if (items.size() == 0) {
                return null;
            }
            return items.get(items.size() - 1);
        }

        public boolean isEmpty() {
            return items.size() == 0;
        }
    }

// Custom Queue for recent sales
    public static class RecentSalesQueue {

        private ArrayList<Sale> items;

        public RecentSalesQueue() {
            items = new ArrayList<>();
        }

        public void offer(Sale sale) {
            items.add(sale);
        }

        public Sale poll() {
            if (items.size() == 0) {
                return null;
            }
            return items.remove(0);
        }

        public int size() {
            return items.size();
        }

        public boolean isEmpty() {
            return items.size() == 0;
        }

        public ArrayList<Sale> getItems() {
            return new ArrayList<>(items);
        }
    }

    // Main data storage - ArrayList of Sale objects
    private static ArrayList<Sale> salesList = new ArrayList<>();
    private static DeletedSalesStack deletedSalesStack = new DeletedSalesStack();
    private static RecentSalesQueue recentSalesQueue = new RecentSalesQueue();

    // Counter for auto-generating IDs
    private static int saleCounter = 1;

    /**
     * Sorts all sales by quantity using the bubble sort algorithm.
     * Arranges sales from smallest to largest quantity.
     * Includes optimization to stop early if array is already sorted.
     */
    public static void bubbleSortByQuantity() {
        int n = salesList.size();

        // Outer loop for each pass
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;  // Optimization flag

            // Inner loop for comparing adjacent elements
            for (int j = 0; j < n - i - 1; j++) {
                Sale currentSale = salesList.get(j);
                Sale nextSale = salesList.get(j + 1);

                // Compare quantities (ascending order: smallest to largest)
                if (currentSale.getQuantity() > nextSale.getQuantity()) {
                    // Swap the sales
                    salesList.set(j, nextSale);
                    salesList.set(j + 1, currentSale);
                    swapped = true;
                }
            }

            // If no swaps in this pass, array is already sorted
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Searches for sales with specific payment status for the currently logged-in user.
     * Regular users can only search their own sales, while admins can search all.
     * paymentStatus - The payment status to search for ("Paid" or "Unpaid")
     * Returns: List of matching sales for the current user
     */
    public static ArrayList<Sale> searchByPaymentStatusForCurrentUser(String paymentStatus) {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return new ArrayList<>();
        }

        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getUsername() != null
                    && sale.getUsername().equals(currentUsername)
                    && sale.getPaymentStatus().equalsIgnoreCase(paymentStatus)) {
                results.add(sale);
            }
        }
        return results;
    }

    /**
     * Retrieves all sales belonging to the currently logged-in user.
     * Returns empty list for guests or users without any sales.
     * Returns: List of sales for the current user
     */
    public static ArrayList<Sale> getSalesForCurrentUser() {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return new ArrayList<>();
        }

        ArrayList<Sale> userSales = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getUsername() != null
                    && sale.getUsername().equals(currentUsername)) {
                userSales.add(sale);
            }
        }
        return userSales;
    }

    /**
     * Searches all sales by customer name (admin only).
     * Performs case-insensitive partial matching on customer names.
     * name - The customer name (or part of name) to search for
     * Returns: List of sales matching the customer name
     */
    public static ArrayList<Sale> searchByCustomerName(String name) {
        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getCustomerName().toLowerCase().contains(name.toLowerCase())) {
                results.add(sale);
            }
        }
        return results;
    }

    /**
     * Searches all sales by payment status (admin only).
     * Performs case-insensitive matching on payment status.
     * paymentStatus - The payment status to search for ("Paid" or "Unpaid")
     * Returns: List of all sales with matching payment status
     */
    public static ArrayList<Sale> searchByPaymentStatus(String paymentStatus) {
        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getPaymentStatus().equalsIgnoreCase(paymentStatus)) {
                results.add(sale);
            }
        }
        return results;
    }

    /**
     * Searches all sales by order status (admin only).
     * Performs case-insensitive matching on order status.
     * status - The status to search for ("Pending", "Completed", "Cancelled")
     * Returns: List of all sales with matching status
     */
    public static ArrayList<Sale> searchByStatus(String status) {
        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getStatus().equalsIgnoreCase(status)) {
                results.add(sale);
            }
        }
        return results;
    }

    /**
     * Searches for a sale by ID using binary search algorithm (admin only).
     * Requires sorted list for binary search to work correctly.
     * searchId - The sale ID to search for
     * Returns: The sale if found, null if not found
     */
    public static Sale binarySearchById(String searchId) {
        // First, sort sales by ID for binary search
        ArrayList<Sale> sortedSales = new ArrayList<>(salesList);
        sortSalesById(sortedSales);

        int left = 0;
        int right = sortedSales.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Sale midSale = sortedSales.get(mid);
            String midId = midSale.getId();

            int comparison = midId.compareToIgnoreCase(searchId);

            if (comparison == 0) {
                return midSale;
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null; // Not found
    }

    /**
     * Sorts all sales by date in descending order (newest first).
     * Uses bubble sort algorithm with optimization for early exit.
     * Modifies the salesList directly.
     */
    public static void sortByDate() {
        int n = salesList.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                Sale current = salesList.get(j);
                Sale next = salesList.get(j + 1);

                if (current.getSaleDate().compareTo(next.getSaleDate()) < 0) {
                    salesList.set(j, next);
                    salesList.set(j + 1, current);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Sorts all sales by price in ascending order (lowest to highest).
     * Uses bubble sort algorithm with optimization for early exit.
     * Modifies the salesList directly.
     */
    public static void sortByPrice() {
        int n = salesList.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                Sale current = salesList.get(j);
                Sale next = salesList.get(j + 1);

                if (current.getPrice() > next.getPrice()) {
                    salesList.set(j, next);
                    salesList.set(j + 1, current);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Helper method to sort a list of sales by ID in ascending order.
     * Uses bubble sort algorithm for simple in-place sorting.
     * list - The list of sales to sort (modified in place)
     */
    private static void sortSalesById(ArrayList<Sale> list) {
        int n = list.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                Sale current = list.get(j);
                Sale next = list.get(j + 1);

                if (current.getId().compareToIgnoreCase(next.getId()) > 0) {
                    list.set(j, next);
                    list.set(j + 1, current);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }
    }

    /**
     * Helper method to reverse the order of a list of sales in place.
     * Uses two-pointer technique to swap elements from both ends.
     * list - The list of sales to reverse (modified in place)
     */
    private static void reverseSalesList(ArrayList<Sale> list) {
        int start = 0;
        int end = list.size() - 1;

        while (start < end) {
            Sale temp = list.get(start);
            list.set(start, list.get(end));
            list.set(end, temp);
            start++;
            end--;
        }
    }

     /**
     * Generates a unique sale ID using an internal counter.
     * Format: "SALE" followed by 3-digit number (e.g., "SALE001", "SALE123")
     * Returns: A new unique sale ID
     */
    public static String generateSaleId() {
        String id = "SALE" + String.format("%03d", saleCounter);
        saleCounter++;
        return id;
    }

    /**
     * Adds a new sale to the system and updates recent sales queue.
     * Also limits the recent sales queue to 5 items maximum.
     * sale - The sale object to add
     * Returns: true if addition successful, false on error
     */
    public static boolean addSale(Sale sale) {
        try {
            salesList.add(sale);

            // STEP 6: Add to recent sales queue
            recentSalesQueue.offer(sale);
            if (recentSalesQueue.size() > 5) {
                recentSalesQueue.poll(); // Remove oldest if more than 5
            }

            return true;
        } catch (Exception e) {
            System.err.println("Error adding sale: " + e.getMessage());
            return false;
        }
    }

     /**
     * Returns all sales in the system.
     * Admin sees all sales, regular users see filtered view via getSaleById.
     * Returns: List of all sales in the system
     */
    public static ArrayList<Sale> getAllSales() {
        return salesList;
    }

    /**
     * Retrieves a sale by its ID with authorization check.
     * Regular users can only access their own sales, while admins can access all.
     * id - The sale ID to look up
     * Returns: The sale if found and authorized, null otherwise
     */
    public static Sale getSaleById(String id) {
        for (Sale sale : salesList) {
            if (sale.getId().equals(id)) {
                // Check if user can access this sale
                if (isAuthorizedToView(sale)) {
                    return sale;
                } else {
                    System.err.println("Unauthorized access attempt to sale: " + id);
                    return null; // Not authorized to view
                }
            }
        }
        return null; // Not found
    }

    /**
     * Checks if the current user is authorized to view a specific sale.
     * Admins can view any sale, regular users can only view their own.
     * sale - The sale to check authorization for
     * Returns: true if authorized to view, false otherwise
     */
    private static boolean isAuthorizedToView(Sale sale) {
        if (isAdmin()) {
            return true; // Admin can view everything
        }

        if (sale.getUsername() == null) {
            return false;
        }

        // Regular user can only view their own sales
        return sale.getUsername().equals(currentUsername);
    }

    /**
     * Returns the total number of sales in the system.
     * Returns: Total count of all sales
     */
    public static int getTotalSalesCount() {
        return salesList.size();
    }

    /**
     * Counts the number of sales with "Pending" status.
     * Returns: Count of pending orders
     */
    public static int getPendingOrdersCount() {
        int count = 0;
        for (Sale sale : salesList) {
            if (sale.getStatus().equalsIgnoreCase("Pending")) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculates total revenue based on user role.
     * Admins see revenue from all sales, regular users only see their own revenue.
     * Returns: Total revenue amount
     */
    public static double getTotalRevenue() {
        if (isAdmin()) {
            // Admin gets total revenue from all sales
            double total = 0;
            for (Sale sale : salesList) {
                total += sale.getTotal();
            }
            return total;
        } else {
            // Regular user gets only their revenue
            return getTotalRevenueForCurrentUser();
        }
    }

    /**
     * Calculates total revenue for the currently logged-in user.
     * Only includes sales that belong to the current user.
     * Returns: Total revenue amount for the current user
     */
    public static double getTotalRevenueForCurrentUser() {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return 0.0;
        }

        double total = 0;
        for (Sale sale : salesList) {
            if (sale.getUsername() != null
                    && sale.getUsername().equals(currentUsername)) {
                total += sale.getTotal();
            }
        }
        return total;
    }

    /**
     * Retrieves the most recent sales for the current user.
     * count - Maximum number of recent sales to return
     * Returns: List of recent sales for the current user
     */
    public static ArrayList<Sale> getRecentSalesForCurrentUser(int count) {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return new ArrayList<>();
        }

        ArrayList<Sale> userSales = getSalesForCurrentUser();
        ArrayList<Sale> recent = new ArrayList<>();
        int startIndex = Math.max(0, userSales.size() - count);

        for (int i = startIndex; i < userSales.size(); i++) {
            recent.add(userSales.get(i));
        }
        return recent;
    }

     /**
     * Returns recent sales queue with user-based filtering.
     * Admins see all recent sales, regular users see only their own.
     * Returns: Queue containing recent sales based on user role
     */
    public static RecentSalesQueue getRecentSalesQueue() {
        if (isAdmin()) {
            // Admin sees all recent sales
            RecentSalesQueue adminQueue = new RecentSalesQueue();
            for (Sale sale : recentSalesQueue.getItems()) {
                adminQueue.offer(sale);
            }
            return adminQueue;
        } else {
            // Regular users see only their recent sales
            return getRecentSalesQueueForCurrentUser();
        }
    }
    
    /**
     * Internal method to get recent sales queue filtered for current user.
     * Returns only the 5 most recent sales belonging to the current user.
     * Returns: Queue of recent sales for current user
     */
    private static RecentSalesQueue getRecentSalesQueueForCurrentUser() {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return new RecentSalesQueue();
        }

        RecentSalesQueue userQueue = new RecentSalesQueue();
        int count = 0;

        // Get items and reverse to get most recent first
        ArrayList<Sale> reversedList = new ArrayList<>(recentSalesQueue.getItems());
        reverseSalesList(reversedList);

        for (Sale sale : reversedList) {
            if (sale.getUsername() != null
                    && sale.getUsername().equals(currentUsername)) {
                userQueue.offer(sale);
                count++;
                if (count >= 5) {
                    break;
                }
            }
        }

        // Reverse back to maintain chronological order
        ArrayList<Sale> tempList = new ArrayList<>(userQueue.getItems());
        reverseSalesList(tempList);
        RecentSalesQueue finalQueue = new RecentSalesQueue();
        for (Sale sale : tempList) {
            finalQueue.offer(sale);
        }
        return finalQueue;
    }

    /**
     * Updates an existing sale with proper authorization and data preservation.
     * Preserves the original username and date to maintain data integrity.
     * id - The ID of the sale to update
     * updatedSale - The sale object with updated information
     * Returns: true if update successful, false if not found or unauthorized
     */
    public static boolean updateSale(String id, Sale updatedSale) {
        for (int i = 0; i < salesList.size(); i++) {
            Sale existingSale = salesList.get(i);

            if (existingSale.getId().equals(id)) {
                // Check if user is authorized to update this sale
                if (!isAuthorizedToModify(existingSale)) {
                    System.err.println("Unauthorized update attempt by: " + currentUsername);
                    return false; // Not authorized
                }

                updatedSale.setUsername(existingSale.getUsername());

                salesList.set(i, updatedSale);
                return true;
            }
        }
        return false; // Sale not found
    }

    /**
     * Checks if the current user is authorized to modify a specific sale.
     * Admins can modify any sale, regular users can only modify their own.
     * sale - The sale to check authorization for
     * Returns: true if authorized to modify, false otherwise
     */
    private static boolean isAuthorizedToModify(Sale sale) {
        if (isAdmin()) {
            return true; // Admin can modify anything
        }

        if (sale.getUsername() == null) {
            return false;
        }

        // Regular user can only modify their own sales
        return sale.getUsername().equals(currentUsername);
    }

    /**
     * Deletes a sale by ID with authorization check.
     * Deleted sales are pushed to the undo stack for possible restoration.
     * id - The sale ID to delete
     * Returns: true if deletion successful, false if not found or unauthorized
     */
    public static boolean deleteSale(String id) {
        for (int i = 0; i < salesList.size(); i++) {
            Sale sale = salesList.get(i);

            if (sale.getId().equals(id)) {
                // Check if user is authorized to delete this sale
                if (!isAuthorizedToModify(sale)) {
                    System.err.println("Unauthorized delete attempt by: " + currentUsername);
                    return false; // Not authorized
                }

                // Push to stack before deleting
                deletedSalesStack.push(sale);
                salesList.remove(i);
                return true;
            }
        }
        return false; // Sale not found
    }

    /**
     * Searches for a sale by ID using binary search for current user only.
     * Searches only within sales belonging to the current user.
     * searchId - The sale ID to search for
     * Returns: The sale if found and belongs to user, null otherwise
     */
    public static Sale binarySearchByIdForCurrentUser(String searchId) {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return null;
        }

        // create sorted copy by ID for binary search
        ArrayList<Sale> userSales = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getUsername() != null
                    && sale.getUsername().equals(currentUsername)) {
                userSales.add(sale);
            }
        }

        sortSalesById(userSales);

        int left = 0;
        int right = userSales.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            Sale midSale = userSales.get(mid);
            String midId = midSale.getId();

            int comparison = midId.compareToIgnoreCase(searchId);

            if (comparison == 0) {
                return midSale;
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null;
    }

    /**
     * Undoes the most recent deletion by restoring a sale from the undo stack.
     * Only works if user is authorized to modify the restored sale.
     * Returns: The restored sale, or null if no deletions to undo
     */
    public static Sale undoDelete() {
        if (!deletedSalesStack.isEmpty()) {
            Sale sale = deletedSalesStack.peek(); // Look at top without removing

            // Check if user is authorized to restore this sale
            if (!isAuthorizedToModify(sale)) {
                System.err.println("Unauthorized undo attempt by: " + currentUsername);
                return null; // Not authorized
            }

            sale = deletedSalesStack.pop();
            salesList.add(sale);
            return sale;
        }
        return null;
    }

    /**
     * Searches current user's sales by customer name with partial matching.
     * Performs case-insensitive partial matching on customer names.
     * name - The name (or part of name) to search for
     * Returns: List of matching sales for the current user
     */
    public static ArrayList<Sale> searchByCustomerNameForCurrentUser(String name) {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return new ArrayList<>();
        }

        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getUsername() != null
                    && sale.getUsername().equals(currentUsername)
                    && sale.getCustomerName().toLowerCase().contains(name.toLowerCase())) {
                results.add(sale);
            }
        }
        return results;
    }
    
    /**
     * Searches current user's sales by order status.
     * Performs case-insensitive matching on order status.
     * status - The status to search for
     * Returns: List of matching sales for the current user
     */
    public static ArrayList<Sale> searchByStatusForCurrentUser(String status) {
        if (currentUser == null || currentUsername.equals("Guest")) {
            return new ArrayList<>();
        }

        ArrayList<Sale> results = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getUsername() != null
                    && sale.getUsername().equals(currentUsername)
                    && sale.getStatus().equalsIgnoreCase(status)) {
                results.add(sale);
            }
        }
        return results;
    }

    /**
     * Generates dashboard statistics for display.
     * Includes total orders, pending orders, and total items sold.
     * Returns: Array of statistics [totalOrders, pendingOrders, itemsSold]
     */
    public static String[] getDashboardStats() {
        String[] stats = new String[3];

        // Total Orders
        stats[0] = String.valueOf(getTotalSalesCount());

        // Pending Orders
        stats[1] = String.valueOf(getPendingOrdersCount());

        // Items Sold (SUM of all quantities) - NOT Total Revenue
        int totalItemsSold = 0;
        for (Sale sale : salesList) {
            totalItemsSold += sale.getQuantity();
        }
        stats[2] = String.valueOf(totalItemsSold);

        return stats;
    }

    /**
     * Prints all sales to console for debugging purposes.
     * Shows different views for admin vs regular users.
     * Includes user context information.
     */
    public static void printAllSales() {
        System.out.println("=== ALL SALES ===");
        System.out.println("Current User: " + currentUsername);
        System.out.println("Is Admin: " + isAdmin());

        if (isAdmin()) {
            // Admin sees all
            for (Sale sale : salesList) {
                System.out.println(sale + " [User: " + (sale.getUsername() != null ? sale.getUsername() : "Unknown") + "]");
            }
        } else {
            // Regular user sees only their sales
            int count = 0;
            for (Sale sale : salesList) {
                if (sale.getUsername() != null
                        && sale.getUsername().equals(currentUsername)) {
                    System.out.println(sale);
                    count++;
                }
            }
            System.out.println("Total visible: " + count + " sales");
        }
        System.out.println("=================");
    }

// USER MANAGEMENT SYSTEM (Within SalesController)
    
    /**
     * Constructs a new User with default statistics.
     * username - The username for the new user
     * password - The password for the new user
     */
    public static class User {

        private String username;
        private String password;
        private int totalOrders;
        private int pendingOrders;
        private int itemsSold;

        public User(String username, String password) {
            this.username = username;
            this.password = password;
            this.totalOrders = 0;
            this.pendingOrders = 0;
            this.itemsSold = 0;
        }
        
        /**
        * Retrieves all sales belonging to the current user.
        * Returns empty list for guests or users without sales.
        * Returns: List of sales for the current user
        */
        public static ArrayList<Sale> getSalesForCurrentUser() {
            if (currentUser == null || currentUsername.equals("Guest")) {
                return new ArrayList<>(); 
            }

            ArrayList<Sale> userSales = new ArrayList<>();
            for (Sale sale : salesList) {
                if (sale.getUsername() != null && sale.getUsername().equals(currentUsername)) {
                    userSales.add(sale);
                }
            }
            return userSales;
        }

        /**
        * Gets total sales count for current user.
        * Returns: Number of sales belonging to the current user
        */
        public static int getTotalSalesCountForCurrentUser() {
            return getSalesForCurrentUser().size();
        }

        /**
        * Gets pending orders count for current user.
        * Returns: Number of pending sales belonging to the current user
        */
        public static int getPendingOrdersCountForCurrentUser() {
            int count = 0;
            for (Sale sale : getSalesForCurrentUser()) {
                if (sale.getStatus().equalsIgnoreCase("Pending")) {
                    count++;
                }
            }
            return count;
        }

        /**
         * Gets total items sold for current user.
         * Returns: Total quantity of items sold by the current user
         */
        public static int getItemsSoldForCurrentUser() {
            int total = 0;
            for (Sale sale : getSalesForCurrentUser()) {
                total += sale.getQuantity();
            }
            return total;
        }
        
        /**
        * Generates dashboard statistics for current user.
        * Returns: Array of user-specific statistics [totalOrders, pendingOrders, itemsSold]
        */
        public static String[] getDashboardStatsForCurrentUser() {
            String[] stats = new String[3];

            if (currentUser == null || currentUsername.equals("Guest")) {
                stats[0] = "0";
                stats[1] = "0";
                stats[2] = "0";
                return stats;
            }

            stats[0] = String.valueOf(getTotalSalesCountForCurrentUser());
            stats[1] = String.valueOf(getPendingOrdersCountForCurrentUser());
            stats[2] = String.valueOf(getItemsSoldForCurrentUser());

            return stats;
        }

        // Getters and Setters
        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getTotalOrders() {
            return totalOrders;
        }

        public void setTotalOrders(int totalOrders) {
            this.totalOrders = totalOrders;
        }

        public int getPendingOrders() {
            return pendingOrders;
        }

        public void setPendingOrders(int pendingOrders) {
            this.pendingOrders = pendingOrders;
        }

        public int getItemsSold() {
            return itemsSold;
        }

        public void setItemsSold(int itemsSold) {
            this.itemsSold = itemsSold;
        }

        /**
        * Updates user statistics with new values.
        * total - New total orders count
        * pending - New pending orders count
        * sold - New items sold count
        */
        public void updateStats(int total, int pending, int sold) {
            this.totalOrders = total;
            this.pendingOrders = pending;
            this.itemsSold = sold;
        }
    }

    // User management system
    
    /**
    * Gets current username for sales tracking.
    * Returns: Current username or "Guest" if not logged in
    */
    private static ArrayList<User> users = new ArrayList<>();
    private static User currentUser = null;
    private static String currentUsername = "Guest"; // Default username

    static {
        // Add default admin user
        users.add(new User("admin", "admin"));
    }

    // Get current username for sales
    public static String getCurrentUsername() {
        return currentUsername;
    }

    /**
     * Registers a new user with the system.
     * Validates that the username doesn't already exist.
     * username - The desired username
     * password - The user's password
     * Returns: true if registration successful, false if username exists
     */
    public static boolean registerUser(String username, String password) {
        // Check if username already exists
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                return false; // Username exists
            }
        }

        users.add(new User(username, password));
        return true;
    }

    /**
     * Authenticates a user and establishes their session.
     * Sets the current user context for all subsequent operations.
     * username - The username to authenticate
     * password - The password to verify
     * Returns: true if login successful, false if credentials invalid
     */
    public static boolean loginUser(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                currentUser = user;
                currentUsername = username;
                return true;
            }
        }
        return false;
    }

    /**
     * Ends the current user session and returns to guest mode.
     * Resets all user-specific context and permissions.
     */
    public static void logoutUser() {
        currentUser = null;
        currentUsername = "Guest";
    }

    /**
    * Gets the currently logged-in user object.
    * Returns: Current User object, or null if not logged in
    */
    public static User getCurrentUser() {
        return currentUser;
    }

    /**
     * Checks if the currently logged-in user has administrator privileges.
     * Returns: true if user is admin, false otherwise
     */
    public static boolean isAdmin() {
        return currentUser != null && currentUser.getUsername().equals("admin");
    }

    /**
    * Gets all registered users (excluding admin for security).
    * Updates user statistics before returning.
    * Returns: List of all non-admin users
    */
    public static ArrayList<User> getAllUsers() {
        // Update user stats before returning
        updateAllUserStats();

        // Create a new list excluding the admin user
        ArrayList<User> filteredUsers = new ArrayList<>();
        for (User user : users) {
            if (!user.getUsername().equals("admin")) {
                filteredUsers.add(user);
            }
        }
        return filteredUsers;
    }

    /**
    * Updates statistics for all registered users.
    * Recalculates total orders, pending orders, and items sold for each user.
    */
    private static void updateAllUserStats() {
        for (User user : users) {
            int total = 0;
            int pending = 0;
            int sold = 0;

            for (Sale sale : salesList) {
                if (sale.getUsername() != null && sale.getUsername().equals(user.getUsername())) {
                    total++;
                    if (sale.getStatus().equalsIgnoreCase("Pending")) {
                        pending++;
                    }
                    sold += sale.getQuantity();
                }
            }

            user.updateStats(total, pending, sold);
        }
    }

    /**
     * Deletes a user and all their associated sales (admin only).
     * Cannot be used to delete the admin account itself.
     * username - The username to delete
     * Returns: true if deletion successful, false if user not found or is admin
     */
    public static boolean deleteUser(String username) {
        // Cannot delete admin
        if (username.equals("admin")) {
            return false;
        }

        // Also delete all sales by this user
        ArrayList<Sale> toRemove = new ArrayList<>();
        for (Sale sale : salesList) {
            if (sale.getUsername() != null && sale.getUsername().equals(username)) {
                toRemove.add(sale);
            }
        }
        salesList.removeAll(toRemove);

        // Delete the user
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getUsername().equals(username)) {
                users.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
    * Searches for a user by username using binary search.
    * username - The username to search for
    * Returns: User object if found, null if not found
    */
    public static User binarySearchUser(String username) {
        // Sort users by username for binary search
        ArrayList<User> sortedUsers = new ArrayList<>(users);

        // bubble sort by username
        int n = sortedUsers.size();
        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < n - i - 1; j++) {
                User current = sortedUsers.get(j);
                User next = sortedUsers.get(j + 1);

                if (current.getUsername().compareToIgnoreCase(next.getUsername()) > 0) {
                    sortedUsers.set(j, next);
                    sortedUsers.set(j + 1, current);
                    swapped = true;
                }
            }
            if (!swapped) {
                break;
            }
        }

        int left = 0;
        int right = sortedUsers.size() - 1;

        while (left <= right) {
            int mid = left + (right - left) / 2;
            User midUser = sortedUsers.get(mid);
            String midUsername = midUser.getUsername();

            int comparison = midUsername.compareToIgnoreCase(username);

            if (comparison == 0) {
                return midUser;
            } else if (comparison < 0) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        return null; // User not found
    }

    /**
    * Sorts all users by total orders in descending order.
    * Uses bubble sort algorithm.
    * Highest total orders appear first.
    */
    public static void bubbleSortByTotalOrders() {
        updateAllUserStats();
        int n = users.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                User currentUser = users.get(j);
                User nextUser = users.get(j + 1);

                if (currentUser.getTotalOrders() < nextUser.getTotalOrders()) {
                    // Swap for descending order (highest first)
                    users.set(j, nextUser);
                    users.set(j + 1, currentUser);
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
    }

    /**
    * Sorts all users by pending orders in descending order.
    * Uses bubble sort algorithm.
    * Highest pending orders appear first.
    */
    public static void bubbleSortByPendingOrders() {
        updateAllUserStats();
        int n = users.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                User currentUser = users.get(j);
                User nextUser = users.get(j + 1);

                if (currentUser.getPendingOrders() < nextUser.getPendingOrders()) {
                    // Swap for descending order (highest first)
                    users.set(j, nextUser);
                    users.set(j + 1, currentUser);
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
    }

    /**
    * Sorts all users by items sold in descending order.
    * Uses bubble sort algorithm.
    * Highest items sold appear first.
    */
    public static void bubbleSortByItemsSold() {
        updateAllUserStats();
        int n = users.size();

        for (int i = 0; i < n - 1; i++) {
            boolean swapped = false;

            for (int j = 0; j < n - i - 1; j++) {
                User currentUser = users.get(j);
                User nextUser = users.get(j + 1);

                if (currentUser.getItemsSold() < nextUser.getItemsSold()) {
                    // Swap for descending order (highest first)
                    users.set(j, nextUser);
                    users.set(j + 1, currentUser);
                    swapped = true;
                }
            }

            if (!swapped) {
                break;
            }
        }
    }

     /**
     * Initializes the system with sample data for demonstration and testing.
     * Creates sample users and sales with realistic data.
     * Should be called once when the application starts.
     */
    public static void initializeSampleData() {
        System.out.println("========================================");
        System.out.println("INITIALIZING SAMPLE DATA");
        System.out.println("========================================");

        // Add sample users (admin + regular users)
        addSampleUsers();

        // Add sample sales (only for regular users, NOT admin)
        addSampleSales();

        System.out.println("========================================");
        System.out.println("SAMPLE DATA SUMMARY:");
        System.out.println("- Total users: " + users.size() + " (1 admin + " + (users.size() - 1) + " sales users)");
        System.out.println("- Total sales: " + salesList.size());
        System.out.println("========================================");
    }

    /**
     * Internal method to add sample users for testing.
     * Adds admin and several regular sales users with sample credentials.
     */
    private static void addSampleUsers() {
        boolean adminExists = false;
        for (User user : users) {
            if (user.getUsername().equals("admin")) {
                adminExists = true;
                break;
            }
        }

        if (!adminExists) {
            users.add(new User("admin", "admin"));
            System.out.println("Added admin user");
        }

        // Add regular sales users
        String[] sampleUsers = {"alice", "bob", "charlie", "diana", "edward"};
        String[] samplePasswords = {"alice123", "bob456", "charlie789", "diana012", "edward345"};

        for (int i = 0; i < sampleUsers.length; i++) {
            boolean userExists = false;
            for (User user : users) {
                if (user.getUsername().equals(sampleUsers[i])) {
                    userExists = true;
                    break;
                }
            }

            if (!userExists) {
                users.add(new User(sampleUsers[i], samplePasswords[i]));
                System.out.println("Added sales user: " + sampleUsers[i]);
            }
        }
    }

    /**
    * Gets total sales count for current user.
    * Returns: Number of sales belonging to the current user
    */
    public static int getTotalSalesCountForCurrentUser() {
        return getSalesForCurrentUser().size();
    }

    /**
    * Gets pending orders count for current user.
    * Returns: Number of pending sales belonging to the current user
    */
    public static int getPendingOrdersCountForCurrentUser() {
        int count = 0;
        for (Sale sale : getSalesForCurrentUser()) {
            if (sale.getStatus().equalsIgnoreCase("Pending")) {
                count++;
            }
        }
        return count;
    }

    /**
    * Gets total items sold for current user.
    * Returns: Total quantity of items sold by the current user
    */
    public static int getItemsSoldForCurrentUser() {
        int total = 0;
        for (Sale sale : getSalesForCurrentUser()) {
            total += sale.getQuantity();
        }
        return total;
    }

    /**
    * Generates dashboard statistics for current user.
    * Returns: Array of user-specific statistics [totalOrders, pendingOrders, itemsSold]
    */
    public static String[] getDashboardStatsForCurrentUser() {
        if (currentUser == null || currentUsername.equals("Guest")) {
            String[] stats = new String[3];
            stats[0] = "0";  // Total Orders
            stats[1] = "0";  // Pending Orders
            stats[2] = "0";  // Items Sold
            return stats;
        }

        int totalOrders = User.getTotalSalesCountForCurrentUser();
        int pendingOrders = User.getPendingOrdersCountForCurrentUser();
        int itemsSold = User.getItemsSoldForCurrentUser();

        String[] stats = new String[3];
        stats[0] = String.valueOf(totalOrders);
        stats[1] = String.valueOf(pendingOrders);
        stats[2] = String.valueOf(itemsSold);

        return stats;
    }

    /**
     * Internal method to add sample sales data for testing.
     * Creates realistic sales data for demonstration purposes.
     */
    private static void addSampleSales() {
        Calendar cal = Calendar.getInstance();

        // Helper method to add random time
        java.util.Random random = new java.util.Random();

        // Store the original current user (save it)
        String originalUser = currentUsername;
        User originalCurrentUser = currentUser;

        // ====== ALICE'S SALES ======
        currentUsername = "alice";

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -5);
        // Add random time between 9 AM and 5 PM
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8)); // 9-16
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Michael Johnson", "Monitor", 299.99, 1,
                "Completed", "Paid", cal.getTime(), "9112233445"));

        cal.add(Calendar.DATE, -2);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Sarah Williams", "USB Cable", 12.99, 5,
                "Completed", "Paid", cal.getTime(), "9334455667"));

        // ====== BOB'S SALES ======
        currentUsername = "bob";

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "David Miller", "Tablet", 349.99, 2,
                "Pending", "Unpaid", cal.getTime(), "9777777777"));

        // ====== CHARLIE'S SALES ======
        currentUsername = "charlie";

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -4);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Jennifer Brown", "Printer", 189.99, 1,
                "Completed", "Paid", cal.getTime(), "9000000001"));

        // ====== DIANA'S SALES ======
        currentUsername = "diana";

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -6);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Robert Garcia", "Gaming Mouse", 59.99, 1,
                "Completed", "Paid", cal.getTime(), "9000000005"));

        // ====== EDWARD'S SALES ======
        currentUsername = "edward";

        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -7);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Lisa Martinez", "Monitor Arm", 89.99, 1,
                "Completed", "Paid", cal.getTime(), "9000000010"));

        // Add a few more sales for variety
        currentUsername = "alice";
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Thomas Anderson", "Keyboard", 45.99, 1,
                "Completed", "Paid", cal.getTime(), "9988776655"));

        currentUsername = "bob";
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -3);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Emily Davis", "Mouse", 25.50, 3,
                "Pending", "Unpaid", cal.getTime(), "9123456780"));

        currentUsername = "charlie";
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -2);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Daniel Wilson", "Laptop", 850.00, 2,
                "Completed", "Paid", cal.getTime(), "9876543210"));

        // Add even more sample customers
        currentUsername = "diana";
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Patricia Taylor", "Webcam", 49.99, 1,
                "Completed", "Paid", cal.getTime(), "9223344556"));

        currentUsername = "edward";
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -4);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Christopher Lee", "Headphones", 79.99, 2,
                "Completed", "Paid", cal.getTime(), "9334455667"));

        currentUsername = "alice";
        cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Amanda Scott", "External SSD", 129.99, 1,
                "Pending", "Unpaid", cal.getTime(), "9445566778"));

        currentUsername = "bob";
        cal = Calendar.getInstance();
        cal.add(Calendar.DATE, -1);
        cal.set(Calendar.HOUR_OF_DAY, 9 + random.nextInt(8));
        cal.set(Calendar.MINUTE, random.nextInt(60));
        cal.set(Calendar.SECOND, random.nextInt(60));
        addSale(new Sale(generateSaleId(), "Kevin White", "Power Bank", 39.99, 3,
                "Completed", "Paid", cal.getTime(), "9556677889"));

        // RESTORE ORIGINAL USER CONTEXT 
        currentUsername = originalUser;
        currentUser = originalCurrentUser;

        System.out.println("Added " + salesList.size() + " sample sales");
    }
}

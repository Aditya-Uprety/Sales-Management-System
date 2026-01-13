/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package view;

import java.awt.Component;  
import java.util.ArrayList; 
import javax.swing.JOptionPane; 
import model.Sale;
import java.util.LinkedList;

/**
 *
 * @author adityauprety
 */
public class MainFrame extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    private LinkedList<String> navigationHistory = new LinkedList<>();

    /**
    * Creates new form MainFrame
    * Initializes sample data and displays home panel.
    */
    public MainFrame() {
        initComponents();

        // Initialize sample data
        controller.SalesController.initializeSampleData();

        // Create home panel
        HomePanel homePanel = new HomePanel(this);

        // Add home panel to mainPanel with absolute positioning
        homePanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(homePanel);

        setLocationRelativeTo(null);
        setVisible(true);
    }

    /**
    * Navigates to the Dashboard panel.
    * Updates navigation history and refreshes display.
    */
    public void showDashboard() {

        // Track navigation
        navigationHistory.add("Dashboard");

        // Remove all components
        mainPanel.removeAll();

        // Create and add dashboard panel
        DashboardPanel dashboardPanel = new DashboardPanel(this);
        dashboardPanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(dashboardPanel);

        // Refresh display
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
    * Navigates to the Home (login) panel.
    * Updates navigation history and refreshes display.
    */
    public void showHome() {

        // Track navigation
        navigationHistory.add("Home");

        // Remove all components
        mainPanel.removeAll();

        // Recreate and add home panel
        HomePanel homePanel = new HomePanel(this);
        homePanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(homePanel);

        // Refresh display
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
    * Navigates to the Sales List panel.
    * Updates navigation history and refreshes display.
    */
    public void showSalesList() {

        // Track navigation
        navigationHistory.add("SalesList");

        // Remove all components
        mainPanel.removeAll();

        // Create and add sales list panel
        SalesListPanel salesListPanel = new SalesListPanel(this);
        salesListPanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(salesListPanel);

        // Refresh display
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
    * Navigates to the Add Sales panel.
    * Updates navigation history and refreshes display.
    */
    public void showAddSales() {

        // Track navigation
        navigationHistory.add("AddSales");

        // Remove all components
        mainPanel.removeAll();

        // Create and add add sales panel
        AddSalesPanel addSalesPanel = new AddSalesPanel(this);
        addSalesPanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(addSalesPanel);

        // Refresh display
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
    * Navigates to the Admin panel.
    * Updates navigation history and refreshes display.
    */
    public void showAdminPanel() {
        navigationHistory.add("Admin");

        mainPanel.removeAll();
        AdminPanel adminPanel = new AdminPanel(this);
        adminPanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(adminPanel);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
    * Navigates to the Update Sales panel with pre-filled data.
    * id - Sale ID to update
    * name - Customer name
    * item - Item name
    * price - Item price
    * quantity - Quantity
    * status - Order status
    * payment - Payment status
    * number - Contact number
    */
    public void showUpdateSales(String id, String name, String item, String price,
            String quantity, String status, String payment, String number) {

        // Track navigation
        navigationHistory.add("UpdateSales");

        mainPanel.removeAll();

        UpdateSalesPanel updateSalesPanel = new UpdateSalesPanel(this);
        updateSalesPanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(updateSalesPanel);

        // Pass the data to the UpdateSalesPanel
        updateSalesPanel.setSaleData(id, name, item, price, quantity, status, payment, number);

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
    * Navigates to the Search/Sort panel.
    * Updates navigation history and refreshes display.
    */
    public void showSearchSort() {

        // Track navigation
        navigationHistory.add("SearchSort");

        // Remove all components
        mainPanel.removeAll();

        // Create and add search/sort panel
        SearchSortPanel searchSortPanel = new SearchSortPanel(this);
        searchSortPanel.setBounds(0, 0, 1192, 680);
        mainPanel.add(searchSortPanel);

        // Refresh display
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    /**
    * Passes search results to the Sales List panel.
    * results - List of sales matching search criteria
    * message - Description of search results for user display
    */
    public void passSearchResultsToSalesList(ArrayList<Sale> results, String message) {
        // Get the current SalesListPanel
        if (mainPanel.getComponentCount() > 0) {
            Component current = mainPanel.getComponent(0);
            if (current instanceof SalesListPanel) {
                SalesListPanel salesListPanel = (SalesListPanel) current;
                salesListPanel.displayFilteredSales("searchResults", results);

                if (message != null && !message.isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            message + "\nFound " + results.size() + " sales.",
                            "Search Results",
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        }
    }

    /**
    * Navigates back to the previous panel.
    * Uses navigation history to return to last visited panel.
    */
    public void goBack() {
        if (navigationHistory.size() > 1) {
            // Remove current panel
            navigationHistory.removeLast();

            // Get previous panel
            String previousPanel = navigationHistory.getLast();

            navigationHistory.removeLast();

            // Navigate to previous panel
            switch (previousPanel) {
                case "Home":
                    showHome();
                    break;
                case "Dashboard":
                    showDashboard();
                    break;
                case "SalesList":
                    showSalesList();
                    break;
                case "AddSales":
                    showAddSales();
                    break;
                case "UpdateSales":
                    showSalesList();
                    break;
                case "SearchSort":
                    showSearchSort();
                    break;
                default:
                    showHome();
            }
        } else {
            showHome(); // If no history, go home
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        mainPanel.setMaximumSize(new java.awt.Dimension(1192, 680));
        mainPanel.setMinimumSize(new java.awt.Dimension(1192, 680));
        mainPanel.setPreferredSize(new java.awt.Dimension(1192, 680));
        mainPanel.setRequestFocusEnabled(false);
        mainPanel.setSize(new java.awt.Dimension(1192, 680));

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 1192, Short.MAX_VALUE)
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 680, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new MainFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel mainPanel;
    // End of variables declaration//GEN-END:variables
}

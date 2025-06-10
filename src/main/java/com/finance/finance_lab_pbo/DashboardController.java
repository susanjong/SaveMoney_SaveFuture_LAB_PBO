package com.finance.finance_lab_pbo;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;

import javafx.event.ActionEvent;
import java.net.URL;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML private Button profileBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button incomeBtn;
    @FXML private Button spendingBtn;
    @FXML private Label balanceLabel;
    @FXML private Label incomeLabel;
    @FXML private Label spendLabel;
    @FXML private Button incomeDetailsBtn;
    @FXML private Button spendDetailsBtn;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private ComboBox<String> periodComboBox;
    @FXML private ComboBox<String> filterComboBox;
    @FXML private TableView<Transaction> transactionTable;
    @FXML private TableColumn<Transaction, Integer> noColumn;
    @FXML private TableColumn<Transaction, String> activityColumn;
    @FXML private TableColumn<Transaction, String> amountColumn;
    @FXML private TableColumn<Transaction, String> dateColumn;
    @FXML private TableColumn<Transaction, String> balanceColumn;

    private ObservableList<Transaction> transactionData;
    private DecimalFormat currencyFormat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currencyFormat = new DecimalFormat("#,###");
        
        // Initialize ComboBoxes
        initializeComboBoxes();
        
        // Initialize table
        initializeTable();
        
        // Load sample data
        loadSampleData();
        
        // Update summary cards
        updateSummaryCards();
    }

    private void initializeComboBoxes() {
        sortComboBox.setItems(FXCollections.observableArrayList("Ascending", "Descending"));
        sortComboBox.setValue("Ascending");
        
        periodComboBox.setItems(FXCollections.observableArrayList("1 week", "2 weeks", "3 weeks", "1 month"));
        periodComboBox.setValue("1 week");
        
        filterComboBox.setItems(FXCollections.observableArrayList("Show All", "Income Only", "Spending Only"));
        filterComboBox.setValue("Show All");
    }

    private void initializeTable() {
        noColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activity"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("formattedBalance"));
        
        transactionData = FXCollections.observableArrayList();
        transactionTable.setItems(transactionData);
    }

    private void loadSampleData() {
        transactionData.clear();
        
        // Sample transactions from the image
        transactionData.add(new Transaction(1, "Electrical Bills", -1000000, "30-05-2025", 5000000));
        transactionData.add(new Transaction(2, "Tuition fees", -5000000, "30-05-2025", 6000000));
        transactionData.add(new Transaction(3, "Salary earnings", 10000000, "30-05-2025", 15000000));
        transactionData.add(new Transaction(4, "Bonus earnings", 5000000, "30-05-2025", 5000000));
    }

    private void updateSummaryCards() {
        double totalIncome = 0;
        double totalSpending = 0;
        double currentBalance = 0;
        
        for (Transaction t : transactionData) {
            if (t.getAmount() > 0) {
                totalIncome += t.getAmount();
            } else {
                totalSpending += Math.abs(t.getAmount());
            }
            currentBalance = t.getBalance(); // Assuming last balance is current
        }
        
        balanceLabel.setText("Rp " + currencyFormat.format(currentBalance));
        incomeLabel.setText("Rp " + currencyFormat.format(totalIncome));
        spendLabel.setText("Rp " + currencyFormat.format(totalSpending));
    }

    @FXML
    void handleNavigation(ActionEvent event) {
        Object source = event.getSource();
        
        try {
            String fxmlFile = "";
            
            if (source == profileBtn) {
                fxmlFile = "/com/finance/finance_lab_pbo/Profile.fxml";
            } else if (source == dashboardBtn) {
                fxmlFile = "/com/finance/finance_lab_pbo/Dashboard.fxml";
            } else if (source == incomeBtn) {
                fxmlFile = "/com/finance/finance_lab_pbo/Income.fxml";
            } else if (source == spendingBtn) {
                fxmlFile = "/com/finance/finance_lab_pbo/Spending.fxml";
            } else {
                return; // Unknown button
            }
            
            if (!fxmlFile.isEmpty()) {
                URL url = getClass().getResource(fxmlFile);
                
                if (url == null) {
                    // Try alternative path format if the first attempt fails
                    String altPath = fxmlFile.replace("/com/finance/finance_lab_pbo/", "/");
                    url = getClass().getResource(altPath);
                    
                    if (url == null) {
                        // Try one more alternative - without leading slash
                        String noSlashPath = fxmlFile.substring(1);
                        url = getClass().getClassLoader().getResource(noSlashPath);
                        
                        if (url == null) {
                            showAlert("Navigation Error", 
                                "Could not find FXML file: " + fxmlFile + 
                                "\nPlease check if the file exists in the resources folder.");
                            return;
                        }
                    }
                }
                
                Parent root = FXMLLoader.load(url);
                Stage stage = (Stage) ((Button) source).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();
            }
            
        } catch (IOException e) {
            showAlert("Navigation Error", 
                    "Could not navigate to the requested page: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void showIncomeDetails() {
        // Show income details dialog or navigate to income details page
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Income Details");
        alert.setHeaderText("Total Income: Rp 15.000.000");
        alert.setContentText("Salary earnings: Rp 10.000.000\nBonus earnings: Rp 5.000.000");
        alert.showAndWait();
    }

    @FXML
    private void showSpendDetails() {
        // Show spending details dialog or navigate to spending details page
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spending Details");
        alert.setHeaderText("Total Spending: Rp 10.000.000");
        alert.setContentText("Electrical Bills: Rp 1.000.000\nTuition fees: Rp 5.000.000\nOther expenses: Rp 4.000.000");
        alert.showAndWait();
    }

    @FXML
    private void onSortChange() {
        String selectedSort = sortComboBox.getValue();
        if ("Ascending".equals(selectedSort)) {
            transactionData.sort((t1, t2) -> Integer.compare(t1.getId(), t2.getId()));
        } else {
            transactionData.sort((t1, t2) -> Integer.compare(t2.getId(), t1.getId()));
        }
    }

    @FXML
    private void onPeriodChange() {
        String selectedPeriod = periodComboBox.getValue();
        System.out.println("Period changed to: " + selectedPeriod);
        // Filter transactions based on period
        // Implementation depends on your date filtering logic
    }

    @FXML
    private void onFilterChange() {
        String selectedFilter = filterComboBox.getValue();
        ObservableList<Transaction> filteredData = FXCollections.observableArrayList();
        
        for (Transaction t : transactionData) {
            switch (selectedFilter) {
                case "Income Only":
                    if (t.getAmount() > 0) filteredData.add(t);
                    break;
                case "Spending Only":
                    if (t.getAmount() < 0) filteredData.add(t);
                    break;
                default:
                    filteredData.add(t);
                    break;
            }
        }
        
        transactionTable.setItems(filteredData);
    }

    // Updated showAlert method to match the usage pattern
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for Transaction data model
    public static class Transaction {
        private int id;
        private String activity;
        private double amount;
        private String date;
        private double balance;

        public Transaction(int id, String activity, double amount, String date, double balance) {
            this.id = id;
            this.activity = activity;
            this.amount = amount;
            this.date = date;
            this.balance = balance;
        }

        // Getters
        public int getId() { return id; }
        public String getActivity() { return activity; }
        public double getAmount() { return amount; }
        public String getDate() { return date; }
        public double getBalance() { return balance; }

        // Formatted getters for display
        public String getFormattedAmount() {
            DecimalFormat df = new DecimalFormat("#,###");
            if (amount > 0) {
                return "Rp " + df.format(amount);
            } else {
                return "Rp " + df.format(Math.abs(amount));
            }
        }

        public String getFormattedBalance() {
            DecimalFormat df = new DecimalFormat("#,###");
            return "Rp " + df.format(balance);
        }

        // Setters
        public void setId(int id) { this.id = id; }
        public void setActivity(String activity) { this.activity = activity; }
        public void setAmount(double amount) { this.amount = amount; }
        public void setDate(String date) { this.date = date; }
        public void setBalance(double balance) { this.balance = balance; }
    }
}
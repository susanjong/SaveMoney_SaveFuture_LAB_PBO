package com.finance.finance_lab_pbo;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import com.finance.finance_lab_pbo.model.Income;
import com.finance.finance_lab_pbo.model.Spending;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

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
    @FXML private TextField yearTextField;
    @FXML private TableView<TransactionDisplay> transactionTable;
    @FXML private TableColumn<TransactionDisplay, Integer> noColumn;
    @FXML private TableColumn<TransactionDisplay, String> activityColumn;
    @FXML private TableColumn<TransactionDisplay, String> amountColumn;
    @FXML private TableColumn<TransactionDisplay, String> dateColumn;
    @FXML private TableColumn<TransactionDisplay, String> balanceColumn;

    private ObservableList<TransactionDisplay> transactionData;
    private DecimalFormat currencyFormat;
    
    // Data source - from database
    private List<Income> incomeList;
    private List<Spending> spendingList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currencyFormat = new DecimalFormat("#,###");
        
        // Initialize ComboBoxes
        initializeComboBoxes();
        
        // Initialize table
        initializeTable();
        
        // Set default year
        yearTextField.setText(String.valueOf(LocalDate.now().getYear()));
        
        // Load data from database and apply filters
        loadDataFromDatabase();
        applyFilters();
    }

    private void loadDataFromDatabase() {
        incomeList = new ArrayList<>();
        spendingList = new ArrayList<>();
        
        // Load Income data
        String incomeQuery = "SELECT * FROM income ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(incomeQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Income income = new Income(
                    rs.getString("source"),
                    rs.getBigDecimal("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("description")
                );
                income.setId(rs.getInt("id"));
                incomeList.add(income);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading income data: " + e.getMessage());
            showAlert("Database Error", "Failed to load income data: " + e.getMessage());
        }
        
        // Load Spending data
        String spendingQuery = "SELECT * FROM spending ORDER BY date DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(spendingQuery);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Spending spending = new Spending(
                    rs.getString("category"),
                    rs.getBigDecimal("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("description")
                );
                spending.setId(rs.getInt("id"));
                spendingList.add(spending);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading spending data: " + e.getMessage());
            showAlert("Database Error", "Failed to load spending data: " + e.getMessage());
        }
    }

    private void initializeComboBoxes() {
        sortComboBox.setItems(FXCollections.observableArrayList("Ascending", "Descending"));
        sortComboBox.setValue("Ascending");
        
        // Month combo box
        periodComboBox.setItems(FXCollections.observableArrayList(
            "All Months", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ));
        periodComboBox.setValue("All");
        
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

    @FXML
    private void onFilterChange() {
        applyFilters();
    }

    @FXML
    private void onPeriodChange() {
        applyFilters();
    }

    @FXML
    private void onYearChange() {
        applyFilters();
    }

    private void applyFilters() {
        String selectedMonth = periodComboBox.getValue();
        String yearText = yearTextField.getText();
        String selectedFilter = filterComboBox.getValue();
        
        int filterYear;
        try {
            filterYear = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            filterYear = LocalDate.now().getYear(); // Default to current year
        }

        // Filter and combine data
        List<TransactionDisplay> filteredTransactions = new ArrayList<>();
        
        // Filter income
        if (!"Spending Only".equals(selectedFilter)) {
            for (Income income : incomeList) {
                if (matchesFilter(income.getDate(), selectedMonth, filterYear)) {
                    filteredTransactions.add(new TransactionDisplay(
                        filteredTransactions.size() + 1,
                        income.getSource() + " - " + income.getDescription(),
                        income.getAmount().doubleValue(),
                        income.getDate().toString(),
                        0 // Balance will be calculated
                    ));
                }
            }
        }
        
        // Filter spending
        if (!"Income Only".equals(selectedFilter)) {
            for (Spending spending : spendingList) {
                if (matchesFilter(spending.getDate(), selectedMonth, filterYear)) {
                    filteredTransactions.add(new TransactionDisplay(
                        filteredTransactions.size() + 1,
                        spending.getCategory() + " - " + spending.getDescription(),
                        -spending.getAmount().doubleValue(), // Negative for spending
                        spending.getDate().toString(),
                        0 // Balance will be calculated
                    ));
                }
            }
        }
        
        // Sort by date and calculate running balance
        filteredTransactions.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        
        double runningBalance = 0;
        for (int i = 0; i < filteredTransactions.size(); i++) {
            TransactionDisplay t = filteredTransactions.get(i);
            runningBalance += t.getAmount();
            t.setBalance(runningBalance);
            t.setId(i + 1); // Renumber
        }
        
        // Apply sort order
        if ("Descending".equals(sortComboBox.getValue())) {
            filteredTransactions.sort((t1, t2) -> Integer.compare(t2.getId(), t1.getId()));
        }
        
        // Update table
        transactionData.clear();
        transactionData.addAll(filteredTransactions);
        
        // Update summary cards
        updateSummaryCards(selectedMonth, filterYear);
    }

    private boolean matchesFilter(LocalDate date, String selectedMonth, int filterYear) {
        if (date.getYear() != filterYear) {
            return false;
        }
        
        if ("All".equals(selectedMonth)) {
            return true;
        }
        
        Month month = Month.valueOf(selectedMonth.toUpperCase());
        return date.getMonth() == month;
    }

    private void updateSummaryCards(String selectedMonth, int filterYear) {
        double totalIncome = 0;
        double totalSpending = 0;
        
        // Calculate income
        for (Income income : incomeList) {
            if (matchesFilter(income.getDate(), selectedMonth, filterYear)) {
                totalIncome += income.getAmount().doubleValue();
            }
        }
        
        // Calculate spending
        for (Spending spending : spendingList) {
            if (matchesFilter(spending.getDate(), selectedMonth, filterYear)) {
                totalSpending += spending.getAmount().doubleValue();
            }
        }
        
        double balance = totalIncome - totalSpending;
        
        balanceLabel.setText("Rp " + currencyFormat.format(balance));
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
                return;
            }
            
            if (!fxmlFile.isEmpty()) {
                URL url = getClass().getResource(fxmlFile);
                
                if (url == null) {
                    String altPath = fxmlFile.replace("/com/finance/finance_lab_pbo/", "/");
                    url = getClass().getResource(altPath);
                    
                    if (url == null) {
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
        String selectedMonth = periodComboBox.getValue();
        String yearText = yearTextField.getText();
        
        int filterYear;
        try {
            filterYear = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            filterYear = LocalDate.now().getYear();
        }

        StringBuilder details = new StringBuilder();
        double total = 0;
        
        for (Income income : incomeList) {
            if (matchesFilter(income.getDate(), selectedMonth, filterYear)) {
                details.append(income.getSource()).append(": Rp ")
                       .append(currencyFormat.format(income.getAmount())).append("\n");
                total += income.getAmount().doubleValue();
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Income Details");
        alert.setHeaderText("Total Income: Rp " + currencyFormat.format(total));
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    @FXML
    private void showSpendDetails() {
        String selectedMonth = periodComboBox.getValue();
        String yearText = yearTextField.getText();
        
        int filterYear;
        try {
            filterYear = Integer.parseInt(yearText);
        } catch (NumberFormatException e) {
            filterYear = LocalDate.now().getYear();
        }

        StringBuilder details = new StringBuilder();
        double total = 0;
        
        for (Spending spending : spendingList) {
            if (matchesFilter(spending.getDate(), selectedMonth, filterYear)) {
                details.append(spending.getCategory()).append(": Rp ")
                       .append(currencyFormat.format(spending.getAmount())).append("\n");
                total += spending.getAmount().doubleValue();
            }
        }
        
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Spending Details");
        alert.setHeaderText("Total Spending: Rp " + currencyFormat.format(total));
        alert.setContentText(details.toString());
        alert.showAndWait();
    }

    @FXML
    private void onSortChange() {
        applyFilters(); // Reapply filters with new sort order
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // TransactionDisplay class for table
    public static class TransactionDisplay {
        private int id;
        private String activity;
        private double amount;
        private String date;
        private double balance;

        public TransactionDisplay(int id, String activity, double amount, String date, double balance) {
            this.id = id;
            this.activity = activity;
            this.amount = amount;
            this.date = date;
            this.balance = balance;
        }

        // Getters and setters
        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getActivity() { return activity; }
        public double getAmount() { return amount; }
        public String getDate() { return date; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }

        public String getFormattedAmount() {
            DecimalFormat df = new DecimalFormat("#,###");
            return "Rp " + df.format(Math.abs(amount));
        }

        public String getFormattedBalance() {
            DecimalFormat df = new DecimalFormat("#,###");
            return "Rp " + df.format(balance);
        }
    }
}
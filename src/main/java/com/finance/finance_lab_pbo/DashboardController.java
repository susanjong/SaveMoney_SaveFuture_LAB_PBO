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
import com.finance.finance_lab_pbo.model.Transaction;

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
    @FXML private TableColumn<TransactionDisplay, String> typeColumn; 

    private ObservableList<TransactionDisplay> transactionData;
    private DecimalFormat currencyFormat;
    
    private List<Transaction> allTransactions;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currencyFormat = new DecimalFormat("#,###");

        initializeComboBoxes();
        
        initializeTable();
        
        yearTextField.setText(String.valueOf(LocalDate.now().getYear()));
        
        loadDataFromDatabase();
        applyFilters();
    }

    private void loadDataFromDatabase() {
        allTransactions = new ArrayList<>();
        
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
                allTransactions.add(income);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading income data: " + e.getMessage());
            showAlert("Database Error", "Failed to load income data: " + e.getMessage());
        }
        
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
                allTransactions.add(spending);
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading spending data: " + e.getMessage());
            showAlert("Database Error", "Failed to load spending data: " + e.getMessage());
        }
    }

    private void initializeComboBoxes() {
        sortComboBox.setItems(FXCollections.observableArrayList("Ascending", "Descending"));
        sortComboBox.setValue("Ascending");
        
        periodComboBox.setItems(FXCollections.observableArrayList(
            "All Months", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        ));
        periodComboBox.setValue("All Months");

        filterComboBox.setItems(FXCollections.observableArrayList("Show All", "Income Only", "Spending Only"));
        filterComboBox.setValue("Show All");
    }

    private void initializeTable() {
        noColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activity"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("formattedAmount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        balanceColumn.setCellValueFactory(new PropertyValueFactory<>("formattedBalance"));
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("type")); // New type column
        
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
            filterYear = LocalDate.now().getYear(); 
        }

        List<TransactionDisplay> filteredTransactions = new ArrayList<>();
        
        for (Transaction transaction : allTransactions) {
            boolean includeTransaction = false;
            
            switch (selectedFilter) {
                case "Show All":
                    includeTransaction = true;
                    break;
                case "Income Only":
                    includeTransaction = "INCOME".equals(transaction.getType());
                    break;
                case "Spending Only":
                    includeTransaction = "SPENDING".equals(transaction.getType());
                    break;
            }
            
            if (includeTransaction && matchesFilter(transaction.getDate(), selectedMonth, filterYear)) {
                String activityDescription = getActivityDescription(transaction);
                double amount = getTransactionAmount(transaction);
                
                filteredTransactions.add(new TransactionDisplay(
                    filteredTransactions.size() + 1,
                    activityDescription,
                    amount,
                    transaction.getDate().toString(),
                    0, 
                    transaction.getType() 
                ));
            }
        }
        filteredTransactions.sort((t1, t2) -> t1.getDate().compareTo(t2.getDate()));
        
        double runningBalance = 0;
        for (int i = 0; i < filteredTransactions.size(); i++) {
            TransactionDisplay t = filteredTransactions.get(i);
            runningBalance += t.getAmount();
            t.setBalance(runningBalance);
            t.setId(i + 1);
        }

        if ("Descending".equals(sortComboBox.getValue())) {
            filteredTransactions.sort((t1, t2) -> Integer.compare(t2.getId(), t1.getId()));
        }
        
        transactionData.clear();
        transactionData.addAll(filteredTransactions);
        
        updateSummaryCards(selectedMonth, filterYear);
    }

    private String getActivityDescription(Transaction transaction) {
        if ("INCOME".equals(transaction.getType())) {
            Income income = (Income) transaction;
            return income.getSource() + " - " + income.getDescription();
        } else {
            Spending spending = (Spending) transaction;
            return spending.getCategory() + " - " + spending.getDescription();
        }
    }

    private double getTransactionAmount(Transaction transaction) {
        double amount = transaction.getAmount().doubleValue();
        return "SPENDING".equals(transaction.getType()) ? -amount : amount;
    }

    private boolean matchesFilter(LocalDate date, String selectedMonth, int filterYear) {
        if (date.getYear() != filterYear) {
            return false;
        }
        
        if ("All Months".equals(selectedMonth)) {
            return true;
        }
        
        Month month = Month.valueOf(selectedMonth.toUpperCase());
        return date.getMonth() == month;
    }

    private void updateSummaryCards(String selectedMonth, int filterYear) {
        double totalIncome = 0;
        double totalSpending = 0;
        
        for (Transaction transaction : allTransactions) {
            if (matchesFilter(transaction.getDate(), selectedMonth, filterYear)) {
                double amount = transaction.getAmount().doubleValue();
                
                if ("INCOME".equals(transaction.getType())) {
                    totalIncome += amount;
                } else if ("SPENDING".equals(transaction.getType())) {
                    totalSpending += amount;
                }
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
                fxmlFile = "/com/finance/finance_lab_pbo/profile.fxml";
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
        
        for (Transaction transaction : allTransactions) {
            if ("INCOME".equals(transaction.getType()) && 
                matchesFilter(transaction.getDate(), selectedMonth, filterYear)) {
                
                Income income = (Income) transaction;
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

        for (Transaction transaction : allTransactions) {
            if ("SPENDING".equals(transaction.getType()) && 
                matchesFilter(transaction.getDate(), selectedMonth, filterYear)) {
                
                Spending spending = (Spending) transaction;
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
        applyFilters(); 
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class TransactionDisplay {
        private int id;
        private String activity;
        private double amount;
        private String date;
        private double balance;
        private String type; 

        public TransactionDisplay(int id, String activity, double amount, String date, double balance, String type) {
            this.id = id;
            this.activity = activity;
            this.amount = amount;
            this.date = date;
            this.balance = balance;
            this.type = type;
        }

        public int getId() { return id; }
        public void setId(int id) { this.id = id; }
        public String getActivity() { return activity; }
        public double getAmount() { return amount; }
        public String getDate() { return date; }
        public double getBalance() { return balance; }
        public void setBalance(double balance) { this.balance = balance; }
        public String getType() { return type; } 
        public void setType(String type) { this.type = type; } 

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
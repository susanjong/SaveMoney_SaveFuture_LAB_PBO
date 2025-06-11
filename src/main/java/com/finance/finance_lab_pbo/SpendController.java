package com.finance.finance_lab_pbo;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ResourceBundle;

import com.finance.finance_lab_pbo.model.SpendingCategory;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class SpendController implements Initializable {
    
    @FXML
    private Button profileBtn;
    
    @FXML
    private Button dashboardBtn;
    
    @FXML
    private Button incomeBtn;
    
    @FXML
    private Button spendingBtn;
    
    @FXML
    private TableView<SpendingDisplay> spendingTable;
    
    @FXML
    private TableColumn<SpendingDisplay, String> noColumn;
    
    @FXML
    private TableColumn<SpendingDisplay, String> categoryColumn;
    
    @FXML
    private TableColumn<SpendingDisplay, String> descriptionColumn;
    
    @FXML
    private TableColumn<SpendingDisplay, String> amountColumn;
    
    @FXML
    private TableColumn<SpendingDisplay, String> dateColumn;
    
    @FXML
    private ComboBox<SpendingCategory> categoryComboBox;
    
    @FXML
    private TextField descriptionField;
    
    @FXML
    private TextField amountField;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private Label totalSpendingLabel;
    
    @FXML
    private Label mostSpendingLabel;
    
    @FXML
    private Button filterAllBtn;
    
    @FXML
    private Button filter1WeekBtn;
    
    @FXML
    private Button filter1MonthBtn;
    
    @FXML
    private Button filter1YearBtn;

    @FXML
    private TextField yearFilterField;
    private String selectedYear = null;

    @FXML
    private ComboBox<String> monthFilterComboBox;
    private String selectedMonth = null;
    
    private ObservableList<SpendingDisplay> spendingData = FXCollections.observableArrayList();
    private int selectedId = -1;
    private String currentFilter = "ALL";
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Setup ComboBox with categories
        categoryComboBox.setItems(FXCollections.observableArrayList(SpendingCategory.values()));
        
        // Set up table columns
        noColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(spendingData.indexOf(cellData.getValue()) + 1)));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Set table data
        spendingTable.setItems(spendingData);
        
        // Set up table selection listener
        spendingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedId = newSelection.getId();
                populateFields(newSelection);
            }
        });

        ObservableList<String> months = FXCollections.observableArrayList(
            "All Months", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        monthFilterComboBox.setItems(months);

        monthFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedMonth = newVal;
            loadSpendingData();
            updateTotals();
        });

        yearFilterField.textProperty().addListener((obs, oldVal, newVal) -> {
            selectedYear = newVal.trim().isEmpty() ? null : newVal.trim();
            loadSpendingData();
            updateTotals();
        });
        
        // Load data from database
        loadSpendingData();
        updateTotals();
    }
    
    private void loadSpendingData() {
        spendingData.clear();
        String sql = buildFilterQuery();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setFilterParameters(stmt);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    SpendingDisplay display = new SpendingDisplay(
                        rs.getInt("id"),
                        rs.getString("category"),
                        rs.getString("description"),
                        formatCurrency(rs.getBigDecimal("amount")),
                        rs.getDate("date").toLocalDate().toString()
                    );
                    spendingData.add(display);
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to load spending data: " + e.getMessage());
        }
    }
    
    private String buildFilterQuery() {
        String baseQuery = "SELECT id, category, description, amount, date FROM spending";
        String whereClause = "";

        if (selectedMonth != null && !selectedMonth.equals("All Months")) {
            int monthNum = monthFilterComboBox.getSelectionModel().getSelectedIndex(); 
            whereClause = " WHERE EXTRACT(MONTH FROM date) = " + monthNum;
        }

        if (selectedYear != null && !selectedYear.isEmpty()) {
            if (whereClause.isEmpty()) {
                whereClause = " WHERE EXTRACT(YEAR FROM date) = " + selectedYear;
            } else {
                whereClause += " AND EXTRACT(YEAR FROM date) = " + selectedYear;
            }
        }

        switch (currentFilter) {
            case "1WEEK":
            case "1MONTH":
            case "1YEAR":
                if (whereClause.isEmpty()) {
                    whereClause = " WHERE date >= ?";
                } else {
                    whereClause += " AND date >= ?";
                }
                break;
        }
        
        return baseQuery + whereClause + " ORDER BY date DESC";
    }
    
    private void setFilterParameters(PreparedStatement stmt) throws SQLException {
        LocalDate now = LocalDate.now();
        int paramIndex = 1;
        
        switch (currentFilter) {
            case "1WEEK":
                stmt.setDate(paramIndex, java.sql.Date.valueOf(now.minusWeeks(1)));
                break;
            case "1MONTH":
                stmt.setDate(paramIndex, java.sql.Date.valueOf(now.minusMonths(1)));
                break;
            case "1YEAR":
                stmt.setDate(paramIndex, java.sql.Date.valueOf(now.minusYears(1)));
                break;
        }
    }
    
    private void updateTotals() {
        String sql = buildTotalQuery();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setFilterParameters(stmt);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal total = rs.getBigDecimal("total");
                    BigDecimal maxAmount = rs.getBigDecimal("max_amount");
                    
                    totalSpendingLabel.setText(formatCurrency(total != null ? total : BigDecimal.ZERO));
                    mostSpendingLabel.setText(formatCurrency(maxAmount != null ? maxAmount : BigDecimal.ZERO));
                }
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to calculate totals: " + e.getMessage());
        }
    }
    
    private String buildTotalQuery() {
        String baseQuery = "SELECT SUM(amount) as total, MAX(amount) as max_amount FROM spending";
        String whereClause = "";
        
        if (selectedMonth != null && !selectedMonth.equals("All Months")) {
            int monthNum = monthFilterComboBox.getSelectionModel().getSelectedIndex(); 
            whereClause = " WHERE EXTRACT(MONTH FROM date) = " + monthNum;
        }

        if (selectedYear != null && !selectedYear.isEmpty()) {
            if (whereClause.isEmpty()) {
                whereClause = " WHERE EXTRACT(YEAR FROM date) = " + selectedYear;
            } else {
                whereClause += " AND EXTRACT(YEAR FROM date) = " + selectedYear;
            }
        }
        
        // Time-based filter
        switch (currentFilter) {
            case "1WEEK":
            case "1MONTH":
            case "1YEAR":
                if (whereClause.isEmpty()) {
                    whereClause = " WHERE date >= ?";
                } else {
                    whereClause += " AND date >= ?";
                }
                break;
        }
        
        return baseQuery + whereClause;
    }
    
    @FXML
    private void filterAll() {
        currentFilter = "ALL";
        updateFilterButtonStyles();
        loadSpendingData();
        updateTotals();
    }
    
    @FXML
    private void filter1Week() {
        currentFilter = "1WEEK";
        updateFilterButtonStyles();
        loadSpendingData();
        updateTotals();
    }
    
    @FXML
    private void filter1Month() {
        currentFilter = "1MONTH";
        updateFilterButtonStyles();
        loadSpendingData();
        updateTotals();
    }
    
    @FXML
    private void filter1Year() {
        currentFilter = "1YEAR";
        updateFilterButtonStyles();
        loadSpendingData();
        updateTotals();
    }

    @FXML
    private void resetFilters() {
        monthFilterComboBox.setValue("All Months");
        selectedMonth = "All Months";
        yearFilterField.clear();
        selectedYear = null;
        currentFilter = "ALL";
        updateFilterButtonStyles();
        loadSpendingData();
        updateTotals();
    }
    
    private void updateFilterButtonStyles() {
        String activeStyle = "-fx-background-color: #1F6E8C; -fx-text-fill: white; -fx-background-radius: 15; -fx-font-size: 11px; -fx-padding: 5 15;";
        String inactiveStyle = "-fx-background-color: transparent; -fx-border-color: #1F6E8C; -fx-border-width: 1; -fx-border-radius: 15; -fx-text-fill: #1F6E8C; -fx-background-radius: 15; -fx-font-size: 11px; -fx-padding: 5 15;";
        
        // Reset all buttons to inactive style
        filterAllBtn.setStyle(inactiveStyle);
        filter1WeekBtn.setStyle(inactiveStyle);
        filter1MonthBtn.setStyle(inactiveStyle);
        filter1YearBtn.setStyle(inactiveStyle);
        
        // Set active button style
        switch (currentFilter) {
            case "ALL":
                filterAllBtn.setStyle(activeStyle);
                break;
            case "1WEEK":
                filter1WeekBtn.setStyle(activeStyle);
                break;
            case "1MONTH":
                filter1MonthBtn.setStyle(activeStyle);
                break;
            case "1YEAR":
                filter1YearBtn.setStyle(activeStyle);
                break;
        }
    }
    
    @FXML
    private void addSpending() {
        SpendingCategory category = categoryComboBox.getValue();
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        
        if (category == null || description.isEmpty() || amountText.isEmpty() || date == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountText.replace(",", "").replace(".", ""));
            
            String sql = "INSERT INTO spending (category, description, amount, date) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, category.name());
                stmt.setString(2, description);
                stmt.setBigDecimal(3, amount);
                stmt.setDate(4, java.sql.Date.valueOf(date));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    clearFields();
                    loadSpendingData();
                    updateTotals();
                    showAlert("Success", "Spending added successfully!");
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to add spending: " + e.getMessage());
        }
    }
    
    @FXML
    private void updateSpending() {
        if (selectedId == -1) {
            showAlert("Error", "Please select a spending record to update");
            return;
        }
        
        SpendingCategory category = categoryComboBox.getValue();
        String description = descriptionField.getText().trim();
        String amountText = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        
        if (category == null || description.isEmpty() || amountText.isEmpty() || date == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }
        
        try {
            BigDecimal amount = new BigDecimal(amountText.replace(",", "").replace(".", ""));
            
            String sql = "UPDATE spending SET category = ?, description = ?, amount = ?, date = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, category.name());
                stmt.setString(2, description);
                stmt.setBigDecimal(3, amount);
                stmt.setDate(4, java.sql.Date.valueOf(date));
                stmt.setInt(5, selectedId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    clearFields();
                    selectedId = -1;
                    spendingTable.getSelectionModel().clearSelection();
                    loadSpendingData();
                    updateTotals();
                    showAlert("Success", "Spending updated successfully!");
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to update spending: " + e.getMessage());
        }
    }
    
    @FXML
    private void deleteSpending() {
        if (selectedId == -1) {
            showAlert("Error", "Please select a spending record to delete");
            return;
        }
        
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Spending");
        confirmAlert.setContentText("Are you sure you want to delete this spending record?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String sql = "DELETE FROM spending WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setInt(1, selectedId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    clearFields();
                    selectedId = -1;
                    spendingTable.getSelectionModel().clearSelection();
                    loadSpendingData();
                    updateTotals();
                    showAlert("Success", "Spending deleted successfully!");
                }
            } catch (SQLException e) {
                showAlert("Database Error", "Failed to delete spending: " + e.getMessage());
            }
        }
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
    
    private void populateFields(SpendingDisplay display) {
        // Set category
        try {
            SpendingCategory category = SpendingCategory.valueOf(display.getCategory());
            categoryComboBox.setValue(category);
        } catch (IllegalArgumentException e) {
            categoryComboBox.setValue(SpendingCategory.OTHER);
        }
        
        descriptionField.setText(display.getDescription());
        
        // Parse amount (remove currency formatting)
        String amountText = display.getAmount().replace("Rp ", "").replace(".", "").replace(",", "");
        amountField.setText(amountText);
        
        // Parse date
        LocalDate date = LocalDate.parse(display.getDate());
        datePicker.setValue(date);
    }
    
    private void clearFields() {
        categoryComboBox.setValue(null);
        descriptionField.clear();
        amountField.clear();
        datePicker.setValue(null);
    }
    
    private String formatCurrency(BigDecimal amount) {
        DecimalFormat df = new DecimalFormat("#,###");
        return "Rp " + df.format(amount);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Display class for TableView
    public static class SpendingDisplay {
        private int id;
        private String category;
        private String description;
        private String amount;
        private String date;
        
        public SpendingDisplay(int id, String category, String description, String amount, String date) {
            this.id = id;
            this.category = category;
            this.description = description;
            this.amount = amount;
            this.date = date;
        }
        
        public int getId() { return id; }
        public String getCategory() { return category; }
        public String getDescription() { return description; }
        public String getAmount() { return amount; }
        public String getDate() { return date; }
    }
}
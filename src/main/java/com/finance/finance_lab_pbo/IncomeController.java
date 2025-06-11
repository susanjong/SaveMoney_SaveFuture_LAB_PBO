package com.finance.finance_lab_pbo;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import com.finance.finance_lab_pbo.model.Income;

import javafx.beans.property.SimpleIntegerProperty;
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class IncomeController implements Initializable {

    // Navigation buttons
    @FXML private Button profileBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button incomeBtn;
    @FXML private Button spendingBtn;

    // Table and columns
    @FXML private TableView<IncomeEntry> incomeTable;
    @FXML private TableColumn<IncomeEntry, Integer> noColumn;
    @FXML private TableColumn<IncomeEntry, String> sourceColumn;
    @FXML private TableColumn<IncomeEntry, String> amountColumn;
    @FXML private TableColumn<IncomeEntry, String> dateColumn;
    @FXML private TableColumn<IncomeEntry, String> descriptionColumn;

    // Input fields
    @FXML private TextField sourceField;
    @FXML private TextField amountField;
    @FXML private DatePicker datePicker;
    @FXML private TextField descriptionField;

    // Labels
    @FXML private Label totalIncomeLabel;
    @FXML private Label mostIncomeLabel;

    //Filters
    @FXML private Button filterAllBtn;
    @FXML private Button filter1WeekBtn;
    @FXML private Button filter1MonthBtn;
    @FXML private Button filter1YearBtn;
    @FXML 
    private TextField yearFilterField;
    private String selectedYear = null;
    @FXML
    private ComboBox<String> monthFilterComboBox;
    private String selectedMonth = null;

    // Data
    private ObservableList<IncomeEntry> incomeData = FXCollections.observableArrayList();
    private int selectedId = -1;
    private String currentFilter = "ALL";
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();

        ObservableList<String> months = FXCollections.observableArrayList(
            "All Months", "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        );
        monthFilterComboBox.setItems(months);

        monthFilterComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            selectedMonth = newVal;
            loadIncomeFromDatabase();
            updateSummaryLabels();
        });

        yearFilterField.textProperty().addListener((obs, oldVal, newVal) -> {
            selectedYear = newVal.trim().isEmpty() ? null : newVal.trim();
            loadIncomeFromDatabase();
            updateSummaryLabels();
        });
        
        loadIncomeFromDatabase();
        incomeTable.setItems(incomeData);
        updateSummaryLabels();
        datePicker.setValue(LocalDate.now());
        
        incomeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void setupTableColumns() {
        noColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        // Make table columns non-resizable for consistent appearance
        noColumn.setResizable(false);
        sourceColumn.setResizable(false);
        amountColumn.setResizable(false);
        dateColumn.setResizable(false);
        descriptionColumn.setResizable(false);
    }

    private void loadIncomeFromDatabase() {
        incomeData.clear();
        String sql = buildFilterQuery();
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            setFilterParameters(stmt);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String source = rs.getString("source");
                    BigDecimal amount = rs.getBigDecimal("amount");
                    Date date = rs.getDate("date");
                    String description = rs.getString("description");
                    
                    String formattedAmount = formatCurrency(amount.doubleValue());
                    String formattedDate = date.toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                    
                    incomeData.add(new IncomeEntry(id, source, formattedAmount, formattedDate, description));
                }
            }
            
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading income data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String buildFilterQuery() {
        String baseQuery = "SELECT * FROM income";
        String whereClause = "";
        
        // Month filter
        if (selectedMonth != null && !selectedMonth.equals("All Months")) {
            int monthNum = monthFilterComboBox.getSelectionModel().getSelectedIndex();
            whereClause = " WHERE EXTRACT(MONTH FROM date) = " + monthNum;
        }
        
        // Year filter
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
        
        return baseQuery + whereClause + " ORDER BY id";
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

    @FXML
    private void filterAll() {
        currentFilter = "ALL";
        updateFilterButtonStyles();
        loadIncomeFromDatabase();
        updateSummaryLabels();
    }
    
    @FXML
    private void filter1Week() {
        currentFilter = "1WEEK";
        updateFilterButtonStyles();
        loadIncomeFromDatabase();
        updateSummaryLabels();
    }
    
    @FXML
    private void filter1Month() {
        currentFilter = "1MONTH";
        updateFilterButtonStyles();
        loadIncomeFromDatabase();
        updateSummaryLabels();
    }
    
    @FXML
    private void filter1Year() {
        currentFilter = "1YEAR";
        updateFilterButtonStyles();
        loadIncomeFromDatabase();
        updateSummaryLabels();
    }

    @FXML
    private void resetFilters() {
        monthFilterComboBox.setValue("All Months");
        selectedMonth = "All Months";
        yearFilterField.clear();
        selectedYear = null;
        currentFilter = "ALL";
        updateFilterButtonStyles();
        loadIncomeFromDatabase();
        updateSummaryLabels();
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

    private void populateFields(IncomeEntry entry) {
        sourceField.setText(entry.getSource());
        // Parse amount from formatted string
        String amountStr = entry.getAmount().replace("Rp ", "").replace(".", "");
        amountField.setText(amountStr);
        // Parse date
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate date = LocalDate.parse(entry.getDate(), formatter);
        datePicker.setValue(date);
        descriptionField.setText(entry.getDescription());
    }

    private void updateSummaryLabels() {
        double total = 0.0;
        double highest = 0.0;

        for (IncomeEntry entry : incomeData) {
            double amount = parseAmount(entry.getAmount());
            total += amount;
            if (amount > highest) {
                highest = amount;
            }
        }

        totalIncomeLabel.setText(formatCurrency(total));
        mostIncomeLabel.setText(formatCurrency(highest));
    }

    private double parseAmount(String amountStr) {
        return Double.parseDouble(amountStr.replace("Rp ", "").replace(".", ""));
    }

    private String formatCurrency(double amount) {
        return "Rp " + String.format("%,.0f", amount).replace(",", ".");
    }

    @FXML
    private void addIncome() {
        String source = sourceField.getText().trim();
        String amountStr = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText().trim();

        if (source.isEmpty() || amountStr.isEmpty() || date == null || description.isEmpty()) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            
            // Create Income object using OOP model
            Income income = new Income(source, amount, date, description);
            
            // Save to database
            String sql = "INSERT INTO income (source, amount, date, description) VALUES (?, ?, ?, ?)";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, income.getSource());
                stmt.setBigDecimal(2, income.getAmount());
                stmt.setDate(3, Date.valueOf(income.getDate()));
                stmt.setString(4, income.getDescription());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Clear input fields
                    clearFields();
                    
                    // Reload data from database
                    loadIncomeFromDatabase();
                    
                    // Update summary
                    updateSummaryLabels();
                    
                    showAlert("Success", "Income added successfully!");
                } else {
                    showAlert("Error", "Failed to add income.");
                }
                
            } catch (SQLException e) {
                showAlert("Database Error", "Error adding income: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount.");
        }
    }

    @FXML
    private void deleteIncome() {
        IncomeEntry selectedEntry = incomeTable.getSelectionModel().getSelectedItem();
        
        if (selectedEntry == null) {
            showAlert("Error", "Please select an income to delete.");
            return;
        }

        String sql = "DELETE FROM income WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, selectedEntry.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Reload data from database
                loadIncomeFromDatabase();
                updateSummaryLabels();
                
                // Clear input fields
                clearFields();
                
                showAlert("Success", "Income deleted successfully!");
            } else {
                showAlert("Error", "Failed to delete income.");
            }
            
        } catch (SQLException e) {
            showAlert("Database Error", "Error deleting income: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void updateIncome() {
        IncomeEntry selectedEntry = incomeTable.getSelectionModel().getSelectedItem();
        
        if (selectedEntry == null) {
            showAlert("Error", "Please select an income to update.");
            return;
        }

        String source = sourceField.getText().trim();
        String amountStr = amountField.getText().trim();
        LocalDate date = datePicker.getValue();
        String description = descriptionField.getText().trim();

        if (source.isEmpty() || amountStr.isEmpty() || date == null || description.isEmpty()) {
            showAlert("Error", "Please fill in all fields to update.");
            return;
        }

        try {
            BigDecimal amount = new BigDecimal(amountStr);
            
            // Create Income object using OOP model
            Income income = new Income(source, amount, date, description);
            
            // Update in database
            String sql = "UPDATE income SET source = ?, amount = ?, date = ?, description = ? WHERE id = ?";
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                
                stmt.setString(1, income.getSource());
                stmt.setBigDecimal(2, income.getAmount());
                stmt.setDate(3, Date.valueOf(income.getDate()));
                stmt.setString(4, income.getDescription());
                stmt.setInt(5, selectedEntry.getId());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Clear input fields
                    clearFields();
                    
                    // Reload data from database
                    loadIncomeFromDatabase();
                    updateSummaryLabels();
                    
                    showAlert("Success", "Income updated successfully!");
                } else {
                    showAlert("Error", "Failed to update income.");
                }
                
            } catch (SQLException e) {
                showAlert("Database Error", "Error updating income: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount.");
        }
    }

    private void clearFields() {
        sourceField.clear();
        amountField.clear();
        datePicker.setValue(LocalDate.now());
        descriptionField.clear();
    }

    // Navigation methods
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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Inner class for Income Entry data model (for TableView display)
    public static class IncomeEntry {
        private SimpleIntegerProperty id;
        private SimpleStringProperty source;
        private SimpleStringProperty amount;
        private SimpleStringProperty date;
        private SimpleStringProperty description;

        public IncomeEntry(int id, String source, String amount, String date, String description) {
            this.id = new SimpleIntegerProperty(id);
            this.source = new SimpleStringProperty(source);
            this.amount = new SimpleStringProperty(amount);
            this.date = new SimpleStringProperty(date);
            this.description = new SimpleStringProperty(description);
        }

        // Getters
        public int getId() { return id.get(); }
        public String getSource() { return source.get(); }
        public String getAmount() { return amount.get(); }
        public String getDate() { return date.get(); }
        public String getDescription() { return description.get(); }

        // Setters
        public void setId(int id) { this.id.set(id); }
        public void setSource(String source) { this.source.set(source); }
        public void setAmount(String amount) { this.amount.set(amount); }
        public void setDate(String date) { this.date.set(date); }
        public void setDescription(String description) { this.description.set(description); }
    }
}
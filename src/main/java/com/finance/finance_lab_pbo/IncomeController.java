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
import java.util.ArrayList;
import java.util.List;
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
    @FXML private TableColumn<IncomeEntry, String> noColumn;
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

    // Data - Store all data in memory
    private List<Income> allIncomeData = new ArrayList<>(); // Raw data from database
    private ObservableList<IncomeEntry> incomeData = FXCollections.observableArrayList(); // Filtered data for table
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
            applyFilters(); 
        });

        yearFilterField.textProperty().addListener((obs, oldVal, newVal) -> {
            selectedYear = newVal.trim().isEmpty() ? null : newVal.trim();
            applyFilters(); 
        });
        
        loadAllIncomeFromDatabase();
        incomeTable.setItems(incomeData);
        applyFilters(); 
        datePicker.setValue(LocalDate.now());
        
        incomeTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                populateFields(newSelection);
            }
        });
    }

    private void setupTableColumns() {
        noColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(incomeData.indexOf(cellData.getValue()) + 1)));
        sourceColumn.setCellValueFactory(new PropertyValueFactory<>("source"));
        amountColumn.setCellValueFactory(new PropertyValueFactory<>("amount"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        noColumn.setResizable(false);
        sourceColumn.setResizable(false);
        amountColumn.setResizable(false);
        dateColumn.setResizable(false);
        descriptionColumn.setResizable(false);
    }


    private void loadAllIncomeFromDatabase() {
        allIncomeData.clear();
        String sql = "SELECT * FROM income ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Income income = new Income(
                    rs.getString("source"),
                    rs.getBigDecimal("amount"),
                    rs.getDate("date").toLocalDate(),
                    rs.getString("description")
                );
                income.setId(rs.getInt("id"));
                allIncomeData.add(income);
            }
            
        } catch (SQLException e) {
            showAlert("Database Error", "Error loading income data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Filter data in memory instead of database
    private void applyFilters() {
        incomeData.clear();
        
        LocalDate now = LocalDate.now();
        LocalDate filterDate = null;
        
        switch (currentFilter) {
            case "1WEEK":
                filterDate = now.minusWeeks(1);
                break;
            case "1MONTH":
                filterDate = now.minusMonths(1);
                break;
            case "1YEAR":
                filterDate = now.minusYears(1);
                break;
        }
        
        int counter = 1;
        for (Income income : allIncomeData) {
            boolean matchesFilter = true;
            
            if (filterDate != null && income.getDate().isBefore(filterDate)) {
                matchesFilter = false;
            }

            if (selectedMonth != null && !selectedMonth.equals("All Months")) {
                int monthNum = monthFilterComboBox.getSelectionModel().getSelectedIndex();
                if (income.getDate().getMonthValue() != monthNum) {
                    matchesFilter = false;
                }
            }

            if (selectedYear != null && !selectedYear.isEmpty()) {
                try {
                    int year = Integer.parseInt(selectedYear);
                    if (income.getDate().getYear() != year) {
                        matchesFilter = false;
                    }
                } catch (NumberFormatException e) {
                }
            }
            
            if (matchesFilter) {
                String formattedAmount = formatCurrency(income.getAmount().doubleValue());
                String formattedDate = income.getDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
                
                incomeData.add(new IncomeEntry(
                    income.getId(), 
                    income.getSource(), 
                    formattedAmount, 
                    formattedDate, 
                    income.getDescription()
                ));
            }
        }
        
        updateSummaryLabels();
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
    private void filterAll() {
        currentFilter = "ALL";
        updateFilterButtonStyles();
        applyFilters();
    }
    
    @FXML
    private void filter1Week() {
        currentFilter = "1WEEK";
        updateFilterButtonStyles();
        applyFilters();
    }
    
    @FXML
    private void filter1Month() {
        currentFilter = "1MONTH";
        updateFilterButtonStyles();
        applyFilters();
    }
    
    @FXML
    private void filter1Year() {
        currentFilter = "1YEAR";
        updateFilterButtonStyles();
        applyFilters();
    }

    @FXML
    private void resetFilters() {
        monthFilterComboBox.setValue("All Months");
        selectedMonth = "All Months";
        yearFilterField.clear();
        selectedYear = null;
        currentFilter = "ALL";
        updateFilterButtonStyles();
        applyFilters();
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
                 PreparedStatement stmt = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
                
                stmt.setString(1, income.getSource());
                stmt.setBigDecimal(2, income.getAmount());
                stmt.setDate(3, Date.valueOf(income.getDate()));
                stmt.setString(4, income.getDescription());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    // Get generated ID
                    try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            income.setId(generatedKeys.getInt(1));
                        }
                    }
                    
                    // Add to memory list
                    allIncomeData.add(income);
                    
                    // Clear input fields
                    clearFields();
                    
                    // Reapply filters to update display
                    applyFilters();
                    
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
                // Remove from memory list
                allIncomeData.removeIf(income -> income.getId() == selectedEntry.getId());
                
                // Reapply filters to update display
                applyFilters();
                
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
            income.setId(selectedEntry.getId());
            
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
                    // Update in memory list
                    for (int i = 0; i < allIncomeData.size(); i++) {
                        if (allIncomeData.get(i).getId() == selectedEntry.getId()) {
                            allIncomeData.set(i, income);
                            break;
                        }
                    }
                    
                    // Clear input fields
                    clearFields();
                    
                    // Reapply filters to update display
                    applyFilters();
                    
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

        public int getId() { return id.get(); }
        public String getSource() { return source.get(); }
        public String getAmount() { return amount.get(); }
        public String getDate() { return date.get(); }
        public String getDescription() { return description.get(); }

        public void setId(int id) { this.id.set(id); }
        public void setSource(String source) { this.source.set(source); }
        public void setAmount(String amount) { this.amount.set(amount); }
        public void setDate(String date) { this.date.set(date); }
        public void setDescription(String description) { this.description.set(description); }
    }
}
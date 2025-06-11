package com.finance.finance_lab_pbo;

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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.ResourceBundle;

public class IncomeController implements Initializable {

    // Navigation buttons
    @FXML private Button profileBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button incomeBtn;
    @FXML private Button spendingBtn;

    // Table and columns
    @FXML private TableView<IncomeEntry> incomeTable;
    @FXML private TableColumn<IncomeEntry, Integer> noColumn;
    @FXML private TableColumn<IncomeEntry, String> activityColumn;
    @FXML private TableColumn<IncomeEntry, String> incomeColumn;
    @FXML private TableColumn<IncomeEntry, String> dateColumn;

    // Input fields
    @FXML private TextField activityField;
    @FXML private TextField incomeField;
    @FXML private DatePicker datePicker;

    // Labels
    @FXML private Label totalIncomeLabel;
    @FXML private Label mostIncomeLabel;

    // Action buttons
    @FXML private Button addActivityBtn;
    @FXML private Button deleteBtn;
    @FXML private Button updateBtn;

    // Data
    private ObservableList<IncomeEntry> incomeData = FXCollections.observableArrayList();
    private NumberFormat currencyFormat;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize currency formatter for Indonesian Rupiah
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        // Set up table columns
        setupTableColumns();
        
        // Load sample data
        loadSampleData();
        
        // Set table data
        incomeTable.setItems(incomeData);
        
        // Update summary labels
        updateSummaryLabels();
        
        // Set default date to today
        datePicker.setValue(LocalDate.now());
    }

    private void setupTableColumns() {
        noColumn.setCellValueFactory(new PropertyValueFactory<>("number"));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activity"));
        incomeColumn.setCellValueFactory(new PropertyValueFactory<>("income"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Make table columns non-resizable for consistent appearance
        noColumn.setResizable(false);
        activityColumn.setResizable(false);
        incomeColumn.setResizable(false);
        dateColumn.setResizable(false);
    }

    private void loadSampleData() {
        incomeData.add(new IncomeEntry(1, "Salary earning", "Rp. 10.000.000", "30-05-2025"));
        incomeData.add(new IncomeEntry(2, "Bonus earning", "Rp. 10.000.000", "30-05-2025"));
        incomeData.add(new IncomeEntry(3, "Salary earning", "Rp. 10.000.000", "30-05-2025"));
        incomeData.add(new IncomeEntry(4, "Bonus earning", "Rp. 10.000.000", "30-05-2025"));
    }

    private void updateSummaryLabels() {
        double total = 0.0;
        double highest = 0.0;

        for (IncomeEntry entry : incomeData) {
            double amount = parseIncomeAmount(entry.getIncome());
            total += amount;
            if (amount > highest) {
                highest = amount;
            }
        }

        totalIncomeLabel.setText(formatCurrency(total));
        mostIncomeLabel.setText(formatCurrency(highest));
    }

    private double parseIncomeAmount(String incomeStr) {
        // Remove "Rp. " and dots, then parse
        return Double.parseDouble(incomeStr.replace("Rp. ", "").replace(".", ""));
    }

    private String formatCurrency(double amount) {
        return "Rp " + String.format("%,.0f", amount).replace(",", ".");
    }

    @FXML
    private void addActivity() {
        String activity = activityField.getText().trim();
        String incomeStr = incomeField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (activity.isEmpty() || incomeStr.isEmpty() || date == null) {
            showAlert("Error", "Please fill in all fields.");
            return;
        }

        try {
            double incomeAmount = Double.parseDouble(incomeStr);
            String formattedIncome = "Rp. " + String.format("%,.0f", incomeAmount).replace(",", ".");
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            int nextNumber = incomeData.size() + 1;
            IncomeEntry newEntry = new IncomeEntry(nextNumber, activity, formattedIncome, formattedDate);
            incomeData.add(newEntry);

            // Clear input fields
            activityField.clear();
            incomeField.clear();
            datePicker.setValue(LocalDate.now());

            // Update summary
            updateSummaryLabels();

            showAlert("Success", "Income activity added successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid income amount.");
        }
    }

    @FXML
    private void deleteActivity() {
        IncomeEntry selectedEntry = incomeTable.getSelectionModel().getSelectedItem();
        
        if (selectedEntry == null) {
            showAlert("Error", "Please select an activity to delete.");
            return;
        }

        incomeData.remove(selectedEntry);
        
        // Update numbers
        for (int i = 0; i < incomeData.size(); i++) {
            incomeData.get(i).setNumber(i + 1);
        }
        
        incomeTable.refresh();
        updateSummaryLabels();
        
        showAlert("Success", "Activity deleted successfully!");
    }

    @FXML
    private void updateActivity() {
        IncomeEntry selectedEntry = incomeTable.getSelectionModel().getSelectedItem();
        
        if (selectedEntry == null) {
            showAlert("Error", "Please select an activity to update.");
            return;
        }

        String activity = activityField.getText().trim();
        String incomeStr = incomeField.getText().trim();
        LocalDate date = datePicker.getValue();

        if (activity.isEmpty() || incomeStr.isEmpty() || date == null) {
            showAlert("Error", "Please fill in all fields to update.");
            return;
        }

        try {
            double incomeAmount = Double.parseDouble(incomeStr);
            String formattedIncome = "Rp. " + String.format("%,.0f", incomeAmount).replace(",", ".");
            String formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));

            selectedEntry.setActivity(activity);
            selectedEntry.setIncome(formattedIncome);
            selectedEntry.setDate(formattedDate);

            incomeTable.refresh();
            updateSummaryLabels();

            // Clear input fields
            activityField.clear();
            incomeField.clear();
            datePicker.setValue(LocalDate.now());

            showAlert("Success", "Activity updated successfully!");

        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid income amount.");
        }
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

    // Inner class for Income Entry data model
    public static class IncomeEntry {
        private SimpleIntegerProperty number;
        private SimpleStringProperty activity;
        private SimpleStringProperty income;
        private SimpleStringProperty date;

        public IncomeEntry(int number, String activity, String income, String date) {
            this.number = new SimpleIntegerProperty(number);
            this.activity = new SimpleStringProperty(activity);
            this.income = new SimpleStringProperty(income);
            this.date = new SimpleStringProperty(date);
        }

        // Getters
        public int getNumber() { return number.get(); }
        public String getActivity() { return activity.get(); }
        public String getIncome() { return income.get(); }
        public String getDate() { return date.get(); }

        // Setters
        public void setNumber(int number) { this.number.set(number); }
        public void setActivity(String activity) { this.activity.set(activity); }
        public void setIncome(String income) { this.income.set(income); }
        public void setDate(String date) { this.date.set(date); }

        // Property methods for TableView
        public SimpleIntegerProperty numberProperty() { return number; }
        public SimpleStringProperty activityProperty() { return activity; }
        public SimpleStringProperty incomeProperty() { return income; }
        public SimpleStringProperty dateProperty() { return date; }
    }
}

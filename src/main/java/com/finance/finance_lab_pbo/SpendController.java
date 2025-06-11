package com.finance.finance_lab_pbo;

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
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

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
    private TableView<SpendingActivity> spendingTable;
    
    @FXML
    private TableColumn<SpendingActivity, String> noColumn;
    
    @FXML
    private TableColumn<SpendingActivity, String> activityColumn;
    
    @FXML
    private TableColumn<SpendingActivity, String> spendingColumn;
    
    @FXML
    private TableColumn<SpendingActivity, String> dateColumn;
    
    @FXML
    private TextField activityField;
    
    @FXML
    private TextField spendingField;
    
    @FXML
    private DatePicker datePicker;
    
    @FXML
    private Button addActivityBtn;
    
    @FXML
    private Button deleteBtn;
    
    @FXML
    private Button updateBtn;
    
    @FXML
    private Label totalSpendingLabel;
    
    @FXML
    private Label mostSpendingLabel;
    
    private ObservableList<SpendingActivity> spendingData = FXCollections.observableArrayList();
    private NumberFormat currencyFormat;
    private int selectedIndex = -1;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize currency formatter for Indonesian Rupiah
        currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        
        // Set up table columns
        noColumn.setCellValueFactory(cellData -> 
            new SimpleStringProperty(String.valueOf(spendingData.indexOf(cellData.getValue()) + 1)));
        activityColumn.setCellValueFactory(new PropertyValueFactory<>("activity"));
        spendingColumn.setCellValueFactory(new PropertyValueFactory<>("spending"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        
        // Set table data
        spendingTable.setItems(spendingData);
        
        // Add sample data
        addSampleData();
        
        // Set up table selection listener
        spendingTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedIndex = spendingData.indexOf(newSelection);
                populateFields(newSelection);
            }
        });
        
        // Update totals
        updateTotals();
    }
    
    private void addSampleData() {
        spendingData.add(new SpendingActivity("Salary earning", "Rp 10.000.000", "30-05-2025"));
        spendingData.add(new SpendingActivity("Bonus earning", "Rp 10.000.000", "30-05-2025"));
        spendingData.add(new SpendingActivity("Salary earning", "Rp 10.000.000", "30-05-2025"));
        spendingData.add(new SpendingActivity("Bonus earning", "Rp 10.000.000", "30-05-2025"));
    }
    
    @FXML
    private void addActivity() {
        String activity = activityField.getText().trim();
        String spendingText = spendingField.getText().trim();
        LocalDate date = datePicker.getValue();
        
        if (activity.isEmpty() || spendingText.isEmpty() || date == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }
        
        try {
            // Parse spending amount
            double spendingAmount = Double.parseDouble(spendingText.replace(",", "").replace(".", ""));
            
            // Format spending
            String formattedSpending = formatCurrency(spendingAmount);
            
            // Format date
            String formattedDate = date.toString();
            
            // Create new spending activity
            SpendingActivity newActivity = new SpendingActivity(activity, formattedSpending, formattedDate);
            spendingData.add(newActivity);
            
            // Clear fields
            clearFields();
            
            // Update totals
            updateTotals();
            
            showAlert("Success", "Activity added successfully!");
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid spending amount");
        }
    }
    
    @FXML
    private void updateActivity() {
        if (selectedIndex == -1) {
            showAlert("Error", "Please select an activity to update");
            return;
        }
        
        String activity = activityField.getText().trim();
        String spendingText = spendingField.getText().trim();
        LocalDate date = datePicker.getValue();
        
        if (activity.isEmpty() || spendingText.isEmpty() || date == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }
        
        try {
            // Parse spending amount
            double spendingAmount = Double.parseDouble(spendingText.replace(",", "").replace(".", ""));
            
            // Format spending
            String formattedSpending = formatCurrency(spendingAmount);
            
            // Format date
            String formattedDate = date.toString();
            
            // Update the selected activity
            SpendingActivity selectedActivity = spendingData.get(selectedIndex);
            selectedActivity.setActivity(activity);
            selectedActivity.setSpending(formattedSpending);
            selectedActivity.setDate(formattedDate);
            
            // Refresh table
            spendingTable.refresh();
            
            // Clear fields and selection
            clearFields();
            selectedIndex = -1;
            spendingTable.getSelectionModel().clearSelection();
            
            // Update totals
            updateTotals();
            
            showAlert("Success", "Activity updated successfully!");
            
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid spending amount");
        }
    }
    
    @FXML
    private void deleteActivity() {
        if (selectedIndex == -1) {
            showAlert("Error", "Please select an activity to delete");
            return;
        }
        
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Deletion");
        confirmAlert.setHeaderText("Delete Activity");
        confirmAlert.setContentText("Are you sure you want to delete this activity?");
        
        if (confirmAlert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            spendingData.remove(selectedIndex);
            clearFields();
            selectedIndex = -1;
            spendingTable.getSelectionModel().clearSelection();
            updateTotals();
            showAlert("Success", "Activity deleted successfully!");
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
    
    private void populateFields(SpendingActivity activity) {
        activityField.setText(activity.getActivity());
        
        // Parse spending amount (remove currency formatting)
        String spendingText = activity.getSpending().replace("Rp ", "").replace(".", "");
        spendingField.setText(spendingText);
        
        // Parse date
        LocalDate date = LocalDate.parse(activity.getDate());
        datePicker.setValue(date);
    }
    
    private void clearFields() {
        activityField.clear();
        spendingField.clear();
        datePicker.setValue(null);
    }
    
    private void updateTotals() {
        double totalSpending = 0;
        double maxSpending = 0;
        
        for (SpendingActivity activity : spendingData) {
            try {
                double amount = Double.parseDouble(activity.getSpending().replace("Rp ", "").replace(".", ""));
                totalSpending += amount;
                if (amount > maxSpending) {
                    maxSpending = amount;
                }
            } catch (NumberFormatException e) {
                // Skip invalid amounts
            }
        }
        
        totalSpendingLabel.setText(formatCurrency(totalSpending));
        mostSpendingLabel.setText(formatCurrency(maxSpending));
    }
    
    private String formatCurrency(double amount) {
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
    
    // Inner class for SpendingActivity
    public static class SpendingActivity {
        private String activity;
        private String spending;
        private String date;
        
        public SpendingActivity(String activity, String spending, String date) {
            this.activity = activity;
            this.spending = spending;
            this.date = date;
        }
        
        public String getActivity() {
            return activity;
        }
        
        public void setActivity(String activity) {
            this.activity = activity;
        }
        
        public String getSpending() {
            return spending;
        }
        
        public void setSpending(String spending) {
            this.spending = spending;
        }
        
        public String getDate() {
            return date;
        }
        
        public void setDate(String date) {
            this.date = date;
        }
    }
}
package com.finance.finance_lab_pbo;

import java.io.IOException;
import java.net.URL;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class ProfileController {

    @FXML
    private TextField bioTextField;

    @FXML
    private TextArea bioTextArea;

    @FXML
    private AnchorPane bioPopup;

    @FXML
    private AnchorPane popupOverlay;

    @FXML
    private Button deleteBtn;

    @FXML
    private Button updateBtn;

    @FXML
    private Button dashboardBtn;

    @FXML
    private Button profileBtn;

    @FXML
    private Button incomeBtn;

    @FXML
    private Button spendingBtn;

    private String currentBio = "";

    /**
     * Initialize method called after FXML loading
     */
    @FXML
    private void initialize() {
        // Set initial bio text if exists
        if (!currentBio.isEmpty()) {
            bioTextField.setText(currentBio);
            bioTextArea.setText(currentBio);
        }

        // Make overlay clickable to close popup
        popupOverlay.setOnMouseClicked(event -> closeBioPopup());
    }

    /**
     * Show bio editing popup when bio text field is clicked
     */
    @FXML
    private void showBioPopup() {
        // Copy current text from text field to text area
        bioTextArea.setText(bioTextField.getText());

        // Show overlay and popup
        popupOverlay.setVisible(true);
        bioPopup.setVisible(true);

        // Bring popup to front
        bioPopup.toFront();

        // Focus on text area for immediate editing
        bioTextArea.requestFocus();
    }

    /**
     * Close bio editing popup
     */
    @FXML
    private void closeBioPopup() {
        bioPopup.setVisible(false);
        popupOverlay.setVisible(false);
    }

    /**
     * Update bio text when Update button is clicked
     */
    @FXML
    private void updateBio() {
        // Get text from text area
        String newBio = bioTextArea.getText().trim();

        // Update the main text field
        bioTextField.setText(newBio);
        currentBio = newBio;

        // Close popup
        closeBioPopup();

        // Optional: Show success message or perform additional actions
        System.out.println("Bio updated successfully: " + newBio);
    }

    /**
     * Delete bio text when Delete button is clicked
     */
    @FXML
    private void deleteBio() {
        // Clear both text areas
        bioTextArea.setText("");
        bioTextField.setText("");
        currentBio = "";

        // Close popup
        closeBioPopup();

        // Optional: Show confirmation message
        System.out.println("Bio deleted successfully");
    }

    /**
     * Handle navigation between different sections
     * This method can be expanded based on your navigation requirements
     */
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

    /**
     * Method to set bio text programmatically
     * @param bio The bio text to set
     */
    public void setBio(String bio) {
        this.currentBio = bio;
        if (bioTextField != null) {
            bioTextField.setText(bio);
        }
        if (bioTextArea != null) {
            bioTextArea.setText(bio);
        }
    }

    /**
     * Method to get current bio text
     * @return Current bio text
     */
    public String getBio() {
        return currentBio;
    }
}
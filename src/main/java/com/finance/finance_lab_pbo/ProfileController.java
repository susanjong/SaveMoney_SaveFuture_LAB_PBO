package com.finance.finance_lab_pbo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

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
    private Button dashboardBtn1;

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
    private void handleNavigation() {
        // Navigation logic can be implemented here
        // For example, switching between different views/scenes

        // Get the source button to determine which navigation was clicked
        // This is a basic implementation - you can expand it based on your needs
        System.out.println("Navigation button clicked");
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
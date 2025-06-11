package com.finance.finance_lab_pbo;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;

public class ProfileController {

    @FXML
    private TextField bioTextField;

    @FXML
    private TextArea bioTextArea;

    @FXML
    private Label bioDisplayLabel;

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
            bioDisplayLabel.setText(currentBio);
        } else {
            bioDisplayLabel.setText("Click below to add your bio...");
        }

        // Make sure text field is empty initially
        bioTextField.setText("");

        // Make overlay clickable to close popup
        popupOverlay.setOnMouseClicked(event -> closeBioPopup());
    }

    /**
     * Show bio editing popup when bio text field is clicked
     */
    @FXML
    private void showBioPopup() {
        // Set text area to current bio (not from text field)
        bioTextArea.setText(currentBio);

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

        // Update the current bio
        currentBio = newBio;

        // Clear the text field (keep it empty)
        bioTextField.setText("");

        // Update only the display label
        if (newBio.isEmpty()) {
            bioDisplayLabel.setText("Click below to add your bio...");
        } else {
            bioDisplayLabel.setText(newBio);
        }

        // Close popup
        closeBioPopup();

        // Optional: Show success message
        System.out.println("Bio updated successfully: " + newBio);
    }

    /**
     * Delete bio text when Delete button is clicked
     */
    @FXML
    private void deleteBio() {
        // Clear text area and current bio
        bioTextArea.setText("");
        currentBio = "";

        // Keep text field empty
        bioTextField.setText("");

        // Reset display label to default text
        bioDisplayLabel.setText("Click below to add your bio...");

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

        // Keep text field empty
        if (bioTextField != null) {
            bioTextField.setText("");
        }

        // Set text area to current bio when needed
        if (bioTextArea != null) {
            bioTextArea.setText(bio);
        }

        // Update display label
        if (bioDisplayLabel != null) {
            if (bio.isEmpty()) {
                bioDisplayLabel.setText("Click below to add your bio...");
            } else {
                bioDisplayLabel.setText(bio);
            }
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
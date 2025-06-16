package com.finance.finance_lab_pbo;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javafx.scene.Node;


public class ProfileController {

    // FXML Components from your FXML file
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

    // New labels for dynamic user information
    @FXML
    private Label usernameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Button changePasswordBtn;

    @FXML
    private Button deleteAccountBtn;

    // Navigation buttons
    @FXML private Button profileBtn;
    @FXML private Button dashboardBtn;
    @FXML private Button incomeBtn;
    @FXML private Button spendingBtn;

    // User data
    private String currentBio = "";
    private int currentUserId;
    private String currentUsername;
    private String currentEmail;

    /**
     * Initialize method called after FXML loading
     */
    @FXML
    private void initialize() {
        System.out.println("ProfileController initialized");
        
        // Load current user data from session
        loadCurrentUserData();
        
        // Load user's bio from database
        loadUserProfile();
        
        // Set up bio display
        setupBioDisplay();

        // Update UI with user information
        updateUserInterface();

        // Make overlay clickable to close popup
        if (popupOverlay != null) {
            popupOverlay.setOnMouseClicked(event -> {
                // Only close if clicking on the overlay itself, not the popup
                if (event.getTarget() == popupOverlay) {
                    closeBioPopup();
                }
            });
        }
    }

    /**
     * Load current user data from UserSession
     */
    private void loadCurrentUserData() {
        try {
            // Check if user is logged in
            if (!UserSession.isUserLoggedIn()) {
                System.out.println("No user logged in, redirecting to login page");
                redirectToLogin();
                return;
            }
            
            // Get current user data from session
            currentUserId = UserSession.getCurrentUserId();
            currentUsername = UserSession.getCurrentUsername();
            currentEmail = UserSession.getCurrentEmail();
            
            System.out.println("Loaded user data - ID: " + currentUserId + ", Username: " + currentUsername);
            
        } catch (Exception e) {
            System.err.println("Error loading user data: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to load user data.");
        }
    }

    /**
     * Load user profile data from database
     */
    private void loadUserProfile() {
        if (currentUserId == -1) {
            return;
        }

        String query = "SELECT name, email, bio FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setInt(1, currentUserId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // Update user info
                String dbName = rs.getString("name");
                String dbEmail = rs.getString("email");
                String dbBio = rs.getString("bio");
                
                // Update session if needed
                if (!currentUsername.equals(dbName) || !currentEmail.equals(dbEmail)) {
                    UserSession.setCurrentUser(currentUserId, dbName, dbEmail);
                    currentUsername = dbName;
                    currentEmail = dbEmail;
                }
                
                // Set bio
                currentBio = (dbBio != null) ? dbBio : "";
                
                System.out.println("Profile loaded successfully for user: " + currentUsername);
                
            } else {
                System.err.println("User not found in database");
                showAlert(Alert.AlertType.ERROR, "Error", "User profile not found.");
                redirectToLogin();
            }
            
        } catch (SQLException e) {
            System.err.println("Error loading user profile: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load user profile.");
        }
    }

    /**
     * Update UI components with current user information
     */
    private void updateUserInterface() {
        // Update username label
        if (usernameLabel != null && currentUsername != null) {
            usernameLabel.setText(currentUsername);
        }

        // Update email label
        if (emailLabel != null && currentEmail != null) {
            emailLabel.setText("Email : " + currentEmail);
        }
    }

    /**
     * Set up bio display based on current bio
     */
    private void setupBioDisplay() {
        if (bioDisplayLabel != null) {
            if (currentBio.isEmpty()) {
                bioDisplayLabel.setText("Click below to add your bio...");
            } else {
                bioDisplayLabel.setText(currentBio);
            }
        }

        // Keep bio text field empty (it's just a clickable area)
        if (bioTextField != null) {
            bioTextField.setText("");
            bioTextField.setEditable(false); // Make it non-editable since it's just for display
        }
    }

    /**
     * Show bio editing popup when bio text field is clicked
     */
    @FXML
    private void showBioPopup() {
        System.out.println("Opening bio popup");
        
        // Set text area to current bio
        if (bioTextArea != null) {
            bioTextArea.setText(currentBio);
        }

        // Show overlay and popup
        if (popupOverlay != null) {
            popupOverlay.setVisible(true);
        }
        if (bioPopup != null) {
            bioPopup.setVisible(true);
            bioPopup.toFront();
        }

        // Focus on text area for immediate editing
        if (bioTextArea != null) {
            bioTextArea.requestFocus();
        }
    }

    /**
     * Close bio editing popup
     */
    @FXML
    private void closeBioPopup() {
        System.out.println("Closing bio popup");
        
        if (bioPopup != null) {
            bioPopup.setVisible(false);
        }
        if (popupOverlay != null) {
            popupOverlay.setVisible(false);
        }
    }

    /**
     * Update bio when Update button is clicked
     */
    @FXML
    private void updateBio() {
        if (bioTextArea == null) {
            return;
        }
        
        // Get new bio text
        String newBio = bioTextArea.getText().trim();
        
        // Update bio in database
        if (saveBioToDatabase(newBio)) {
            // Update local bio
            currentBio = newBio;
            
            // Update display
            setupBioDisplay();
            
            // Close popup
            closeBioPopup();
            
            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Success", "Bio updated successfully!");
            
            System.out.println("Bio updated successfully");
        }
    }

    /**
     * Delete bio when Delete button is clicked
     */
    @FXML
    private void deleteBio() {
        // Confirm deletion
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Delete");
        confirmAlert.setHeaderText("Delete Bio");
        confirmAlert.setContentText("Are you sure you want to delete your bio?");
        
        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            // Delete bio from database
            if (saveBioToDatabase("")) {
                // Clear local bio
                currentBio = "";
                
                // Clear text area
                if (bioTextArea != null) {
                    bioTextArea.setText("");
                }
                
                // Update display
                setupBioDisplay();
                
                // Close popup
                closeBioPopup();
                
                // Show success message
                showAlert(Alert.AlertType.INFORMATION, "Success", "Bio deleted successfully!");
                
                System.out.println("Bio deleted successfully");
            }
        }
    }

    /**
     * Save bio to database
     * @param bio The bio text to save
     * @return true if successful, false otherwise
     */
    private boolean saveBioToDatabase(String bio) {
        String query = "UPDATE users SET bio = ? WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, bio);
            pstmt.setInt(2, currentUserId);
            
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected > 0) {
                System.out.println("Bio saved successfully to database");
                return true;
            } else {
                System.err.println("No rows affected when updating bio");
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update bio in database.");
                return false;
            }
            
        } catch (SQLException e) {
            System.err.println("Error saving bio to database: " + e.getMessage());
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to save bio to database.");
            return false;
        }
    }

    /**
     * Show alert dialog
     * @param alertType Type of alert
     * @param title Title of the alert
     * @param message Message to display
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
 * Fixed redirectToLogin method
 */
private void redirectToLogin() {
    try {
        // Method 1: Using getResource to get the FXML file location
        URL fxmlLocation = getClass().getResource("/com/finance/finance_lab_pbo/Login.fxml"); // Adjust path as needed
        if (fxmlLocation == null) {
            System.err.println("Could not find login.fxml file");
            return;
        }
        
        FXMLLoader loader = new FXMLLoader(fxmlLocation);
        Parent loginRoot = loader.load();
        
        // Get current stage
        Stage currentStage = (Stage) ((Node) profileBtn).getScene().getWindow(); // Adjust profilePane to your actual pane
        
        // Create new scene and set it
        Scene loginScene = new Scene(loginRoot);
        currentStage.setScene(loginScene);
        currentStage.setTitle("Login - Finance Lab");
        currentStage.show();
        
    } catch (IOException e) {
        System.err.println("Error loading login screen: " + e.getMessage());
        e.printStackTrace();
        
        // Fallback: Show error and close application
        showAlert(Alert.AlertType.ERROR, "Navigation Error", 
                 "Unable to return to login screen. Application will close.");
        Platform.exit();
    }
}

// Navigation methods - CORRECTED VERSION
@FXML
void handleNavigation(ActionEvent event) {
    Object source = event.getSource();
    
    try {
        String fxmlFile = "";
        
        // Fixed: Profile button should stay on profile page or you might want to remove this case
        if (source == profileBtn) {
            // Already on profile page, so maybe just return or refresh
            return;
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
                // Try alternative paths
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

    /**
     * Handle change password button click
     */
    @FXML
    private void changePassword(ActionEvent event) {
        try {
        // Load change password page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/finance_lab_pbo/changepassword.fxml"));
        Parent root = loader.load();
        
        // Get current stage
        Stage currentStage = (Stage) ((Node) profileBtn).getScene().getWindow();
        
        // Set new scene
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle("Change Password - Finance Lab");
        currentStage.show();
        
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load change password page.");
    }
}

    /**
     * Alternative method name for change password (backwards compatibility)
     */
    @FXML
    private void handleChangePassword(ActionEvent event) {
       try {
        // Load change password page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/finance_lab_pbo/changepassword.fxml"));
        Parent root = loader.load();
        
        // Get current stage
        Stage currentStage = (Stage) ((Node) profileBtn).getScene().getWindow();
        
        // Set new scene
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle("Change Password - Finance Lab");
        currentStage.show();
        
    } catch (IOException e) {
        e.printStackTrace();
        showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load change password page.");
    }
}

  /**
 * Handle delete account button click - SIMPLE VERSION
 */
@FXML
private void deleteAccount(ActionEvent event) {
    // First confirmation dialog
    Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
    confirmAlert.setTitle("Confirm Account Deletion");
    confirmAlert.setHeaderText("Delete Account");
    confirmAlert.setContentText("Are you sure you want to delete your account? This action cannot be undone.");
    
    // Add custom buttons to make it more explicit
    ButtonType deleteButton = new ButtonType("Delete Account", ButtonBar.ButtonData.OK_DONE);
    ButtonType cancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
    confirmAlert.getButtonTypes().setAll(deleteButton, cancelButton);
    
    Optional<ButtonType> result = confirmAlert.showAndWait();
    if (result.isPresent() && result.get() == deleteButton) {
        // Second confirmation
        Alert finalConfirm = new Alert(Alert.AlertType.WARNING);
        finalConfirm.setTitle("Final Confirmation");
        finalConfirm.setHeaderText("Last Warning");
        finalConfirm.setContentText("This will permanently delete your account and all associated data. Are you absolutely sure?");
        
        ButtonType finalDeleteButton = new ButtonType("Yes, Delete Forever", ButtonBar.ButtonData.OK_DONE);
        ButtonType finalCancelButton = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        finalConfirm.getButtonTypes().setAll(finalDeleteButton, finalCancelButton);
        
        Optional<ButtonType> finalResult = finalConfirm.showAndWait();
        if (finalResult.isPresent() && finalResult.get() == finalDeleteButton) {
            // Perform account deletion
            if (performCompleteAccountDeletion()) {
                showAlert(Alert.AlertType.INFORMATION, "Account Deleted", 
                         "Your account has been successfully deleted.");
                
                // Clear session and redirect to login
                UserSession.clearSession();
                redirectToLogin();
            } else {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", 
                         "Failed to delete account. Please try again or contact support.");
            }
        }
    }
}

/**
 * Complete account deletion with cascading delete for all related records
 * Based on your database schema: users, spending, income tables
 * @return true if successful, false otherwise
 */
private boolean performCompleteAccountDeletion() {
    Connection conn = null;
    
    try {
        conn = DatabaseConnection.getConnection();
        
        // Start transaction to ensure all deletions succeed or fail together
        conn.setAutoCommit(false);
        
        // Delete in reverse order of foreign key dependencies
        // (child tables first, then parent table)
        
        // 1. Delete spending records
        String deleteSpendingQuery = "DELETE FROM spending WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteSpendingQuery)) {
            pstmt.setInt(1, currentUserId);
            int spendingDeleted = pstmt.executeUpdate();
            System.out.println("Deleted " + spendingDeleted + " spending records");
        }
        
        // 2. Delete income records
        String deleteIncomeQuery = "DELETE FROM income WHERE user_id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteIncomeQuery)) {
            pstmt.setInt(1, currentUserId);
            int incomeDeleted = pstmt.executeUpdate();
            System.out.println("Deleted " + incomeDeleted + " income records");
        }
        
        // 3. Finally, delete the user record
        String deleteUserQuery = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(deleteUserQuery)) {
            pstmt.setInt(1, currentUserId);
            int userDeleted = pstmt.executeUpdate();
            
            if (userDeleted == 0) {
                throw new SQLException("Failed to delete user record - user not found");
            }
            System.out.println("User account deleted successfully");
        }
        
        // Commit the transaction
        conn.commit();
        System.out.println("Account deletion completed successfully");
        return true;
        
    } catch (SQLException e) {
        System.err.println("Error during account deletion: " + e.getMessage());
        e.printStackTrace();
        
        // Rollback transaction on error
        if (conn != null) {
            try {
                conn.rollback();
                System.out.println("Transaction rolled back due to error");
            } catch (SQLException rollbackEx) {
                System.err.println("Error rolling back transaction: " + rollbackEx.getMessage());
            }
        }
        return false;
        
    } finally {
        // Restore auto-commit and close connection
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}


}
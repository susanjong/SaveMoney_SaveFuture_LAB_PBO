package com.finance.finance_lab_pbo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class LoginPageController {
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    @FXML
    private Hyperlink forgotPasswordLink;
    
    // Current logged-in user data
    private static int currentUserId = -1;
    private static String currentUserName = "";
    private static String currentUserEmail = "";
    
    // Handle login (Continue button)
@FXML
private void handleLogin(ActionEvent event) {
    String email = emailField.getText().trim();
    String password = passwordField.getText();
    
    // Validate input
    if (!validateInput(email, password)) {
        return;
    }
    
    // Authenticate user
    User user = authenticateUser(email, password);
    if (user != null) {
        // Set current user data using UserSession instead of static variables
        UserSession.setCurrentUser(user.getId(), user.getName(), user.getEmail());
        
        // Optional: Remove or comment out the old static variable assignments
        // currentUserId = user.getId();
        // currentUserName = user.getName();
        // currentUserEmail = user.getEmail();
        
        showAlert(Alert.AlertType.INFORMATION, "Login Successful", "Welcome back, " + user.getName() + "!");
        
        // Navigate to main application/dashboard
        navigateToMainApp();
    } else {
        showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid email or password. Please try again.");
        // Clear password field for security
        passwordField.clear();
    }
}
    
    // Handle navigation to register page (Register Here button)
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Load register page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/finance_lab_pbo/Register.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) registerButton.getScene().getWindow();
            
            // Set new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Register - Finance Lab");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load register page.");
        }
    }
    
    // Handle forgot password
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            // Load change password page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/finance_lab_pbo/changepassword.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Set new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Change Password - Finance Lab");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load change password page.");
        }
    }
    
    // Field focus handlers to clear error styling
    @FXML
    private void handleEmailFieldFocus() {
        if (emailField.getStyle().contains("-fx-border-color: red")) {
            emailField.setStyle("-fx-background-radius: 40px;");
        }
    }
    
    @FXML
    private void handlePasswordFieldFocus() {
        if (passwordField.getStyle().contains("-fx-border-color: red")) {
            passwordField.setStyle("-fx-background-radius: 40px;");
        }
    }
    
    // Validate user input
    private boolean validateInput(String email, String password) {
        boolean isValid = true;
        
        // Reset field styles
        emailField.setStyle("-fx-background-radius: 40px;");
        passwordField.setStyle("-fx-background-radius: 40px;");
        
        // Validate email
        if (email.isEmpty()) {
            emailField.setStyle("-fx-background-radius: 40px; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 40px;");
            isValid = false;
        }
        
        // Validate password
        if (password.isEmpty()) {
            passwordField.setStyle("-fx-background-radius: 40px; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 40px;");
            isValid = false;
        }
        
        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please enter both email and password.");
        }
        
        return isValid;
    }
    
    // Authenticate user against database
    private User authenticateUser(String email, String password) {
        String query = "SELECT id, name, email, password FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String hashedInputPassword = hashPassword(password);
                
                // Check if password matches
                if (storedPassword.equals(hashedInputPassword)) {
                    return new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email")
                    );
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not connect to database. Please try again.");
        }
        
        return null;
    }
    
    // Hash password using SHA-256 (same method as in RegisterPageController)
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            
            return sb.toString();
            
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // Fallback to plain text (not recommended for production)
        }
    }
    
    // Navigate to main application after successful login
    private void navigateToMainApp() {
        try {
            // Load main application page (replace with your actual main page)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/finance_lab_pbo/Dashboard.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) loginButton.getScene().getWindow();
            
            // Set new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Finance Lab - Dashboard");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load main application.");
        }
    }
    
    // Show alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Static methods to get current user data (can be used by other controllers)
    public static int getCurrentUserId() {
        return currentUserId;
    }
    
    public static String getCurrentUserName() {
        return currentUserName;
    }
    
    public static String getCurrentUserEmail() {
        return currentUserEmail;
    }
    
    public static boolean isUserLoggedIn() {
        return currentUserId != -1;
    }
    
    public static void logout() {
        currentUserId = -1;
        currentUserName = "";
        currentUserEmail = "";
    }
    
    // User data class
    private static class User {
        private int id;
        private String name;
        private String email;
        
        public User(int id, String name, String email) {
            this.id = id;
            this.name = name;
            this.email = email;
        }
        
        public int getId() {
            return id;
        }
        
        public String getName() {
            return name;
        }
        
        public String getEmail() {
            return email;
        }
    }
}
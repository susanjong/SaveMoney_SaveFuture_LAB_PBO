package com.finance.finance_lab_pbo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class RegisterPageController {
    
    @FXML
    private TextField nameField;
    
    @FXML
    private TextField emailField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private PasswordField confirmPasswordField;
    
    @FXML
    private Button loginButton;
    
    @FXML
    private Button registerButton;
    
    // Handle registration (Continue button)
    @FXML
    private void handleLogin(ActionEvent event) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();
        
        // Validate input
        if (!validateInput(name, email, password, confirmPassword)) {
            return;
        }
        
        // Check if email already exists
        if (isEmailExists(email)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Email already exists. Please use a different email.");
            return;
        }
        
        // Register user
        if (registerUser(name, email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Account created successfully! You can now login.");
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Failed to create account. Please try again.");
        }
    }
    
    // Handle navigation to login page (Login Here button)
    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Load login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/finance/finance_lab_pbo/Login.fxml"));
            Parent root = loader.load();
            
            // Get current stage
            Stage stage = (Stage) registerButton.getScene().getWindow();
            
            // Set new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login - Finance Lab");
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load login page.");
        }
    }
    
    // Field focus handlers to clear prompt text styling
    @FXML
    private void handleNameFieldFocus() {
        if (nameField.getStyle().contains("-fx-border-color: red")) {
            nameField.setStyle("-fx-background-radius: 40px;");
        }
    }
    
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
    
    @FXML
    private void handleConfirmPasswordFieldFocus() {
        if (confirmPasswordField.getStyle().contains("-fx-border-color: red")) {
            confirmPasswordField.setStyle("-fx-background-radius: 40px;");
        }
    }
    
    // Validate user input
    private boolean validateInput(String name, String email, String password, String confirmPassword) {
        boolean isValid = true;
        
        // Reset field styles
        nameField.setStyle("-fx-background-radius: 40px;");
        emailField.setStyle("-fx-background-radius: 40px;");
        passwordField.setStyle("-fx-background-radius: 40px;");
        confirmPasswordField.setStyle("-fx-background-radius: 40px;");
        
        // Validate name
        if (name.isEmpty() || name.length() < 2) {
            nameField.setStyle("-fx-background-radius: 40px; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 40px;");
            isValid = false;
        }
        
        // Validate email
        if (email.isEmpty() || !email.contains("@") || !isValidEmail(email)) {
            emailField.setStyle("-fx-background-radius: 40px; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 40px;");
            isValid = false;
        }
        
        // Validate password
        if (!isValidPassword(password)) {
            passwordField.setStyle("-fx-background-radius: 40px; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 40px;");
            isValid = false;
        }
        
        // Validate confirm password
        if (!password.equals(confirmPassword)) {
            confirmPasswordField.setStyle("-fx-background-radius: 40px; -fx-border-color: red; -fx-border-width: 2px; -fx-border-radius: 40px;");
            isValid = false;
        }
        
        if (!isValid) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", 
                "Please check your input:\n" +
                "- Name must be at least 2 characters\n" +
                "- Email must be valid and contain @\n" +
                "- Password must be at least 6 characters with 1 uppercase and 1 number\n" +
                "- Passwords must match");
        }
        
        return isValid;
    }
    
    // Check if email is valid format
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
    
    // Check if password meets requirements
    private boolean isValidPassword(String password) {
        if (password.length() < 6) {
            return false;
        }
        
        boolean hasUppercase = false;
        boolean hasNumber = false;
        
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            }
            if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }
        
        return hasUppercase && hasNumber;
    }
    
    // Check if email already exists in database
    private boolean isEmailExists(String email) {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not check email availability.");
        }
        
        return false;
    }
    
    // Register user in database
    private boolean registerUser(String name, String email, String password) {
        String query = "INSERT INTO users (name, email, password, created_at) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashPassword(password));
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not create account. Please try again.");
        }
        
        return false;
    }
    
    // Hash password using SHA-256
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
    
    // Show alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    // Clear all input fields
    private void clearFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        
        // Reset field styles
        nameField.setStyle("-fx-background-radius: 40px;");
        emailField.setStyle("-fx-background-radius: 40px;");
        passwordField.setStyle("-fx-background-radius: 40px;");
        confirmPasswordField.setStyle("-fx-background-radius: 40px;");
    }
}
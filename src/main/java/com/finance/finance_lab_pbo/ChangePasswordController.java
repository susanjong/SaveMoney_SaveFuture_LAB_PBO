package com.finance.finance_lab_pbo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class ChangePasswordController {

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private Button loginButton; // Keep original button name to match FXML

    @FXML
    private void handleLogin() { // Keep original method name to match FXML
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // Validasi input kosong
        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input Error", "Please fill in all fields!");
            return;
        }

        // Validasi email
        if (!isValidEmail(email)) {
            showAlert(Alert.AlertType.ERROR, "Email Error", "Email must contain '@' symbol!");
            return;
        }

        // Validasi password
        if (!isValidPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Password Error",
                    "Password must be at least 6 characters long, contain at least 1 uppercase letter and 1 number!");
            return;
        }

        // Jika validasi berhasil, tampilkan konfirmasi dan arahkan ke login page
        showAlert(Alert.AlertType.INFORMATION, "Success", "Password change data validated successfully!");

        try {
            navigateToLogin();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Failed to load login page!");
            e.printStackTrace();
        }
    }

    /**
     * Validasi email - harus mengandung karakter '@'
     */
    private boolean isValidEmail(String email) {
        return email.contains("@");
    }

    /**
     * Validasi password dengan kriteria:
     * - Minimal 6 karakter
     * - Minimal 1 huruf kapital
     * - Minimal 1 angka
     */
    private boolean isValidPassword(String password) {
        if (password.length() < 6) {
            return false;
        }

        boolean hasUppercase = false;
        boolean hasDigit = false;

        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUppercase = true;
            }
            if (Character.isDigit(c)) {
                hasDigit = true;
            }

            // Jika sudah memenuhi kedua kriteria, tidak perlu lanjut loop
            if (hasUppercase && hasDigit) {
                break;
            }
        }

        return hasUppercase && hasDigit;
    }

    /**
     * Menampilkan alert dialog
     */
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Navigasi ke halaman login
     */
    private void navigateToLogin() throws IOException {
        // Load FXML file untuk login page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Login.fxml"));
        Parent root = loader.load();

        // Dapatkan stage saat ini
        Stage stage = (Stage) loginButton.getScene().getWindow();

        // Buat scene baru dan set ke stage
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Login");
        stage.show();
    }

    /**
     * Method untuk reset/clear form
     */
    @FXML
    private void clearForm() {
        emailField.clear();
        passwordField.clear();
    }
}
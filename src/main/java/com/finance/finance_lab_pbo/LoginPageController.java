package com.finance.finance_lab_pbo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

public class LoginPageController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    // Inisialisasi controller
    @FXML
    private void initialize() {
        // Setup placeholder text untuk text fields
        setupPlaceholders();

        // Disable login button pada awalnya
        loginButton.setDisable(false);

        // Setup listeners untuk validasi real-time
        setupValidationListeners();
    }

    private void setupPlaceholders() {
        // Set prompt text yang sudah didefinisikan di FXML
        if (emailField.getPromptText() == null || emailField.getPromptText().isEmpty()) {
            emailField.setPromptText("Enter Your Email");
        }

        if (passwordField.getPromptText() == null || passwordField.getPromptText().isEmpty()) {
            passwordField.setPromptText("Enter Your Password");
        }
    }

    private void setupValidationListeners() {
        // Listener untuk email field
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndToggleButton();
        });

        // Listener untuk password field
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndToggleButton();
        });
    }

    private void validateAndToggleButton() {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Cek apakah email dan password tidak kosong dan memenuhi kriteria
        boolean isEmailValid = isValidEmailSilent(email);
        boolean isPasswordValid = isValidPasswordSilent(password);

        // Enable/disable login button berdasarkan validasi
        loginButton.setDisable(!(isEmailValid && isPasswordValid));

        // Update style button untuk memberikan feedback visual
        if (isEmailValid && isPasswordValid) {
            loginButton.setStyle("-fx-background-color: #44ADC0; -fx-text-fill: white; -fx-background-radius: 40px;");
        } else {
            loginButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-background-radius: 40px;");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        // Validasi input terlebih dahulu
        if (!isValidInput(email, password)) {
            return; // Jika tidak valid, keluar dari method (error sudah ditampilkan di isValidInput)
        }

        // Logika autentikasi sederhana
        if (authenticateUser(email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                    "Selamat datang! Login berhasil.\nEmail: " + email);

            // Redirect ke halaman utama (contoh)
            try {
                loadMainApplication();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Gagal memuat halaman utama: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Login Gagal",
                    "Email atau password salah!\nSilakan periksa kembali kredensial Anda.\n\n" +
                            "Untuk demo, gunakan:\n" +
                            "• admin@finance.com dengan password Admin123\n" +
                            "• user@finance.com dengan password User123\n" +
                            "• test@example.com dengan password Test123");
        }
    }

    // Method untuk menampilkan informasi validasi saat user klik pada field
    @FXML
    private void handleEmailFieldFocus() {
        String currentEmail = emailField.getText();
        if (currentEmail == null || currentEmail.isEmpty() || !isValidEmailSilent(currentEmail)) {
            showValidationInfo("Email",
                    "Syarat email yang valid:\n" +
                            "• Harus mengandung simbol '@'\n" +
                            "• Format: nama@domain.com\n" +
                            "• Contoh: user@example.com");
        }
    }

    @FXML
    private void handlePasswordFieldFocus() {
        String currentPassword = passwordField.getText();
        if (currentPassword == null || currentPassword.isEmpty() || !isValidPasswordSilent(currentPassword)) {
            showValidationInfo("Password",
                    "Syarat password yang valid:\n" +
                            "• Minimal 6 karakter\n" +
                            "• Minimal 1 huruf besar (A-Z)\n" +
                            "• Minimal 1 angka (0-9)\n" +
                            "• Contoh: MyPass123, Admin2024");
        }
    }

    private void showValidationInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info Validasi - " + title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        try {
            // Load halaman register dengan auto maximized
            loadRegisterPage();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal memuat halaman register: " + e.getMessage());
        }
    }

    private boolean isValidInput(String email, String password) {
        // Cek apakah email kosong atau null
        if (email == null || email.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid",
                    "Email tidak boleh kosong!\n\nSilakan masukkan email Anda.");
            emailField.requestFocus(); // Focus ke email field
            return false;
        }

        // Cek apakah password kosong atau null
        if (password == null || password.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Input Tidak Valid",
                    "Password tidak boleh kosong!\n\nSilakan masukkan password Anda.");
            passwordField.requestFocus(); // Focus ke password field
            return false;
        }

        // Validasi format email
        if (!isValidEmail(email)) {
            emailField.requestFocus();
            return false;
        }

        // Validasi password
        if (!isValidPassword(password)) {
            passwordField.requestFocus();
            return false;
        }

        return true;
    }

    // Validasi silent (tanpa menampilkan alert) untuk real-time checking
    private boolean isValidEmailSilent(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }

        if (!email.contains("@")) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        return email.matches(emailRegex);
    }

    private boolean isValidPasswordSilent(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }

        // Cek semua kriteria password
        boolean hasMinLength = password.length() >= 6;
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");

        return hasMinLength && hasUpperCase && hasDigit;
    }

    private boolean isValidEmail(String email) {
        // Cek apakah email mengandung simbol @
        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Email Tidak Valid",
                    "Email harus mengandung simbol '@'!\n\nContoh format yang benar: user@example.com");
            return false;
        }

        // Validasi format email yang lebih detail
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.ERROR, "Format Email Salah",
                    "Format email tidak valid!\n\nPastikan email memiliki format yang benar.\nContoh: user@example.com");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        StringBuilder errorMessage = new StringBuilder("Password tidak memenuhi syarat:\n\n");
        boolean isValid = true;

        // Cek panjang password minimal 6 karakter
        if (password.length() < 6) {
            errorMessage.append("• Password harus minimal 6 karakter\n");
            isValid = false;
        }

        // Cek apakah ada huruf besar
        if (!password.matches(".*[A-Z].*")) {
            errorMessage.append("• Password harus mengandung minimal 1 huruf besar (A-Z)\n");
            isValid = false;
        }

        // Cek apakah ada angka
        if (!password.matches(".*[0-9].*")) {
            errorMessage.append("• Password harus mengandung minimal 1 angka (0-9)\n");
            isValid = false;
        }

        // Jika password tidak valid, tampilkan pesan error
        if (!isValid) {
            errorMessage.append("\nContoh password yang valid: MyPass123, Admin2024, User123ABC");
            showAlert(Alert.AlertType.ERROR, "Password Tidak Valid", errorMessage.toString());
            return false;
        }

        return true;
    }

    private boolean authenticateUser(String email, String password) {
        // Implementasi sederhana untuk demo
        // Dalam aplikasi nyata, ini harus terhubung dengan database

        // Contoh user default dengan password yang memenuhi kriteria
        return (email.equals("admin@finance.com") && password.equals("Admin123")) ||
                (email.equals("user@finance.com") && password.equals("User123")) ||
                (email.equals("test@example.com") && password.equals("Test123"));
    }

    private void loadMainApplication() throws IOException {
        // Load FXML halaman utama
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/MainApplication.fxml"));
        Parent root = loader.load();

        Stage currentStage = (Stage) loginButton.getScene().getWindow();
        Scene scene = new Scene(root);
        currentStage.setScene(scene);
        currentStage.setTitle("Finance Application - Dashboard");
        // Set maximized untuk halaman utama juga
        currentStage.setMaximized(true);
        currentStage.show();
    }

    private void loadRegisterPage() throws IOException {
        try {
            // Try to find Register FXML file in multiple locations
            URL fxmlUrl = null;

            // Try path 1: In resources root
            fxmlUrl = getClass().getResource("/Register.fxml");

            if (fxmlUrl == null) {
                // Try path 2: In same package as this class
                fxmlUrl = getClass().getResource("Register.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 3: With full package path
                fxmlUrl = getClass().getResource("/com/finance/finance_lab_pbo/Register.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 4: In fxml folder
                fxmlUrl = getClass().getResource("/fxml/Register.fxml");
            }

            if (fxmlUrl == null) {
                System.err.println("Register FXML file not found! Please place Register.fxml in one of these locations:");
                System.err.println("1. src/main/resources/Register.fxml");
                System.err.println("2. src/main/resources/com/finance/finance_lab_pbo/Register.fxml");
                System.err.println("3. src/main/resources/fxml/Register.fxml");
                System.err.println("4. In the same folder as this Java file");

                throw new IOException("Register.fxml file not found! Please check the file location.");
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get current stage and set new scene
            Stage currentStage = (Stage) registerButton.getScene().getWindow();

            // Create new scene
            Scene scene = new Scene(root);

            // Set title and scene
            currentStage.setTitle("Ayo Simpan - Register");
            currentStage.setScene(scene);
            currentStage.setMaximized(true);
            currentStage.show();

            System.out.println("Register page loaded successfully");

        } catch (IOException e) {
            System.err.println("Error loading Register page: " + e.getMessage());
            throw new IOException("Error loading Register page: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Handler untuk "Forgot Password" hyperlink
    @FXML
    private void handleForgotPassword(ActionEvent event) {
        try {
            // Load halaman change password dengan auto maximized
            loadChangePasswordPage();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal memuat halaman change password: " + e.getMessage());
        }
    }

    private void loadChangePasswordPage() throws IOException {
        try {
            // Try to find ChangePassword FXML file in multiple locations
            URL fxmlUrl = null;

            // Try path 1: In resources root
            fxmlUrl = getClass().getResource("/ChangePassword.fxml");

            if (fxmlUrl == null) {
                // Try path 2: In same package as this class
                fxmlUrl = getClass().getResource("ChangePassword.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 3: With full package path
                fxmlUrl = getClass().getResource("/com/finance/finance_lab_pbo/ChangePassword.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 4: In fxml folder
                fxmlUrl = getClass().getResource("/fxml/ChangePassword.fxml");
            }

            if (fxmlUrl == null) {
                System.err.println("ChangePassword FXML file not found! Please place ChangePassword.fxml in one of these locations:");
                System.err.println("1. src/main/resources/ChangePassword.fxml");
                System.err.println("2. src/main/resources/com/finance/finance_lab_pbo/ChangePassword.fxml");
                System.err.println("3. src/main/resources/fxml/ChangePassword.fxml");
                System.err.println("4. In the same folder as this Java file");

                throw new IOException("ChangePassword.fxml file not found! Please check the file location.");
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get current stage and set new scene
            Stage currentStage = (Stage) emailField.getScene().getWindow();

            // Create new scene
            Scene scene = new Scene(root);

            // Set title and scene
            currentStage.setTitle("Ayo Simpan - Change Password");
            currentStage.setScene(scene);

            // PENTING: Set maximized setelah scene di-set
            currentStage.setMaximized(true);

            // Ensure the stage is shown
            currentStage.show();

            System.out.println("Change Password page loaded successfully");

        } catch (IOException e) {
            System.err.println("Error loading Change Password page: " + e.getMessage());
            throw new IOException("Error loading Change Password page: " + e.getMessage());
        }
    }
}
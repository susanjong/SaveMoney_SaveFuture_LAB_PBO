package com.finance.finance_lab_pbo;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class RegisterPageController {

    @FXML
    private Button loginButton; // Button "Continue" untuk register

    @FXML
    private Button registerButton; // Button "Login Here" untuk kembali ke login

    @FXML
    private TextField nameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField passwordField;

    @FXML
    private TextField confirmPasswordField;

    // List untuk menyimpan user yang terdaftar (dalam aplikasi nyata menggunakan database)
    private static List<User> registeredUsers = new ArrayList<>();

    // Inisialisasi controller
    @FXML
    private void initialize() {
        // Setup placeholder text untuk text fields
        setupPlaceholders();

        // Disable continue button pada awalnya
        loginButton.setDisable(true);

        // Setup listeners untuk validasi real-time
        setupValidationListeners();

        // Initialize with some default users if empty
        initializeDefaultUsers();
    }

    private void initializeDefaultUsers() {
        if (registeredUsers.isEmpty()) {
            registeredUsers.add(new User("Admin", "admin@finance.com", "Admin123"));
            registeredUsers.add(new User("User", "user@finance.com", "User123"));
            registeredUsers.add(new User("Test User", "test@example.com", "Test123"));
        }
    }

    private void setupPlaceholders() {
        // Set prompt text untuk semua field
        if (nameField.getPromptText() == null || nameField.getPromptText().isEmpty()) {
            nameField.setPromptText("Enter Your Name (min 1 character)");
        }

        if (emailField.getPromptText() == null || emailField.getPromptText().isEmpty()) {
            emailField.setPromptText("Enter Your Email (must contain @)");
        }

        if (passwordField.getPromptText() == null || passwordField.getPromptText().isEmpty()) {
            passwordField.setPromptText("Password (min 6 chars, 1 uppercase, 1 number)");
        }

        if (confirmPasswordField.getPromptText() == null || confirmPasswordField.getPromptText().isEmpty()) {
            confirmPasswordField.setPromptText("Confirm Your Password");
        }
    }

    private void setupValidationListeners() {
        // Listener untuk name field
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndToggleButton();
        });

        // Listener untuk email field
        emailField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndToggleButton();
        });

        // Listener untuk password field
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndToggleButton();
        });

        // Listener untuk confirm password field
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> {
            validateAndToggleButton();
        });
    }

    private void validateAndToggleButton() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Cek apakah semua field tidak kosong dan memenuhi kriteria
        boolean isNameValid = isValidNameSilent(name);
        boolean isEmailValid = isValidEmailSilent(email);
        boolean isPasswordValid = isValidPasswordSilent(password);
        boolean isConfirmPasswordValid = password.equals(confirmPassword) && !confirmPassword.isEmpty();

        // Enable/disable continue button berdasarkan validasi
        boolean allValid = isNameValid && isEmailValid && isPasswordValid && isConfirmPasswordValid;
        loginButton.setDisable(!allValid);

        // Update style button untuk memberikan feedback visual
        if (allValid) {
            loginButton.setStyle("-fx-background-color: #44ADC0; -fx-text-fill: white; -fx-background-radius: 40px;");
        } else {
            loginButton.setStyle("-fx-background-color: #cccccc; -fx-text-fill: #666666; -fx-background-radius: 40px;");
        }
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        // Method ini sebenarnya untuk handle register (button "Continue")
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Show detailed validation requirements before checking
        if (!showValidationRequirements()) {
            return;
        }

        // Validasi input terlebih dahulu dengan alert yang detail
        if (!isValidInputWithDetailedAlert(name, email, password, confirmPassword)) {
            return; // Jika tidak valid, keluar dari method
        }

        // Cek apakah email sudah terdaftar
        if (isEmailAlreadyRegistered(email)) {
            showAlert(Alert.AlertType.ERROR, "Registration Failed",
                    "EMAIL SUDAH TERDAFTAR!\n\n" +
                            "Email: " + email + "\n\n" +
                            "Aksi yang dapat dilakukan:\n" +
                            "• Gunakan email lain untuk registrasi\n" +
                            "• Login dengan akun yang sudah ada\n" +
                            "• Reset password jika lupa");
            emailField.requestFocus();
            emailField.selectAll();
            return;
        }

        // Register user baru
        if (registerUser(name, email, password)) {
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful!",
                    "SELAMAT! Registrasi berhasil.\n\n" +
                            "Detail Akun:\n" +
                            "Nama: " + name + "\n" +
                            "Email: " + email + "\n\n" +
                            "Akun Anda telah aktif dan siap digunakan!\n" +
                            "Anda akan diarahkan ke halaman login...");

            // Clear all fields
            clearAllFields();

            // Redirect ke halaman login
            try {
                loadLoginPage();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Error",
                        "Gagal memuat halaman login: " + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Registration Failed",
                    "REGISTRASI GAGAL!\n\n" +
                            "Terjadi kesalahan sistem saat registrasi.\n\n" +
                            "Silakan coba lagi atau hubungi administrator jika masalah berlanjut.");
        }
    }

    private boolean showValidationRequirements() {
        // Show validation requirements dialog
        Alert requirementsAlert = new Alert(Alert.AlertType.INFORMATION);
        requirementsAlert.setTitle("Syarat Registrasi");
        requirementsAlert.setHeaderText("Pastikan data Anda memenuhi syarat berikut:");
        requirementsAlert.setContentText(
                "NAMA:\n" +
                        "   • Hanya huruf dan spasi\n" +
                        "   • Tidak dimulai/diakhiri spasi\n\n" +

                        "EMAIL:\n" +
                        "   • Harus mengandung simbol '@'\n" +
                        "   • Format: nama@domain.com\n" +
                        "   • Contoh: user@example.com\n\n" +

                        "PASSWORD:\n" +
                        "   • Minimal 6 karakter\n" +
                        "   • Minimal 1 huruf BESAR (A-Z)\n" +
                        "   • Minimal 1 angka (0-9)\n" +
                        "   • Contoh: MyPass123\n\n" +

                        "KONFIRMASI PASSWORD:\n" +
                        "   • Harus sama dengan password\n" +
                        "   • Case sensitive (huruf besar/kecil)\n\n" +

                        "Klik OK untuk melanjutkan validasi..."
        );
        requirementsAlert.showAndWait();
        return true;
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Method ini untuk button "Login Here" - kembali ke halaman login
        try {
            loadLoginPage();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal memuat halaman login: " + e.getMessage());
        }
    }

    private boolean isValidInputWithDetailedAlert(String name, String email, String password, String confirmPassword) {
        // Validasi nama dengan alert detail
        if (name == null || name.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Nama Kosong",
                    "NAMA TIDAK BOLEH KOSONG!\n\n" +
                            "Silakan masukkan nama Anda.\n" +
                            "Contoh nama yang valid:\n" +
                            "   • John Doe\n" +
                            "   • Maria Santos\n" +
                            "   • Ahmad Rizki");
            nameField.requestFocus();
            return false;
        }

        if (!isValidName(name)) {
            nameField.requestFocus();
            nameField.selectAll();
            return false;
        }

        // Validasi email dengan alert detail
        if (email == null || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Email Kosong",
                    "EMAIL TIDAK BOLEH KOSONG!\n\n" +
                            "Silakan masukkan email Anda.\n" +
                            "Contoh email yang valid:\n" +
                            "   • student@gmail.ac.id");
            emailField.requestFocus();
            return false;
        }

        if (!isValidEmail(email)) {
            emailField.requestFocus();
            emailField.selectAll();
            return false;
        }

        // Validasi password dengan alert detail
        if (password == null || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Password Kosong",
                    "PASSWORD TIDAK BOLEH KOSONG!\n\n" +
                            "Silakan masukkan password Anda.\n" +
                            "Contoh password yang valid:\n" +
                            "   • User123ABC");
            passwordField.requestFocus();
            return false;
        }

        if (!isValidPassword(password)) {
            passwordField.requestFocus();
            passwordField.selectAll();
            return false;
        }

        // Validasi confirm password dengan alert detail
        if (confirmPassword == null || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Konfirmasi Password Kosong",
                    "KONFIRMASI PASSWORD TIDAK BOLEH KOSONG!\n\n" +
                            "Silakan konfirmasi password Anda.\n" +
                            "Tips: Pastikan mengetik ulang password yang sama persis.");
            confirmPasswordField.requestFocus();
            return false;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Password Tidak Cocok",
                    "PASSWORD DAN KONFIRMASI TIDAK COCOK!\n\n" +
                            "Detail masalah:\n" +
                            "   • Password: " + password.length() + " karakter\n" +
                            "   • Konfirmasi: " + confirmPassword.length() + " karakter\n\n" +
                            "Tips untuk memperbaiki:\n" +
                            "   • Periksa huruf besar/kecil (case sensitive)\n" +
                            "   • Pastikan tidak ada spasi tambahan\n" +
                            "   • Ketik ulang kedua password dengan hati-hati");
            confirmPasswordField.requestFocus();
            confirmPasswordField.selectAll();
            return false;
        }

        return true;
    }

    // Validasi silent (tanpa menampilkan alert) untuk real-time checking
    private boolean isValidNameSilent(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        // Nama minimal 1 karakter dan hanya huruf dan spasi
        if (name.trim().length() < 1) {
            return false;
        }

        // Cek apakah hanya mengandung huruf dan spasi
        return name.matches("^[a-zA-Z\\s]+$") && !name.startsWith(" ") && !name.endsWith(" ");
    }

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

    private boolean isValidName(String name) {
        // Cek panjang nama minimal 1 karakter
        if (name.trim().length() < 1) {
            showAlert(Alert.AlertType.ERROR, "Nama Terlalu Pendek",
                    "NAMA HARUS MINIMAL 1 KARAKTER!\n\n" +
                            "Panjang nama saat ini: " + name.trim().length() + " karakter\n" +
                            "Minimal yang dibutuhkan: 1 karakter\n\n" +
                            "Contoh nama yang valid:\n" +
                            "   • A (1 karakter)\n" +
                            "   • Maria Sari (10 karakter)");
            return false;
        }

        // Cek apakah nama hanya mengandung huruf dan spasi
        if (!name.matches("^[a-zA-Z\\s]+$")) {
            showAlert(Alert.AlertType.ERROR, "Format Nama Salah",
                    "NAMA HANYA BOLEH HURUF DAN SPASI!\n\n" +
                            "Karakter yang tidak diizinkan:\n" +
                            "   • Angka (0-9)\n" +
                            "   • Simbol (!@#$%^&*)\n" +
                            "   • Tanda baca (.,;:)\n\n" +
                            "Contoh nama yang benar:\n" +
                            "   • John Doe\n" +
                            "   • Ahmad bin Ali");
            return false;
        }

        // Cek apakah nama tidak dimulai atau diakhiri dengan spasi
        if (name.startsWith(" ") || name.endsWith(" ")) {
            showAlert(Alert.AlertType.ERROR, "Format Nama Salah",
                    "NAMA TIDAK BOLEH DIMULAI/DIAKHIRI SPASI!\n\n" +
                            "Masalah yang ditemukan:\n" +
                            (name.startsWith(" ") ? "   • Dimulai dengan spasi\n" : "") +
                            (name.endsWith(" ") ? "   • Diakhiri dengan spasi\n" : "") +
                            "\nSolusi: Hapus spasi di awal dan akhir nama");
            return false;
        }

        return true;
    }

    private boolean isValidEmail(String email) {
        // Cek apakah email mengandung simbol @
        if (!email.contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Email Tidak Valid",
                    "EMAIL HARUS MENGANDUNG SIMBOL '@'!\n\n" +
                            "Email yang Anda masukkan: " + email + "\n\n" +
                            "Format email yang benar:\n" +
                            "   • nama@domain.com\n" +
                            "Pastikan ada simbol @ di antara nama dan domain");
            return false;
        }

        // Validasi format email yang lebih detail
        String emailRegex = "^[A-Za-z0-9+_.-]+@([A-Za-z0-9.-]+\\.[A-Za-z]{2,})$";
        if (!email.matches(emailRegex)) {
            showAlert(Alert.AlertType.ERROR, "Format Email Salah",
                    "FORMAT EMAIL TIDAK VALID!\n\n" +
                            "Email yang Anda masukkan: " + email + "\n\n" +
                            "Kemungkinan masalah:\n" +
                            "   • Tidak ada domain setelah @\n" +
                            "   • Domain tidak memiliki ekstensi (.com, .id, dll)\n" +
                            "   • Mengandung karakter yang tidak diizinkan\n\n" +
                            "Contoh format yang benar:\n" +
                            "   • user@gmail.com\n" +
                            "   • student123@university.ac.id");
            return false;
        }

        return true;
    }

    private boolean isValidPassword(String password) {
        StringBuilder errorMessage = new StringBuilder("PASSWORD TIDAK MEMENUHI SYARAT:\n\n");
        boolean isValid = true;

        // Array untuk menyimpan status validasi
        boolean hasMinLength = password.length() >= 6;
        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");

        // Tampilkan status setiap kriteria
        errorMessage.append("Status Validasi Password:\n");
        errorMessage.append(hasMinLength ? "✓" : "✗").append(" Minimal 6 karakter (Saat ini: ").append(password.length()).append(")\n");
        errorMessage.append(hasUpperCase ? "✓" : "✗").append(" Minimal 1 huruf BESAR (A-Z)\n");
        errorMessage.append(hasDigit ? "✓" : "✗").append(" Minimal 1 angka (0-9)\n\n");

        // Cek kriteria yang tidak terpenuhi
        if (!hasMinLength) {
            errorMessage.append("Password terlalu pendek: ").append(password.length()).append(" karakter\n");
            errorMessage.append("   Tambahkan ").append(6 - password.length()).append(" karakter lagi\n\n");
            isValid = false;
        }

        if (!hasUpperCase) {
            errorMessage.append("Tidak ada huruf BESAR (A-Z)\n");
            errorMessage.append("   Tambahkan minimal 1 huruf kapital\n\n");
            isValid = false;
        }

        if (!hasDigit) {
            errorMessage.append("Tidak ada angka (0-9)\n");
            errorMessage.append("   Tambahkan minimal 1 angka\n\n");
            isValid = false;
        }

        // Jika password tidak valid, tampilkan pesan error
        if (!isValid) {
            errorMessage.append("Contoh password yang valid:\n");
            errorMessage.append("   • MyPass123 (8 karakter, M+P huruf besar, 123 angka)\n");
            errorMessage.append("   • Admin2024 (9 karakter, A huruf besar, 2024 angka)\n");
            errorMessage.append("   • User123ABC (10 karakter, U+A+B+C huruf besar, 123 angka)");

            showAlert(Alert.AlertType.ERROR, "Password Tidak Valid", errorMessage.toString());
            return false;
        }

        return true;
    }

    private boolean isEmailAlreadyRegistered(String email) {
        return registeredUsers.stream().anyMatch(user -> user.getEmail().equalsIgnoreCase(email));
    }

    private boolean registerUser(String name, String email, String password) {
        try {
            User newUser = new User(name, email, password);
            registeredUsers.add(newUser);
            System.out.println("User registered successfully: " + name + " (" + email + ")");
            System.out.println("Total registered users: " + registeredUsers.size());
            return true;
        } catch (Exception e) {
            System.err.println("Error registering user: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public static List<User> getRegisteredUsers() {
        return new ArrayList<>(registeredUsers);
    }

    private void clearAllFields() {
        nameField.clear();
        emailField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
    }

    private void loadLoginPage() throws IOException {
        try {
            // Try to find Login FXML file in multiple locations
            URL fxmlUrl = null;

            // Try path 1: In resources root
            fxmlUrl = getClass().getResource("/Login.fxml");

            if (fxmlUrl == null) {
                // Try path 2: In same package as this class
                fxmlUrl = getClass().getResource("Login.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 3: With full package path
                fxmlUrl = getClass().getResource("/com/finance/finance_lab_pbo/Login.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 4: In fxml folder
                fxmlUrl = getClass().getResource("/fxml/Login.fxml");
            }

            if (fxmlUrl == null) {
                System.err.println("Login FXML file not found! Please place Login.fxml in one of these locations:");
                System.err.println("1. src/main/resources/Login.fxml");
                System.err.println("2. src/main/resources/com/finance/finance_lab_pbo/Login.fxml");
                System.err.println("3. src/main/resources/fxml/Login.fxml");
                System.err.println("4. In the same folder as this Java file");

                throw new IOException("Login.fxml file not found! Please check the file location.");
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Get current stage
            Stage currentStage = (Stage) registerButton.getScene().getWindow();

            // Create new scene
            Scene scene = new Scene(root);

            // IMPROVED: Set properties to ensure maximized window
            currentStage.setTitle("Ayo Simpan - Login");

            // First set the scene
            currentStage.setScene(scene);

            // Then maximize the window - using Platform.runLater to ensure proper timing
            Platform.runLater(() -> {
                // Set maximized state
                currentStage.setMaximized(true);

                // Additional properties to ensure full window
                currentStage.setResizable(true);

                // Center on screen if not maximized properly
                currentStage.centerOnScreen();

                // Ensure window is focused and brought to front
                currentStage.toFront();
                currentStage.requestFocus();
            });

            // Show the stage
            currentStage.show();

            System.out.println("Login page loaded successfully with maximized window");

        } catch (IOException e) {
            System.err.println("Error loading Login page: " + e.getMessage());
            throw new IOException("Error loading Login page: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        // Set minimum width untuk alert agar text tidak terpotong
        alert.getDialogPane().setMinWidth(500);

        alert.showAndWait();
    }

    // Method untuk menampilkan informasi validasi saat user klik pada field (OPTIONAL)
    @FXML
    private void handleNameFieldFocus() {
        String currentName = nameField.getText();
        if (currentName == null || currentName.isEmpty() || !isValidNameSilent(currentName)) {
            showValidationInfo("Validasi Nama",
                    "Syarat nama yang valid:\n\n" +
                            "• Hanya boleh huruf dan spasi\n" +
                            "• Tidak boleh dimulai/diakhiri spasi\n\n");
        }
    }

    @FXML
    private void handleEmailFieldFocus() {
        String currentEmail = emailField.getText();
        if (currentEmail == null || currentEmail.isEmpty() || !isValidEmailSilent(currentEmail)) {
            showValidationInfo("Validasi Email",
                    "Syarat email yang valid:\n\n" +
                            "• Harus mengandung simbol '@'\n" +
                            "• Format: nama@domain.com\n" +
                            "• Domain harus memiliki ekstensi\n\n" +
                            "Contoh email yang benar:\n" +
                            "   • user@gmail.com\n" +
                            "   • nama@company.co.id\n" +
                            "   • student@university.ac.id");
        }
    }

    @FXML
    private void handlePasswordFieldFocus() {
        String currentPassword = passwordField.getText();
        if (currentPassword == null || currentPassword.isEmpty() || !isValidPasswordSilent(currentPassword)) {
            showValidationInfo("Validasi Password",
                    "Syarat password yang valid:\n\n" +
                            "• Minimal 6 karakter\n" +
                            "• Minimal 1 huruf BESAR (A-Z)\n" +
                            "• Minimal 1 angka (0-9)\n\n" +
                            "Contoh password yang benar:\n" +
                            "   • MyPass123\n" +
                            "   • Admin2024\n" +
                            "   • User123ABC");
        }
    }

    @FXML
    private void handleConfirmPasswordFieldFocus() {
        String currentConfirmPassword = confirmPasswordField.getText();
        String currentPassword = passwordField.getText();
        if (currentConfirmPassword == null || currentConfirmPassword.isEmpty() ||
                !currentConfirmPassword.equals(currentPassword)) {
            showValidationInfo("Konfirmasi Password",
                    "Syarat konfirmasi password:\n\n" +
                            "• Harus sama dengan password di atas\n" +
                            "• Case sensitive (huruf besar/kecil)\n" +
                            "• Tidak boleh ada spasi tambahan\n\n" +
                            "Tips:\n" +
                            "   • Ketik ulang password dengan hati-hati\n" +
                            "   • Pastikan tidak ada typo\n" +
                            "   • Perhatikan huruf besar dan kecil");
        }
    }

    private void showValidationInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().setMinWidth(450);
        alert.showAndWait();
    }

    // Inner class untuk menyimpan data user
    public static class User {
        private String name;
        private String email;
        private String password;

        public User(String name, String email, String password) {
            this.name = name;
            this.email = email;
            this.password = password;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPassword() {
            return password;
        }

        @Override
        public String toString() {
            return "User{name='" + name + "', email='" + email + "'}";
        }
    }
}
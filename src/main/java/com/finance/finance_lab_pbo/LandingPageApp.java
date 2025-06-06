package com.finance.finance_lab_pbo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class LandingPageApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Try to find FXML file in multiple locations
            URL fxmlUrl = null;

            // Try path 1: In resources root
            fxmlUrl = getClass().getResource("/LandingPage.fxml");

            if (fxmlUrl == null) {
                // Try path 2: In same package as this class
                fxmlUrl = getClass().getResource("LandingPage.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 3: With full package path
                fxmlUrl = getClass().getResource("/com/finance/finance_lab_pbo/LandingPage.fxml");
            }

            if (fxmlUrl == null) {
                System.err.println("FXML file not found! Please place LandingPage.fxml in one of these locations:");
                System.err.println("1. src/main/resources/LandingPage.fxml");
                System.err.println("2. src/main/resources/com/finance/finance_lab_pbo/LandingPage.fxml");
                System.err.println("3. In the same folder as this Java file");

                // Create a simple scene without FXML as fallback
                createFallbackScene(primaryStage);
                return;
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Create scene and set up stage
            // Create scene and set up stage
            Scene scene = new Scene(root);
            primaryStage.setTitle("Ayo Simpan - Save Money App");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();


        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();

            // Create fallback scene
            createFallbackScene(primaryStage);
        }
    }

    private void createFallbackScene(Stage primaryStage) {
        try {
            // Create a simple scene with basic components (without FXML)
            javafx.scene.layout.VBox root = new javafx.scene.layout.VBox(20);
            root.setAlignment(javafx.geometry.Pos.CENTER);
            root.setStyle("-fx-background-color: #9BD6E1; -fx-padding: 50;");

            javafx.scene.control.Label titleLabel = new javafx.scene.control.Label("Ayo Simpan");
            titleLabel.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

            javafx.scene.control.Label subtitleLabel = new javafx.scene.control.Label("Save Money App");
            subtitleLabel.setStyle("-fx-font-size: 20px;");

            javafx.scene.control.Button loginBtn = new javafx.scene.control.Button("Login");
            loginBtn.setStyle("-fx-background-color: #44ADC0; -fx-text-fill: white; -fx-font-size: 16px; -fx-pref-width: 120;");
            loginBtn.setOnAction(e -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Login");
                alert.setHeaderText(null);
                alert.setContentText("Login button clicked!");
                alert.showAndWait();
            });

            javafx.scene.control.Button registerBtn = new javafx.scene.control.Button("Register");
            registerBtn.setStyle("-fx-background-color: #B2DEE5; -fx-font-size: 16px; -fx-pref-width: 120;");
            registerBtn.setOnAction(e -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
                alert.setTitle("Register");
                alert.setHeaderText(null);
                alert.setContentText("Register button clicked!");
                alert.showAndWait();
            });

            root.getChildren().addAll(titleLabel, subtitleLabel, loginBtn, registerBtn);

            Scene scene = new Scene(root, 400, 300);
            primaryStage.setTitle("Ayo Simpan - Save Money App (Fallback)");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();


        } catch (Exception fallbackError) {
            fallbackError.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
package com.finance.finance_lab_pbo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;

public class LoginPageApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Try to find FXML file in multiple locations
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
                System.err.println("FXML file not found! Please place Login.fxml in one of these locations:");
                System.err.println("1. src/main/resources/Login.fxml");
                System.err.println("2. src/main/resources/com/finance/finance_lab_pbo/Login.fxml");
                System.err.println("3. In the same folder as this Java file");
                return;
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Create scene and set up stage
            Scene scene = new Scene(root);
            primaryStage.setTitle("Ayo Simpan - Save Money App");
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();

        } catch (Exception e) {
            System.err.println("Error loading FXML: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
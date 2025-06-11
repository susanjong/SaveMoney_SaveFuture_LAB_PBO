
package com.finance.finance_lab_pbo;

import java.net.URL;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ProfileApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        try {
            // Try to find FXML file in multiple locations
            URL fxmlUrl = null;

            // Try path 1: In resources root
            fxmlUrl = getClass().getResource("/profile.fxml");

            if (fxmlUrl == null) {
                // Try path 2: In same package as this class
                fxmlUrl = getClass().getResource("profile.fxml");
            }

            if (fxmlUrl == null) {
                // Try path 3: With full package path
                fxmlUrl = getClass().getResource("/com/finance/finance_lab_pbo/profile.fxml");
            }

            // Load FXML
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            // Create scene and set up stage
            Scene scene = new Scene(root);
            primaryStage.setTitle("Ayo Simpan - Profile Page");
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
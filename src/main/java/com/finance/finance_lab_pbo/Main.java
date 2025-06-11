package com.finance.finance_lab_pbo;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {
    private static Stage primaryStage;

    public static void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the requested screen: " + e.getMessage());
        }
    }

    private static void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void start(Stage stage) throws Exception {
        primaryStage = stage;

        showRegisterScreen();
    }
    
    // Method to get the primary stage
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
    
    // Method to show landing page
    public static void showLandingPage() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/finance/finance_login/LandingPage.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);

            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            primaryStage.setTitle("Personal Finance Tracker - Welcome");
            primaryStage.setScene(scene);
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
        
            // Set minimum window size
            primaryStage.setMinWidth(800);  
            primaryStage.setMinHeight(600);
            
            primaryStage.show();
            
        } catch (IOException e) {
            System.err.println("Error loading LandingPage.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the landing page: " + e.getMessage());
        }
    }
    
    // Method to show login screen - FIXED PATH
    public static void showLoginScreen() {
        // Try multiple paths for Login.fxml
        FXMLLoader loader = null;
        
        // Try path 1: root resources
        try {
            loader = new FXMLLoader(Main.class.getResource("/Login.fxml"));
            loader.load();
        } catch (Exception e1) {
            // Try path 2: same package
            try {
                loader = new FXMLLoader(Main.class.getResource("Login.fxml"));
                loader.load();
            } catch (Exception e2) {
                // Try path 3: with package path
                try {
                    loader = new FXMLLoader(Main.class.getResource("/com/finance/finance_lab_pbo/Login.fxml"));
                    loader.load();
                } catch (Exception e3) {
                    // Create a simple login if FXML not found
                    System.err.println("Login.fxml not found. Creating simple login.");
                    return;
                }
            }
        }
        
        Parent root = (Parent) loader.getRoot();
        Scene scene = new Scene(root);

        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setTitle("Personal Finance Tracker - Login");
        primaryStage.setScene(scene);
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
      
        primaryStage.setMinWidth(800);  
        primaryStage.setMinHeight(600);
        
        primaryStage.show();
    }
    
    // Method to show register screen
    public static void showRegisterScreen() {
        // Try multiple paths for Register.fxml
        FXMLLoader loader = null;
        
        try {
            loader = new FXMLLoader(Main.class.getResource("/Register.fxml"));
            loader.load();
        } catch (Exception e1) {
            try {
                loader = new FXMLLoader(Main.class.getResource("Register.fxml"));
                loader.load();
            } catch (Exception e2) {
                try {
                    loader = new FXMLLoader(Main.class.getResource("/com/finance/finance_lab_pbo/Register.fxml"));
                    loader.load();
                } catch (Exception e3) {
                    System.err.println("Register.fxml not found.");
                    return;
                }
            }
        }
        
        Parent root = (Parent) loader.getRoot();
        Scene scene = new Scene(root);
        
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setTitle("Personal Finance Tracker - Register");
        primaryStage.setScene(scene);
        primaryStage.setX(screenBounds.getMinX());
        primaryStage.setY(screenBounds.getMinY());
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());
      
        primaryStage.setMinWidth(800);  
        primaryStage.setMinHeight(600);
        
        primaryStage.show();
    }
    
    // Method to show dashboard
    public static void showDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Dashboard.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            primaryStage.setTitle("Personal Finance Tracker - Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
        
            primaryStage.setMinWidth(800);  
            primaryStage.setMinHeight(600);
            
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the dashboard: " + e.getMessage());
        }
    }

    // Method to show change password screen
    public static void showChangePassword() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/finance/finance_login/changepassword.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

            primaryStage.setTitle("Personal Finance Tracker - Change Password");
            primaryStage.setScene(scene);
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
        
            primaryStage.setMinWidth(800);  
            primaryStage.setMinHeight(600);
            
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading changepassword.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the change password screen: " + e.getMessage());
        }
    }

    // Method to show change password with username parameter
    public static void showChangePassword(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/finance/finance_login/changepassword.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            primaryStage.setTitle("Personal Finance Tracker - Change Password");
            primaryStage.setScene(scene);
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
        
            primaryStage.setMinWidth(800);  
            primaryStage.setMinHeight(600);
            
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading changepassword.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the change password screen: " + e.getMessage());
        }
    }

    // Method to show dashboard with user data
    public static void showDashboard(String username) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/resources/com/finance/finance_lab_pbo/Dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            
            primaryStage.setTitle("Personal Finance Tracker - Dashboard");
            primaryStage.setScene(scene);
            primaryStage.setX(screenBounds.getMinX());
            primaryStage.setY(screenBounds.getMinY());
            primaryStage.setWidth(screenBounds.getWidth());
            primaryStage.setHeight(screenBounds.getHeight());
        
            primaryStage.setMinWidth(800);  
            primaryStage.setMinHeight(600);
            
            primaryStage.show();
        } catch (IOException e) {
            System.err.println("Error loading Dashboard.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the dashboard: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
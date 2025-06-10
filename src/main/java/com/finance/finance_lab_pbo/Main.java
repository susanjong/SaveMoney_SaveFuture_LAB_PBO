package com.finance.finance_lab_pbo;

import javafx.application.Application;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import java.io.IOException;

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

        // Start with the dashboard
        showDashboard();
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
    
    // Method to show login screen
    public static void showLoginScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/finance/finance_login/Login.fxml"));
            Parent root = loader.load();
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
            
        } catch (IOException e) {
            System.err.println("Error loading Login.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the login screen: " + e.getMessage());
        }
    }
    
    // Method to show register screen
    public static void showRegisterScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("/com/finance/finance_login/Register.fxml"));
            Parent root = loader.load();
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
        } catch (IOException e) {
            System.err.println("Error loading Register.fxml: " + e.getMessage());
            e.printStackTrace();
            showAlert("Loading Error", "Could not load the register screen: " + e.getMessage());
        }
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
            
            // Get the controller and pass the username if controller has setUsername method
            // Note: Uncomment and modify this section if your ChangePasswordController has setUsername method
            /*
            ChangePasswordController controller = loader.getController();
            if (controller != null) {
                controller.setUsername(username);
            }
            */
            
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
            
            // Get the controller and pass the username if controller has setUsername method
            // Note: Uncomment and modify this section if your DashboardController has setUsername method
            /*
            DashboardController controller = loader.getController();
            if (controller != null) {
                controller.setUsername(username);
            }
            */
            
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
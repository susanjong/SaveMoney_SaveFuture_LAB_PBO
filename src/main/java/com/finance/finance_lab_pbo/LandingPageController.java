package com.finance.finance_lab_pbo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public class LandingPageController {

    @FXML
    private void handleLogin(ActionEvent event) {
        // Add your login logic here
        showAlert("Login", "Login button clicked!");
        System.out.println("Login button pressed");
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        // Add your register logic here
        showAlert("Register", "Register button clicked!");
        System.out.println("Register button pressed");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
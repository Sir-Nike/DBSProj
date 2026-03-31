package com.collegequiz.controller;

import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AdminLoginController extends BaseController {
    @FXML private TextField adminUsernameField;
    @FXML private PasswordField adminPasswordField;

    @FXML
    private void handleLogin() {
        String username = adminUsernameField.getText().trim();
        String password = adminPasswordField.getText();

        if (username.isBlank() || password.isBlank()) {
            showError("Admin Login", "Enter both username and password.");
            return;
        }

        if (!"admin".equals(username) || !"admin123".equals(password)) {
            showError("Admin Login Failed", "Invalid admin credentials.");
            return;
        }

        AppSession.setAdminSession(username);
        AppNavigator.showAdminDashboard();
    }

    @FXML
    private void handleBack() {
        AppNavigator.showLogin();
    }
}

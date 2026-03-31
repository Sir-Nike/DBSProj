package com.collegequiz.controller;

import com.collegequiz.model.Student;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class StudentLoginController extends BaseController {
    @FXML private TextField studentRegNoField;
    @FXML private PasswordField studentPasswordField;

    @FXML
    private void handleLogin() {
        String regNo = studentRegNoField.getText().trim();
        String password = studentPasswordField.getText();

        if (!regNo.matches("\\d{9}")) {
            showError("Student Login", "Registration number must contain exactly 9 digits.");
            return;
        }

        if (password.isBlank()) {
            showError("Student Login", "Enter the student password.");
            return;
        }

        Student student = service.authenticateStudent(regNo, password);
        if (student == null) {
            showError("Student Login Failed", "Invalid registration number or password.");
            return;
        }

        AppSession.setStudentSession(student);
        AppNavigator.showStudentDashboard();
    }

    @FXML
    private void handleBack() {
        AppNavigator.showLogin();
    }
}

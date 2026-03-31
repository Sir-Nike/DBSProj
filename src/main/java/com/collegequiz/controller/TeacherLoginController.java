package com.collegequiz.controller;

import com.collegequiz.model.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class TeacherLoginController extends BaseController {
    @FXML private TextField teacherCodeField;
    @FXML private PasswordField teacherPasswordField;

    @FXML
    private void handleLogin() {
        String teacherCode = teacherCodeField.getText().trim();
        String password = teacherPasswordField.getText();

        if (teacherCode.isBlank() || password.isBlank()) {
            showError("Teacher Login", "Enter both teacher code and password.");
            return;
        }

        Teacher teacher = service.authenticateTeacher(teacherCode, password);
        if (teacher == null) {
            showError("Teacher Login Failed", "Invalid teacher code or password.");
            return;
        }

        AppSession.setTeacherSession(teacher);
        AppNavigator.showTeacherDashboard();
    }

    @FXML
    private void handleBack() {
        AppNavigator.showLogin();
    }
}

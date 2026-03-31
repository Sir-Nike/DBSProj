package com.collegequiz.controller;

import com.collegequiz.service.QuizManagementService;
import com.collegequiz.service.impl.QuizManagementServiceImpl;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;

public abstract class BaseController {
    protected final QuizManagementService service;

    protected BaseController() {
        this(new QuizManagementServiceImpl());
    }

    protected BaseController(QuizManagementService service) {
        this.service = service;
    }

    protected void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    protected void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    public void handleToggleTheme() {
        ThemeManager.toggle();
        AppNavigator.applyTheme();
    }
}

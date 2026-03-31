package com.collegequiz.controller;

import javafx.fxml.FXML;

public class AuthChoiceController extends BaseController {
    @FXML
    private void handleTeacherChoice() {
        AppNavigator.showTeacherLogin();
    }

    @FXML
    private void handleStudentChoice() {
        AppNavigator.showStudentLogin();
    }
}

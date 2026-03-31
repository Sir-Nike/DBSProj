package com.collegequiz.controller;

import com.collegequiz.model.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TeacherHomeController extends BaseController {
    @FXML
    private Label teacherLabel;
    @FXML
    private Label teacherMetaLabel;

    @FXML
    private void initialize() {
        Teacher teacher = AppSession.getLoggedInTeacher();
        if (teacher == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        teacherLabel.setText(teacher.name());
        teacherMetaLabel.setText(teacher.teacherCode() + " · Department " + teacher.departmentId());
    }

    @FXML
    private void handleCreateSubject() {
        AppNavigator.showTeacherCreateSubject();
    }

    @FXML
    private void handleCreateQuiz() {
        AppNavigator.showTeacherCreateQuiz();
    }

    @FXML
    private void handleAddQuestion() {
        AppNavigator.showTeacherAddQuestion();
    }

    @FXML
    private void handleReviewResults() {
        AppNavigator.showTeacherReview();
    }

    @FXML
    private void handleLogout() {
        AppSession.clear();
        QuizRuntimeContext.clear();
        AppNavigator.showLogin();
    }
}

package com.collegequiz.controller;

import com.collegequiz.model.Teacher;
import com.collegequiz.model.Department;
import com.collegequiz.model.TeacherDashboardRow;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TeacherHomeController extends BaseController {
    @FXML
    private Label teacherLabel;
    @FXML
    private Label teacherMetaLabel;
    @FXML
    private Label subjectCountLabel;
    @FXML
    private Label quizCountLabel;
    @FXML
    private Label attemptCountLabel;

    @FXML
    private void initialize() {
        Teacher teacher = AppSession.getLoggedInTeacher();
        if (teacher == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        teacherLabel.setText(teacher.name());
        Department department = null;
        try {
            department = service.getDepartments().stream()
                    .filter(item -> item.departmentId().equals(teacher.departmentId()))
                    .findFirst()
                    .orElse(null);
        } catch (RuntimeException ex) {
            department = null;
        }
        String departmentLabelText = department == null
                ? "Department #" + teacher.departmentId()
                : department.departmentName();
        teacherMetaLabel.setText(teacher.teacherCode() + " · " + departmentLabelText);

        try {
            List<TeacherDashboardRow> rows = service.getTeacherDashboard(teacher.teacherId());
            Set<Integer> subjectIds = new HashSet<>();
            int attempts = 0;
            for (TeacherDashboardRow row : rows) {
                subjectIds.add(row.subjectId());
                attempts += row.attemptCount() == null ? 0 : row.attemptCount();
            }
            subjectCountLabel.setText(String.valueOf(subjectIds.size()));
            quizCountLabel.setText(String.valueOf(rows.size()));
            attemptCountLabel.setText(String.valueOf(attempts));
        } catch (RuntimeException ex) {
            subjectCountLabel.setText("0");
            quizCountLabel.setText("0");
            attemptCountLabel.setText("0");
        }
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

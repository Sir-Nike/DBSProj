package com.collegequiz.controller;

import com.collegequiz.model.Department;
import com.collegequiz.model.Teacher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TeacherCreateSubjectController extends BaseController {
    @FXML private Label teacherLabel;
    @FXML private Label departmentLabel;
    @FXML private TextField subjectNameField;
    @FXML private TextField subjectCodeField;
    @FXML private TextField semesterField;

    @FXML
    private void initialize() {
        if (AppSession.getLoggedInTeacher() == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        Teacher teacher = AppSession.getLoggedInTeacher();
        teacherLabel.setText(teacher.teacherCode() + " · " + teacher.name());
        Department department = service.getDepartments().stream()
                .filter(item -> item.departmentId().equals(teacher.departmentId()))
                .findFirst()
                .orElse(null);
        departmentLabel.setText(department == null ? "Department #" + teacher.departmentId() : department.departmentName());
    }

    @FXML
    private void handleSave() {
        Teacher teacher = AppSession.getLoggedInTeacher();
        if (teacher == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        String subjectName = subjectNameField.getText().trim();
        String subjectCode = subjectCodeField.getText().trim();
        String semesterText = semesterField.getText().trim();

        if (subjectName.isBlank() || subjectCode.isBlank() || semesterText.isBlank()) {
            showError("Missing Details", "Subject name, code, and semester are required.");
            return;
        }

        try {
            int semester = Integer.parseInt(semesterText);
            if (semester < 1 || semester > 8) {
                showError("Invalid Semester", "Semester must be between 1 and 8.");
                return;
            }

            service.createSubject(
                    subjectName,
                    subjectCode,
                    semester,
                    teacher.departmentId());
            showInfo("Subject Created", "Subject created successfully.");
            subjectNameField.clear();
            subjectCodeField.clear();
            semesterField.clear();
        } catch (NumberFormatException ex) {
            showError("Invalid Semester", "Semester must be numeric.");
        }
    }

    @FXML
    private void handleBack() {
        AppNavigator.showTeacherDashboard();
    }
}

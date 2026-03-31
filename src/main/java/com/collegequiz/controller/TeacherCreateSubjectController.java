package com.collegequiz.controller;

import com.collegequiz.model.Department;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class TeacherCreateSubjectController extends BaseController {
    @FXML private Label teacherLabel;
    @FXML private ComboBox<Department> departmentCombo;
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

        teacherLabel.setText(AppSession.getLoggedInTeacher().teacherCode());
        departmentCombo.setItems(FXCollections.observableArrayList(service.getDepartments()));
    }

    @FXML
    private void handleSave() {
        Department department = departmentCombo.getValue();
        if (department == null) {
            showError("Missing Department", "Choose a department.");
            return;
        }

        try {
            Integer subjectId = service.createSubject(
                    subjectNameField.getText().trim(),
                    subjectCodeField.getText().trim(),
                    Integer.parseInt(semesterField.getText().trim()),
                    department.departmentId());
            showInfo("Subject Created", "Subject ID " + subjectId + " created.");
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

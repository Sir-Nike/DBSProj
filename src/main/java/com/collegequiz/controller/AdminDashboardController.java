package com.collegequiz.controller;

import com.collegequiz.model.Department;
import com.collegequiz.model.Student;
import com.collegequiz.model.Teacher;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;
import java.util.Optional;

public class AdminDashboardController extends BaseController {
    @FXML private Label adminLabel;
    @FXML private Label teacherCountLabel;
    @FXML private Label studentCountLabel;
    @FXML private Label departmentCountLabel;

    @FXML private TextField departmentCodeField;
    @FXML private TextField departmentNameField;
    @FXML private ComboBox<Department> teacherDepartmentCombo;
    @FXML private TextField teacherNameField;
    @FXML private TextField teacherPasswordField;
    @FXML private TableView<Teacher> teacherTable;
    @FXML private TableColumn<Teacher, String> teacherCodeColumn;
    @FXML private TableColumn<Teacher, String> teacherNameColumn;
    @FXML private TableColumn<Teacher, String> teacherDepartmentColumn;

    @FXML private ComboBox<Department> studentDepartmentCombo;
    @FXML private TextField studentRegNoField;
    @FXML private TextField studentNameField;
    @FXML private TextField studentPasswordField;
    @FXML private TableView<Student> studentTable;
    @FXML private TableColumn<Student, String> studentRegColumn;
    @FXML private TableColumn<Student, String> studentNameColumn;
    @FXML private TableColumn<Student, String> studentDepartmentColumn;

    private List<Department> departments;

    @FXML
    private void initialize() {
        if (!AppSession.isAdmin()) {
            showError("Session Missing", "No admin is logged in.");
            AppNavigator.showLogin();
            return;
        }

        adminLabel.setText(AppSession.getLoggedInAdminUsername());
        teacherCodeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().teacherCode()));
        teacherNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        teacherDepartmentColumn.setCellValueFactory(data -> new SimpleStringProperty(departmentName(data.getValue().departmentId())));
        studentRegColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().registrationNo()));
        studentNameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().name()));
        studentDepartmentColumn.setCellValueFactory(data -> new SimpleStringProperty(departmentName(data.getValue().departmentId())));
        teacherTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        teacherTable.setFixedCellSize(34);
        studentTable.setFixedCellSize(34);
        teacherTable.setPlaceholder(new Label("No teachers added yet."));
        studentTable.setPlaceholder(new Label("No students added yet."));
        teacherDepartmentCombo.setPromptText("Select department");
        studentDepartmentCombo.setPromptText("Select department");
        refreshData();
    }

    @FXML
    private void handleRefresh() {
        refreshData();
    }

    @FXML
    private void handleAddDepartment() {
        String code = departmentCodeField.getText().trim();
        String name = departmentNameField.getText().trim();

        if (code.isBlank() || name.isBlank()) {
            showError("Missing Details", "Department code and name are required.");
            return;
        }

        try {
            service.createDepartment(code, name);
            departmentCodeField.clear();
            departmentNameField.clear();
            refreshData();
            showInfo("Department Added", "Department created successfully.");
        } catch (RuntimeException ex) {
            showError("Create Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleAddTeacher() {
        String name = teacherNameField.getText().trim();
        String password = teacherPasswordField.getText();
        Department department = teacherDepartmentCombo.getValue();

        if (name.isBlank() || password.isBlank() || department == null) {
            showError("Missing Details", "Teacher name, password, and department are required.");
            return;
        }

        try {
            service.createTeacher(name, password, department.departmentId());
            teacherNameField.clear();
            teacherPasswordField.clear();
            teacherDepartmentCombo.setValue(null);
            refreshData();
            showInfo("Teacher Added", "Teacher account created successfully. The teacher code was generated automatically.");
        } catch (RuntimeException ex) {
            showError("Create Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleAddStudent() {
        String regNo = studentRegNoField.getText().trim();
        String name = studentNameField.getText().trim();
        String password = studentPasswordField.getText();
        Department department = studentDepartmentCombo.getValue();

        if (!regNo.matches("\\d{9}")) {
            showError("Invalid Registration", "Registration number must contain exactly 9 digits.");
            return;
        }
        if (name.isBlank() || password.isBlank() || department == null) {
            showError("Missing Details", "Student name, password, and department are required.");
            return;
        }

        try {
            service.createStudent(regNo, name, password, department.departmentId());
            studentRegNoField.clear();
            studentNameField.clear();
            studentPasswordField.clear();
            studentDepartmentCombo.setValue(null);
            refreshData();
            showInfo("Student Added", "Student account created successfully.");
        } catch (RuntimeException ex) {
            showError("Create Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleDeleteTeacher() {
        Teacher selected = teacherTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select Teacher", "Choose a teacher first.");
            return;
        }
        if (!confirm("Delete Teacher", "Remove this teacher and their quizzes?")) {
            return;
        }
        try {
            service.removeTeacher(selected.teacherId());
            refreshData();
            showInfo("Teacher Removed", "The teacher and their quiz data were removed.");
        } catch (RuntimeException ex) {
            showError("Delete Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Student selected = studentTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select Student", "Choose a student first.");
            return;
        }
        if (!confirm("Delete Student", "Remove this student and their attempts?")) {
            return;
        }
        try {
            service.removeStudent(selected.studentId());
            refreshData();
            showInfo("Student Removed", "The student was removed.");
        } catch (RuntimeException ex) {
            showError("Delete Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleDeleteAllData() {
        if (!confirm("Delete All Data", "This will delete teachers, students, subjects, quizzes, attempts, logs, and departments. You will need to rerun the seed script to use the app again.")) {
            return;
        }
        try {
            service.clearAllOperationalData();
            refreshData();
            showInfo("Data Cleared", "Operational data has been deleted.");
        } catch (RuntimeException ex) {
            showError("Delete Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        AppSession.clear();
        QuizRuntimeContext.clear();
        AppNavigator.showLogin();
    }

    private void refreshData() {
        try {
            departments = service.getDepartments();
            teacherDepartmentCombo.setItems(FXCollections.observableArrayList(departments));
            studentDepartmentCombo.setItems(FXCollections.observableArrayList(departments));

            List<Teacher> teachers = service.getAllTeachers();
            List<Student> students = service.getAllStudents();
            teacherTable.setItems(FXCollections.observableArrayList(teachers));
            studentTable.setItems(FXCollections.observableArrayList(students));

            teacherCountLabel.setText(String.valueOf(teachers.size()));
            studentCountLabel.setText(String.valueOf(students.size()));
            departmentCountLabel.setText(String.valueOf(departments.size()));
        } catch (RuntimeException ex) {
            departments = List.of();
            teacherDepartmentCombo.setItems(FXCollections.emptyObservableList());
            studentDepartmentCombo.setItems(FXCollections.emptyObservableList());
            teacherTable.setItems(FXCollections.emptyObservableList());
            studentTable.setItems(FXCollections.emptyObservableList());
            teacherCountLabel.setText("0");
            studentCountLabel.setText("0");
            departmentCountLabel.setText("0");
            showError("Load Failed", ex.getMessage());
        }
    }

    private String departmentName(Integer departmentId) {
        if (departmentId == null || departments == null) {
            return "Department #" + departmentId;
        }
        return departments.stream()
                .filter(item -> item.departmentId().equals(departmentId))
                .findFirst()
                .map(Department::departmentName)
                .orElse("Department #" + departmentId);
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> choice = alert.showAndWait();
        return choice.isPresent() && choice.get() == ButtonType.OK;
    }
}

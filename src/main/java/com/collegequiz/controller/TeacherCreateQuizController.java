package com.collegequiz.controller;

import com.collegequiz.model.Department;
import com.collegequiz.model.Subject;
import com.collegequiz.model.Teacher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TeacherCreateQuizController extends BaseController {
    @FXML private Label teacherLabel;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private TextField quizTitleField;
    @FXML private TextField durationField;
    @FXML private DatePicker quizDatePicker;

    @FXML
    private void initialize() {
        Teacher teacher = AppSession.getLoggedInTeacher();
        if (teacher == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        teacherLabel.setText(teacher.teacherCode());
        subjectCombo.setItems(FXCollections.observableArrayList(loadTeacherSubjects()));
    }

    @FXML
    private void handleSave() {
        Subject subject = subjectCombo.getValue();
        if (subject == null) {
            showError("Missing Subject", "Choose a subject.");
            return;
        }

        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            LocalDate date = quizDatePicker.getValue() == null ? LocalDate.now() : quizDatePicker.getValue();
            LocalDateTime quizDate = date.atTime(LocalTime.of(10, 0));
            Integer quizId = service.createQuiz(
                    quizTitleField.getText().trim(),
                    duration,
                    quizDate,
                    subject.subjectId(),
                    AppSession.getLoggedInTeacherId());
            showInfo("Quiz Created", "Quiz ID " + quizId + " created.");
            quizTitleField.clear();
            durationField.clear();
            quizDatePicker.setValue(null);
        } catch (NumberFormatException ex) {
            showError("Invalid Duration", "Duration must be numeric.");
        }
    }

    @FXML
    private void handleBack() {
        AppNavigator.showTeacherDashboard();
    }

    private List<Subject> loadTeacherSubjects() {
        List<Subject> subjects = new ArrayList<>();
        Teacher teacher = AppSession.getLoggedInTeacher();
        if (teacher != null) {
            subjects.addAll(service.getSubjectsByDepartment(teacher.departmentId()));
        }
        return subjects;
    }
}

package com.collegequiz.controller;

import com.collegequiz.model.Quiz;
import com.collegequiz.model.Subject;
import com.collegequiz.model.Teacher;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TeacherAddQuestionController extends BaseController {
    @FXML private Label teacherLabel;
    @FXML private ComboBox<Subject> subjectCombo;
    @FXML private ComboBox<Quiz> quizCombo;
    @FXML private TextArea questionTextArea;
    @FXML private TextField marksField;
    @FXML private TextField option1Field;
    @FXML private TextField option2Field;
    @FXML private TextField option3Field;
    @FXML private TextField option4Field;
    @FXML private RadioButton correct1;
    @FXML private RadioButton correct2;
    @FXML private RadioButton correct3;
    @FXML private RadioButton correct4;

    private final ToggleGroup correctGroup = new ToggleGroup();

    @FXML
    private void initialize() {
        Teacher teacher = AppSession.getLoggedInTeacher();
        if (teacher == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        teacherLabel.setText(teacher.teacherCode() + " · " + teacher.name());
        correct1.setToggleGroup(correctGroup);
        correct2.setToggleGroup(correctGroup);
        correct3.setToggleGroup(correctGroup);
        correct4.setToggleGroup(correctGroup);
        correct1.setUserData(1);
        correct2.setUserData(2);
        correct3.setUserData(3);
        correct4.setUserData(4);

        subjectCombo.setItems(FXCollections.observableArrayList(loadTeacherSubjects()));
        subjectCombo.setOnAction(event -> refreshQuizzesForSelectedSubject());
        refreshQuizzesForSelectedSubject();
    }

    @FXML
    private void handleSave() {
        Subject subject = subjectCombo.getValue();
        Quiz quiz = quizCombo.getValue();
        if (subject == null) {
            showError("Missing Subject", "Choose a subject first.");
            return;
        }
        if (quiz == null) {
            showError("Missing Quiz", "Choose a quiz.");
            return;
        }
        if (correctGroup.getSelectedToggle() == null) {
            showError("Missing Correct Option", "Select the correct option.");
            return;
        }

        try {
            String questionText = questionTextArea.getText().trim();
            String marksText = marksField.getText().trim();
            List<String> options = List.of(
                    option1Field.getText().trim(),
                    option2Field.getText().trim(),
                    option3Field.getText().trim(),
                    option4Field.getText().trim()
            );

            if (questionText.isBlank() || marksText.isBlank() || options.stream().anyMatch(String::isBlank)) {
                showError("Incomplete Question", "Question, marks, and all four options are required.");
                return;
            }

            double marks = Double.parseDouble(marksText);
            if (marks <= 0) {
                showError("Invalid Marks", "Marks must be greater than zero.");
                return;
            }

            Integer questionId = service.addQuestion(
                    quiz.quizId(),
                    questionText,
                    marks,
                    service.getQuestionsByQuiz(quiz.quizId()).size() + 1);

            int correctIndex = (Integer) correctGroup.getSelectedToggle().getUserData();
            for (int i = 0; i < options.size(); i++) {
                service.addOption(questionId, options.get(i), i + 1 == correctIndex ? "Y" : "N", i + 1);
            }

            showInfo("Question Added", "Question added successfully.");
            clearForm();
        } catch (NumberFormatException ex) {
            showError("Invalid Marks", "Marks must be numeric.");
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
        subjects.sort(Comparator.comparing(Subject::subjectCode));
        return subjects;
    }

    private void refreshQuizzesForSelectedSubject() {
        Subject subject = subjectCombo.getValue();
        if (subject == null) {
            quizCombo.setItems(FXCollections.emptyObservableList());
            return;
        }

        Teacher teacher = AppSession.getLoggedInTeacher();
        List<Quiz> quizzes = new ArrayList<>();
        for (Quiz quiz : service.getQuizzesBySubject(subject.subjectId())) {
            if (teacher != null && teacher.teacherId().equals(quiz.createdBy())) {
                quizzes.add(quiz);
            }
        }
        quizzes.sort(Comparator.comparing(Quiz::quizTitle));
        quizCombo.setItems(FXCollections.observableArrayList(quizzes));
    }

    private void clearForm() {
        subjectCombo.setValue(null);
        quizCombo.setItems(FXCollections.emptyObservableList());
        questionTextArea.clear();
        marksField.clear();
        option1Field.clear();
        option2Field.clear();
        option3Field.clear();
        option4Field.clear();
        correctGroup.selectToggle(null);
    }
}

package com.collegequiz.controller;

import com.collegequiz.model.Department;
import com.collegequiz.model.Question;
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
        if (AppSession.getLoggedInTeacher() == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        teacherLabel.setText(AppSession.getLoggedInTeacher().teacherCode());
        correct1.setToggleGroup(correctGroup);
        correct2.setToggleGroup(correctGroup);
        correct3.setToggleGroup(correctGroup);
        correct4.setToggleGroup(correctGroup);
        correct1.setUserData(1);
        correct2.setUserData(2);
        correct3.setUserData(3);
        correct4.setUserData(4);
        quizCombo.setItems(FXCollections.observableArrayList(loadTeacherQuizzes()));
    }

    @FXML
    private void handleSave() {
        Quiz quiz = quizCombo.getValue();
        if (quiz == null) {
            showError("Missing Quiz", "Choose a quiz.");
            return;
        }
        if (correctGroup.getSelectedToggle() == null) {
            showError("Missing Correct Option", "Select the correct option.");
            return;
        }

        try {
            double marks = Double.parseDouble(marksField.getText().trim());
            String questionText = questionTextArea.getText().trim();
            List<String> options = List.of(
                    option1Field.getText().trim(),
                    option2Field.getText().trim(),
                    option3Field.getText().trim(),
                    option4Field.getText().trim()
            );

            if (questionText.isBlank() || options.stream().anyMatch(String::isBlank)) {
                showError("Incomplete Question", "Question and all four options are required.");
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

            showInfo("Question Added", "Question ID " + questionId + " created.");
            clearForm();
        } catch (NumberFormatException ex) {
            showError("Invalid Marks", "Marks must be numeric.");
        }
    }

    @FXML
    private void handleBack() {
        AppNavigator.showTeacherDashboard();
    }

    private List<Quiz> loadTeacherQuizzes() {
        List<Quiz> quizzes = new ArrayList<>();
        Teacher teacher = AppSession.getLoggedInTeacher();
        if (teacher != null) {
            for (Subject subject : service.getSubjectsByDepartment(teacher.departmentId())) {
                quizzes.addAll(service.getQuizzesBySubject(subject.subjectId()));
            }
        }
        quizzes.sort(Comparator.comparing(Quiz::quizId));
        return quizzes;
    }

    private void clearForm() {
        questionTextArea.clear();
        marksField.clear();
        option1Field.clear();
        option2Field.clear();
        option3Field.clear();
        option4Field.clear();
        correctGroup.selectToggle(null);
    }
}

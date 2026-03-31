package com.collegequiz.controller;

import com.collegequiz.model.AvailableQuizRow;
import com.collegequiz.model.Department;
import com.collegequiz.model.Student;
import com.collegequiz.model.StudentResultRow;
import com.collegequiz.model.Subject;
import com.collegequiz.model.Quiz;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.ArrayList;
import java.util.List;

public class StudentDashboardController extends BaseController {
    @FXML
    private Label studentNameLabel;
    @FXML
    private Label registrationLabel;
    @FXML
    private Label departmentLabel;
    @FXML
    private Label availableCountLabel;
    @FXML
    private Label resultCountLabel;
    @FXML
    private TableView<AvailableQuizRow> quizTable;
    @FXML
    private TableColumn<AvailableQuizRow, String> quizTitleColumn;
    @FXML
    private TableColumn<AvailableQuizRow, String> subjectColumn;
    @FXML
    private TableColumn<AvailableQuizRow, Number> durationColumn;
    @FXML
    private TableColumn<AvailableQuizRow, Number> marksColumn;
    @FXML
    private TableColumn<AvailableQuizRow, String> publishedColumn;
    @FXML
    private TableView<StudentResultRow> resultTable;
    @FXML
    private TableColumn<StudentResultRow, String> resultQuizTitleColumn;
    @FXML
    private TableColumn<StudentResultRow, Number> resultScoreColumn;
    @FXML
    private TableColumn<StudentResultRow, Number> resultPercentColumn;
    @FXML
    private TableColumn<StudentResultRow, String> resultTimeColumn;

    @FXML
    private void initialize() {
        Student student = AppSession.getLoggedInStudent();
        if (student == null) {
            showError("Session Missing", "No student is logged in.");
            AppNavigator.showLogin();
            return;
        }

        studentNameLabel.setText(student.name());
        registrationLabel.setText(student.registrationNo());
        Department department = service.getDepartments().stream()
                .filter(item -> item.departmentId().equals(student.departmentId()))
                .findFirst()
                .orElse(null);
        departmentLabel.setText(department == null
                ? "Department #" + student.departmentId()
                : department.departmentName());

        quizTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().quizTitle()));
        subjectColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().subjectCode() + " - " + data.getValue().subjectName()));
        durationColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().durationMinutes()));
        marksColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().totalMarks()));
        publishedColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().resultsPublished()));

        resultQuizTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().quizTitle()));
        resultScoreColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().totalScore()));
        resultPercentColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().percentage()));
        resultTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().submissionTime() == null ? "-" : data.getValue().submissionTime().toString()));

        quizTable.setPlaceholder(new Label("No quizzes available right now."));
        resultTable.setPlaceholder(new Label("No published results yet."));
        refreshTables();
    }

    @FXML
    private void handleRefresh() {
        refreshTables();
    }

    @FXML
    private void handleStartQuiz() {
        AvailableQuizRow selected = quizTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select Quiz", "Please choose a quiz first.");
            return;
        }

        try {
            Integer attemptId = service.startAttempt(selected.quizId(), AppSession.getLoggedInStudentId());
            QuizRuntimeContext.setQuizId(selected.quizId());
            QuizRuntimeContext.setAttemptId(attemptId);
            AppNavigator.showQuizAttempt();
        } catch (RuntimeException ex) {
            showError("Start Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleLogout() {
        AppSession.clear();
        QuizRuntimeContext.clear();
        AppNavigator.showLogin();
    }

    private void refreshTables() {
        Student student = AppSession.getLoggedInStudent();
        List<AvailableQuizRow> availableQuizzes = new ArrayList<>();
        try {
            List<Subject> subjects = service.getSubjectsByDepartment(student.departmentId());
            for (Subject subject : subjects) {
                List<Quiz> quizzes = service.getQuizzesBySubject(subject.subjectId());
                for (Quiz quiz : quizzes) {
                    String attemptStatus = service.getAttemptStatus(quiz.quizId(), student.studentId());
                    if ("SUBMITTED".equals(attemptStatus) || "AUTO_SUBMITTED".equals(attemptStatus)) {
                        continue;
                    }
                    availableQuizzes.add(new AvailableQuizRow(
                            quiz.quizId(),
                            quiz.quizTitle(),
                            subject.subjectCode(),
                            subject.subjectName(),
                            quiz.durationMinutes(),
                            quiz.totalMarks(),
                            quiz.quizDate(),
                            quiz.resultsPublished(),
                            subject.subjectId()
                    ));
                }
            }
            List<StudentResultRow> publishedResults = service.getPublishedResults(student.studentId());
            availableCountLabel.setText(String.valueOf(availableQuizzes.size()));
            resultCountLabel.setText(String.valueOf(publishedResults.size()));
            quizTable.setItems(FXCollections.observableArrayList(availableQuizzes));
            resultTable.setItems(FXCollections.observableArrayList(publishedResults));
        } catch (RuntimeException ex) {
            showError("Load Failed", ex.getMessage());
            availableCountLabel.setText("0");
            resultCountLabel.setText("0");
            quizTable.setItems(FXCollections.emptyObservableList());
            resultTable.setItems(FXCollections.emptyObservableList());
        }
    }
}

package com.collegequiz.controller;

import com.collegequiz.model.AvailableQuizRow;
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
    private TableView<AvailableQuizRow> quizTable;
    @FXML
    private TableColumn<AvailableQuizRow, Number> quizIdColumn;
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
    private TableColumn<StudentResultRow, Number> resultQuizIdColumn;
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
        departmentLabel.setText("Department ID: " + student.departmentId());

        quizIdColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().quizId()));
        quizTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().quizTitle()));
        subjectColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().subjectCode() + " - " + data.getValue().subjectName()));
        durationColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().durationMinutes()));
        marksColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().totalMarks()));
        publishedColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().resultsPublished()));

        resultQuizIdColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().quizId()));
        resultQuizTitleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().quizTitle()));
        resultScoreColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().totalScore()));
        resultPercentColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().percentage()));
        resultTimeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().submissionTime() == null ? "-" : data.getValue().submissionTime().toString()));

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

        Integer attemptId = service.startAttempt(selected.quizId(), AppSession.getLoggedInStudentId());
        QuizRuntimeContext.setQuizId(selected.quizId());
        QuizRuntimeContext.setAttemptId(attemptId);
        AppNavigator.showQuizAttempt();
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
        List<Subject> subjects = service.getSubjectsByDepartment(student.departmentId());
        for (Subject subject : subjects) {
            List<Quiz> quizzes = service.getQuizzesBySubject(subject.subjectId());
            for (Quiz quiz : quizzes) {
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

        quizTable.setItems(FXCollections.observableArrayList(availableQuizzes));
        resultTable.setItems(FXCollections.observableArrayList(service.getPublishedResults(student.studentId())));
    }
}

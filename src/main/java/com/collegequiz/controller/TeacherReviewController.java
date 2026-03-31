package com.collegequiz.controller;

import com.collegequiz.model.TeacherDashboardRow;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class TeacherReviewController extends BaseController {
    @FXML private Label teacherLabel;
    @FXML private TableView<TeacherDashboardRow> dashboardTable;
    @FXML private TableColumn<TeacherDashboardRow, String> titleColumn;
    @FXML private TableColumn<TeacherDashboardRow, String> subjectColumn;
    @FXML private TableColumn<TeacherDashboardRow, Number> attemptsColumn;
    @FXML private TableColumn<TeacherDashboardRow, Number> marksColumn;
    @FXML private TableColumn<TeacherDashboardRow, String> publishedColumn;
    @FXML private Button publishButton;
    @FXML private Label totalQuizzesLabel;
    @FXML private Label publishedQuizzesLabel;
    @FXML private Label draftQuizzesLabel;

    @FXML
    private void initialize() {
        if (AppSession.getLoggedInTeacher() == null) {
            showError("Session Missing", "No teacher is logged in.");
            AppNavigator.showLogin();
            return;
        }

        var teacher = AppSession.getLoggedInTeacher();
        teacherLabel.setText(teacher.teacherCode() + " · " + teacher.name());
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().quizTitle()));
        subjectColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().subjectCode() + " - " + data.getValue().subjectName()));
        attemptsColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().attemptCount()));
        marksColumn.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().totalMarks()));
        publishedColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().resultsPublished()));

        dashboardTable.setPlaceholder(new Label("No quizzes are available for publishing yet."));
        dashboardTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> updatePublishButtonState());
        refreshTable();
    }

    @FXML
    private void handleRefresh() {
        refreshTable();
    }

    @FXML
    private void handlePublish() {
        TeacherDashboardRow row = dashboardTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            showError("Select Quiz", "Choose a quiz first.");
            return;
        }
        if (row.attemptCount() == null || row.attemptCount() <= 0) {
            showError("Cannot Publish Yet", "This quiz has no attempts yet. Wait for at least one student attempt before publishing.");
            return;
        }
        try {
            service.publishResults(row.quizId(), AppSession.getLoggedInTeacherId());
            refreshTable();
        } catch (RuntimeException ex) {
            showError("Publish Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleUnpublish() {
        TeacherDashboardRow row = dashboardTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            showError("Select Quiz", "Choose a quiz first.");
            return;
        }
        try {
            service.unpublishResults(row.quizId(), AppSession.getLoggedInTeacherId());
            refreshTable();
        } catch (RuntimeException ex) {
            showError("Unpublish Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleDeleteQuiz() {
        TeacherDashboardRow row = dashboardTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            showError("Select Quiz", "Choose a quiz first.");
            return;
        }
        if (!confirm("Delete Quiz", "This will permanently remove the quiz, questions, attempts, and results. Continue?")) {
            return;
        }
        try {
            service.removeQuiz(row.quizId(), AppSession.getLoggedInTeacherId());
            refreshTable();
            showInfo("Quiz Removed", "The quiz was deleted.");
        } catch (RuntimeException ex) {
            showError("Delete Failed", ex.getMessage());
        }
    }

    @FXML
    private void handleBack() {
        AppNavigator.showTeacherDashboard();
    }

    private void refreshTable() {
        try {
            var rows = service.getTeacherDashboard(AppSession.getLoggedInTeacherId());
            int published = 0;
            for (TeacherDashboardRow row : rows) {
                if ("Y".equalsIgnoreCase(row.resultsPublished())) {
                    published++;
                }
            }
            dashboardTable.setItems(FXCollections.observableArrayList(rows));
            totalQuizzesLabel.setText(String.valueOf(rows.size()));
            publishedQuizzesLabel.setText(String.valueOf(published));
            draftQuizzesLabel.setText(String.valueOf(Math.max(0, rows.size() - published)));
            updatePublishButtonState();
        } catch (RuntimeException ex) {
            dashboardTable.setItems(FXCollections.emptyObservableList());
            totalQuizzesLabel.setText("0");
            publishedQuizzesLabel.setText("0");
            draftQuizzesLabel.setText("0");
            updatePublishButtonState();
            showError("Load Failed", ex.getMessage());
        }
    }

    private void updatePublishButtonState() {
        if (publishButton == null || dashboardTable == null) {
            return;
        }
        TeacherDashboardRow row = dashboardTable.getSelectionModel().getSelectedItem();
        publishButton.setDisable(row == null || row.attemptCount() == null || row.attemptCount() <= 0);
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

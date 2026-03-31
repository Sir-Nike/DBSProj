package com.collegequiz.controller;

import com.collegequiz.model.TeacherDashboardRow;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class TeacherReviewController extends BaseController {
    @FXML private Label teacherLabel;
    @FXML private TableView<TeacherDashboardRow> dashboardTable;
    @FXML private TableColumn<TeacherDashboardRow, String> titleColumn;
    @FXML private TableColumn<TeacherDashboardRow, String> subjectColumn;
    @FXML private TableColumn<TeacherDashboardRow, Number> attemptsColumn;
    @FXML private TableColumn<TeacherDashboardRow, Number> marksColumn;
    @FXML private TableColumn<TeacherDashboardRow, String> publishedColumn;
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
        } catch (RuntimeException ex) {
            dashboardTable.setItems(FXCollections.emptyObservableList());
            totalQuizzesLabel.setText("0");
            publishedQuizzesLabel.setText("0");
            draftQuizzesLabel.setText("0");
            showError("Load Failed", ex.getMessage());
        }
    }
}

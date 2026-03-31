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
        service.publishResults(row.quizId(), AppSession.getLoggedInTeacherId());
        refreshTable();
    }

    @FXML
    private void handleUnpublish() {
        TeacherDashboardRow row = dashboardTable.getSelectionModel().getSelectedItem();
        if (row == null) {
            showError("Select Quiz", "Choose a quiz first.");
            return;
        }
        service.unpublishResults(row.quizId(), AppSession.getLoggedInTeacherId());
        refreshTable();
    }

    @FXML
    private void handleBack() {
        AppNavigator.showTeacherDashboard();
    }

    private void refreshTable() {
        dashboardTable.setItems(FXCollections.observableArrayList(
                service.getTeacherDashboard(AppSession.getLoggedInTeacherId())));
    }
}

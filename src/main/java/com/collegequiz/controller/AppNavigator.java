package com.collegequiz.controller;

import com.collegequiz.QuizManagementApp;
import javafx.animation.FadeTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public final class AppNavigator {
    private static Stage primaryStage;

    private AppNavigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void showLogin() {
        loadAndShow("/com/collegequiz/view/auth-choice.fxml", "College Quiz Management System", 1120, 720);
    }

    public static void showTeacherLogin() {
        loadAndShow("/com/collegequiz/view/teacher-login.fxml", "Teacher Login", 1080, 680);
    }

    public static void showStudentLogin() {
        loadAndShow("/com/collegequiz/view/student-login.fxml", "Student Login", 1080, 680);
    }

    public static void showTeacherDashboard() {
        loadAndShow("/com/collegequiz/view/teacher-home.fxml", "Teacher Home", 1140, 720);
    }

    public static void showStudentDashboard() {
        loadAndShow("/com/collegequiz/view/student-dashboard.fxml", "Student Dashboard", 1360, 820);
    }

    public static void showQuizAttempt() {
        loadAndShow("/com/collegequiz/view/quiz-attempt.fxml", "Quiz Attempt", 1080, 740);
    }

    public static void showTeacherCreateSubject() {
        loadAndShow("/com/collegequiz/view/teacher-create-subject.fxml", "Create Subject", 960, 660);
    }

    public static void showTeacherCreateQuiz() {
        loadAndShow("/com/collegequiz/view/teacher-create-quiz.fxml", "Create Quiz", 960, 660);
    }

    public static void showTeacherAddQuestion() {
        loadAndShow("/com/collegequiz/view/teacher-add-question.fxml", "Add Question", 1080, 760);
    }

    public static void showTeacherReview() {
        loadAndShow("/com/collegequiz/view/teacher-review.fxml", "Teacher Review", 1240, 780);
    }

    private static void loadAndShow(String resourcePath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(QuizManagementApp.class.getResource(resourcePath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(QuizManagementApp.class.getResource("/com/collegequiz/css/app.css").toExternalForm());
            scene.getStylesheets().add(QuizManagementApp.class.getResource(ThemeManager.getThemeStylesheet()).toExternalForm());
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
            FadeTransition fade = new FadeTransition(Duration.millis(180), root);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.playFromStart();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load UI: " + resourcePath, ex);
        }
    }

    public static void applyTheme() {
        if (primaryStage == null || primaryStage.getScene() == null) {
            return;
        }
        Scene scene = primaryStage.getScene();
        scene.getStylesheets().clear();
        scene.getStylesheets().add(QuizManagementApp.class.getResource("/com/collegequiz/css/app.css").toExternalForm());
        scene.getStylesheets().add(QuizManagementApp.class.getResource(ThemeManager.getThemeStylesheet()).toExternalForm());
    }
}

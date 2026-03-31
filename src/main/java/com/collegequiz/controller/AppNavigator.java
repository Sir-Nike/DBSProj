package com.collegequiz.controller;

import com.collegequiz.QuizManagementApp;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public final class AppNavigator {
    private static Stage primaryStage;

    private AppNavigator() {
    }

    public static void init(Stage stage) {
        primaryStage = stage;
    }

    public static void showLogin() {
        loadAndShow("/com/collegequiz/view/auth-choice.fxml", "College Quiz Management System", 920, 620);
    }

    public static void showTeacherLogin() {
        loadAndShow("/com/collegequiz/view/teacher-login.fxml", "Teacher Login", 820, 560);
    }

    public static void showStudentLogin() {
        loadAndShow("/com/collegequiz/view/student-login.fxml", "Student Login", 820, 560);
    }

    public static void showTeacherDashboard() {
        loadAndShow("/com/collegequiz/view/teacher-home.fxml", "Teacher Home", 960, 620);
    }

    public static void showStudentDashboard() {
        loadAndShow("/com/collegequiz/view/student-dashboard.fxml", "Student Dashboard", 1120, 700);
    }

    public static void showQuizAttempt() {
        loadAndShow("/com/collegequiz/view/quiz-attempt.fxml", "Quiz Attempt", 960, 640);
    }

    public static void showTeacherCreateSubject() {
        loadAndShow("/com/collegequiz/view/teacher-create-subject.fxml", "Create Subject", 780, 560);
    }

    public static void showTeacherCreateQuiz() {
        loadAndShow("/com/collegequiz/view/teacher-create-quiz.fxml", "Create Quiz", 780, 560);
    }

    public static void showTeacherAddQuestion() {
        loadAndShow("/com/collegequiz/view/teacher-add-question.fxml", "Add Question", 920, 660);
    }

    public static void showTeacherReview() {
        loadAndShow("/com/collegequiz/view/teacher-review.fxml", "Teacher Review", 1040, 640);
    }

    private static void loadAndShow(String resourcePath, String title, double width, double height) {
        try {
            FXMLLoader loader = new FXMLLoader(QuizManagementApp.class.getResource(resourcePath));
            Parent root = loader.load();
            Scene scene = new Scene(root, width, height);
            scene.getStylesheets().add(QuizManagementApp.class.getResource("/com/collegequiz/css/app.css").toExternalForm());
            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException ex) {
            throw new IllegalStateException("Unable to load UI: " + resourcePath, ex);
        }
    }
}

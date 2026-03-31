package com.collegequiz;

import com.collegequiz.controller.AppNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class QuizManagementApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppNavigator.init(primaryStage);
        AppNavigator.showLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

package com.collegequiz.controller;

import com.collegequiz.model.Question;
import com.collegequiz.model.QuestionOption;
import com.collegequiz.model.Quiz;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.List;

public class QuizAttemptController extends BaseController {
    @FXML private Label quizTitleLabel;
    @FXML private Label quizMetaLabel;
    @FXML private Label timerLabel;
    @FXML private Label progressLabel;
    @FXML private Label questionTextLabel;
    @FXML private VBox optionsContainer;

    private Timeline timer;
    private int remainingSeconds;
    private List<Question> questions;
    private int currentQuestionIndex;
    private Integer attemptId;
    private boolean submitted;

    @FXML
    private void initialize() {
        Integer quizId = QuizRuntimeContext.getQuizId();
        attemptId = QuizRuntimeContext.getAttemptId();

        if (quizId == null || attemptId == null) {
            showError("Missing Quiz Context", "Quiz attempt context is missing.");
            AppNavigator.showStudentDashboard();
            return;
        }

        Quiz quiz = service.getQuizById(quizId);
        if (quiz == null) {
            showError("Quiz Not Found", "The selected quiz could not be loaded.");
            AppNavigator.showStudentDashboard();
            return;
        }

        quizTitleLabel.setText(quiz.quizTitle());
        quizMetaLabel.setText("Quiz #" + quiz.quizId() + " | Total Marks: " + quiz.totalMarks());
        remainingSeconds = quiz.durationMinutes() * 60;
        questions = service.getQuestionsByQuiz(quizId);
        if (questions == null || questions.isEmpty()) {
            showError("Quiz Not Ready", "This quiz has no questions yet. Please try again later.");
            AppNavigator.showStudentDashboard();
            return;
        }
        currentQuestionIndex = 0;
        updateTimerLabel();
        showQuestion();
        startTimer();
    }

    @FXML
    private void handlePrev() {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            showQuestion();
        }
    }

    @FXML
    private void handleNext() {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        if (currentQuestionIndex < questions.size() - 1) {
            currentQuestionIndex++;
            showQuestion();
        }
    }

    @FXML
    private void handleSubmit() {
        submitNow(false);
    }

    @FXML
    private void handleBackToDashboard() {
        stopTimer();
        QuizRuntimeContext.clear();
        AppNavigator.showStudentDashboard();
    }

    private void showQuestion() {
        if (questions == null || questions.isEmpty()) {
            return;
        }
        Question question = questions.get(currentQuestionIndex);
        progressLabel.setText("Question " + (currentQuestionIndex + 1) + " of " + questions.size());
        questionTextLabel.setText(question.displayOrder() + ". " + question.questionText());
        optionsContainer.getChildren().clear();

        ToggleGroup group = new ToggleGroup();
        for (QuestionOption option : service.getOptionsByQuestion(question.questionId())) {
            RadioButton radioButton = new RadioButton(option.optionText());
            radioButton.setToggleGroup(group);
            radioButton.setWrapText(true);
            radioButton.setUserData(option.optionId());
            radioButton.setOnAction(event -> {
                Integer selectedOptionId = (Integer) radioButton.getUserData();
                try {
                    service.autosaveAnswer(attemptId, question.questionId(), selectedOptionId);
                } catch (RuntimeException ex) {
                    showError("Autosave Failed", ex.getMessage());
                }
            });
            optionsContainer.getChildren().add(radioButton);
        }

        Integer selectedOptionId = service.getSelectedOptionId(attemptId, question.questionId());
        if (selectedOptionId != null) {
            group.getToggles().stream()
                    .filter(toggle -> selectedOptionId.equals(toggle.getUserData()))
                    .findFirst()
                    .ifPresent(toggle -> group.selectToggle(toggle));
        }
    }

    private void startTimer() {
        timer = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            remainingSeconds--;
            updateTimerLabel();
            if (remainingSeconds <= 0) {
                submitNow(true);
            }
        }));
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    private void updateTimerLabel() {
        int minutes = Math.max(0, remainingSeconds) / 60;
        int seconds = Math.max(0, remainingSeconds) % 60;
        timerLabel.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
    }

    private void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    private void submitNow(boolean auto) {
        if (submitted) {
            return;
        }
        submitted = true;
        stopTimer();
        try {
            service.submitAttempt(attemptId);
            String message = auto
                    ? "Time is over. Your attempt has been submitted. Results will appear after the teacher publishes them."
                    : "Your attempt has been submitted. Results will appear after the teacher publishes them.";
            showInfo(auto ? "Auto Submitted" : "Submitted", message);
            QuizRuntimeContext.clear();
            AppNavigator.showStudentDashboard();
        } catch (RuntimeException ex) {
            submitted = false;
            showError("Submit Failed", ex.getMessage());
        }
    }
}

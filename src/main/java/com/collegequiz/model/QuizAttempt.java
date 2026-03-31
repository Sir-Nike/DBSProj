package com.collegequiz.model;

import java.time.LocalDateTime;

public record QuizAttempt(
        Integer attemptId,
        LocalDateTime submissionTime,
        Double totalScore,
        Integer quizId,
        Integer studentId,
        LocalDateTime startTime,
        LocalDateTime lastSavedAt,
        String status
) {
}
